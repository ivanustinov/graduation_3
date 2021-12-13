package ru.ustinov.voting.web.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.ustinov.voting.model.Role;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.to.UserTo;
import ru.ustinov.voting.util.UserUtil;
import ru.ustinov.voting.web.AuthUser;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.net.URI;
import java.util.EnumSet;

@RestController
@RequestMapping(value = ProfileRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Tag(name = "Profile Controller")
public class ProfileRestController extends AbstractUserController {

    @Autowired
    ProfileRestController profileRestController;

    static final String REST_URL = "/rest/profile";

    @GetMapping
    public User get(@AuthenticationPrincipal @ApiIgnore AuthUser authUser) {
        return authUser.getUser();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<User> register(@Valid @RequestBody UserTo userTo) {
        log.info("register {}", userTo);
        final User created = profileRestController.create(UserUtil.createNewFromTo(userTo));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL).build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CacheEvict(value = "users", key = "#authUser.user.email")
    public void update(@RequestBody @Valid UserTo userTo, @AuthenticationPrincipal @ApiIgnore AuthUser authUser) {
        log.info("updating user {} by new userTo {}", authUser.getUser(), userTo);
        User user = authUser.getUser();
        super.update(UserUtil.updateFromTo(user, userTo), user.id());
    }
}