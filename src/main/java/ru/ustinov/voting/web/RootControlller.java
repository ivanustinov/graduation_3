package ru.ustinov.voting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ustinov.voting.service.RestaurantService;

import java.time.LocalDate;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 11.10.2021
 */

@Controller
public class RootControlller {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/")
    public String root() {
        return "redirect:/voting";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/voting")
    public String voting(ModelMap modelMap) {
        modelMap.addAttribute("restaurants", restaurantService.getWithDishes(LocalDate.now()));
        return "voting";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String getUsers() {
        return "users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/menusList")
    public String getMenusList() {
        return "menusList";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/menus/{date}")
    public String getMenus(ModelMap modelMap, @PathVariable LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        modelMap.addAttribute("date", date);
        modelMap.addAttribute("restaurants", restaurantService.getWithDishes(date));
        return "menus";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dishes/{restaurant_id}/{date}")
    public String getDishes(ModelMap modelMap, @PathVariable int restaurant_id, @PathVariable LocalDate date) {
        modelMap.addAttribute("restaurant", restaurantService.getRestaurant(restaurant_id));
        modelMap.addAttribute("date", date);
        return "dishes";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/restaurants")
    public String getRestaurants() {
        return "restaurants";
    }

}
