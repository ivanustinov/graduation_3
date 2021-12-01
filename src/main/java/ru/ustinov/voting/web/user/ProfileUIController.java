package ru.ustinov.voting.web.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import ru.ustinov.voting.model.User;
import ru.ustinov.voting.to.UserTo;
import ru.ustinov.voting.util.UserUtil;
import ru.ustinov.voting.web.AuthUser;

import javax.validation.Valid;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 12.10.2021
 */
@Controller
@RequestMapping("/profile")
public class ProfileUIController extends AbstractUserController{

    @GetMapping
    public String profile(ModelMap modelMap, @AuthenticationPrincipal AuthUser authUser) {
        modelMap.addAttribute("user", authUser.getUser());
        return "profile";
    }

    @PostMapping
    public String updateProfile(@Valid @ModelAttribute("user") UserTo userTo, BindingResult result, SessionStatus status,
                                @AuthenticationPrincipal AuthUser authUser) {
        if (result.hasErrors()) {
            return "profile";
        }
        User user = authUser.getUser();
        final User userUpdated = UserUtil.updateFromTo(user, userTo);
        super.update(userUpdated, authUser.id());
        authUser.setUser(user);
        status.setComplete();
        return "redirect:/voting";
    }

    @GetMapping("/register")
    public String register(ModelMap model) {
        model.addAttribute("user", new User());
        model.addAttribute("register", true);
        return "profile";
    }

    @PostMapping("/register")
    public String saveRegister(@Valid UserTo userTo, BindingResult result, SessionStatus status, ModelMap model) {
        if (result.hasErrors()) {
            model.addAttribute("register", true);
            return "profile";
        }
        User user = UserUtil.createNewFromTo(userTo);
        super.create(user);
        status.setComplete();
        return "redirect:/login?message=app.registered&username=" + userTo.getEmail();
    }

}
