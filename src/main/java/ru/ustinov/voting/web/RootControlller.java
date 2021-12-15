package ru.ustinov.voting.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ustinov.voting.service.DishServise;
import ru.ustinov.voting.service.RestaurantService;
import ru.ustinov.voting.web.formatter.DateFormatter;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 11.10.2021
 */

@ApiIgnore
@Controller
public class RootControlller {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private DishServise dishServise;

    @Autowired
    private Environment environment;

    @GetMapping("/")
    public String root() {
        return "redirect:/voting";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/voting")
    public String voting(ModelMap modelMap, TimeZone timeZone) {
        modelMap.addAttribute("restaurants", restaurantService.getWithDishes(LocalDate.now(timeZone.toZoneId()), true));
        return "voting";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public String getUsers() {
        return "users";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/menusList")
    public String getMenusList(ModelMap modelMap) {
        final List<String> activeProfiles = Arrays.stream(environment.getActiveProfiles()).toList();
        if (activeProfiles.contains("ru_date_format")) {
            modelMap.addAttribute("dateFormat", "ru_date_format");
        }
        return "menusList";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/menus")
    public String getMenus(ModelMap modelMap, @ModelAttribute("date") LocalDate date) {
        modelMap.addAttribute("restaurants", restaurantService.getWithDishes(date, false));
        return "menus";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete_all_dishes")
    public String deleteAll(@RequestParam LocalDate date, @RequestParam int restaurant_id) {
        dishServise.deleteAllByRestaurantAndDate(restaurant_id, date);
        return "redirect:/menus?date=" + DateFormatter.format(date);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dishes/{restaurant_id}")
    public String getDishes(ModelMap modelMap, @PathVariable int restaurant_id, @ModelAttribute("date") LocalDate date) {
        modelMap.addAttribute("restaurant", restaurantService.getRestaurant(restaurant_id));
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
