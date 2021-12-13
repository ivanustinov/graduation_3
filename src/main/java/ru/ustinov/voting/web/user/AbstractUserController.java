package ru.ustinov.voting.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.ustinov.voting.Profiles;
import ru.ustinov.voting.error.UpdateRestrictionException;
import ru.ustinov.voting.model.BaseEntity;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.repository.UserRepository;
import ru.ustinov.voting.util.UserUtil;
import ru.ustinov.voting.util.validation.Util;
import ru.ustinov.voting.util.validation.ValidationUtil;
import ru.ustinov.voting.web.AuthUser;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractUserController {

    @Autowired
    protected UserRepository repository;

    @Autowired
    private UniqueMailUserToValidator uniqueMailUserToValidator;

    @Autowired
    private UniqueMailUserValidator uniqueMailUserValidator;

    @Autowired
    private SessionRegistry sessionRegistry;

    private boolean modificationRestriction;

    @Autowired
    @SuppressWarnings("deprecation")
    public void setEnvironment(Environment environment) {
        modificationRestriction = environment.acceptsProfiles(Profiles.HEROKU);
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        final Optional<Object> validator = Optional.ofNullable(binder.getTarget()).filter(field -> User.class.isAssignableFrom(field.getClass()));
        if (validator.isPresent()) {
            binder.addValidators(uniqueMailUserValidator);
        } else {
            binder.addValidators(uniqueMailUserToValidator);
        }
    }

    public ResponseEntity<User> get(int id) {
        log.info("get {}", id);
        return ResponseEntity.of(repository.findById(id));
    }

    public List<User> getAll() {
        log.info("getAll");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name", "email"));
    }

    @CacheEvict(value = "users", key = "#user.email")
    public User create(User user) {
        log.info("create {}", user);
        return prepareAndSave(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void update(User user, int id) {
        expireSessions(id);
        checkModificationAllowed(id);
        ValidationUtil.assureIdConsistent(user, id);
        log.info("update {} with id={}", user, id);
        prepareAndSave(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(int id) {
        expireSessions(id);
        checkModificationAllowed(id);
        log.info("delete {}", id);
        repository.deleteExisted(id);
    }

    private void expireSessions(int id) {
        final User user = Util.getEntity(repository.get(id), "error.entityWithIdNotFound", String.valueOf(id));
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        final List<AuthUser> authUsers = allPrincipals.stream()
                        .map(o -> (AuthUser) o).filter(authUser -> authUser.getUser().getEmail().equals(user.getEmail())).toList();
        if (!authUsers.isEmpty()) {
            final List<SessionInformation> allSessions = sessionRegistry.getAllSessions(authUsers.get(0), false);
            for (SessionInformation allSession : allSessions) {
                allSession.expireNow();
            }
        }
    }

    public ResponseEntity<User> getByEmail(String email) {
        log.info("getByEmail {}", email);
        return ResponseEntity.of(repository.getByEmail(email));
    }

    protected User prepareAndSave(User user) {
        return repository.save(UserUtil.prepareToSave(user));
    }

    protected void checkModificationAllowed(int id) {
        if (modificationRestriction && id < BaseEntity.START_SEQ + 2) {
            throw new UpdateRestrictionException();
        }
    }
}