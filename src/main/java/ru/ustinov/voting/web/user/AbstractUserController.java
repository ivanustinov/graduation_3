package ru.ustinov.voting.web.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import ru.ustinov.voting.Profiles;
import ru.ustinov.voting.error.UpdateRestrictionException;
import ru.ustinov.voting.model.BaseEntity;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.repository.UserRepository;
import ru.ustinov.voting.to.UserTo;
import ru.ustinov.voting.util.UserUtil;
import ru.ustinov.voting.util.validation.ValidationUtil;

import java.util.List;

import static ru.ustinov.voting.util.validation.ValidationUtil.checkNew;

@Slf4j
public abstract class AbstractUserController {

    @Autowired
    protected UserRepository repository;

    @Autowired
    private UniqueMailValidator emailValidator;

    private boolean modificationRestriction;

    @Autowired
    @SuppressWarnings("deprecation")
    public void setEnvironment(Environment environment) {
        modificationRestriction = environment.acceptsProfiles(Profiles.HEROKU);
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(emailValidator);
    }

    public ResponseEntity<User> get(int id) {
        log.info("get {}", id);
        return ResponseEntity.of(repository.findById(id));
    }

    public List<User> getAll() {
        log.info("getAll");
        return repository.findAll(Sort.by(Sort.Direction.ASC, "name", "email"));
    }

    public User create(User user) {
        log.info("create {}", user);
        return prepareAndSave(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void update(User user, int id) {
        checkModificationAllowed(id);
        log.info("update {} with id={}", user, id);
        ValidationUtil.assureIdConsistent(user, id);
        prepareAndSave(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void delete(int id) {
        checkModificationAllowed(id);
        log.info("delete {}", id);
        repository.deleteExisted(id);
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