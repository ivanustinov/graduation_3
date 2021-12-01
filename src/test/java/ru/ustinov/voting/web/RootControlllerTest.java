package ru.ustinov.voting.web;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.ustinov.voting.TestUtil.userAuth;
import static ru.ustinov.voting.web.restaurant.RestaurantTestData.RESTAURAUNT_HARBIN_ID;
import static ru.ustinov.voting.web.user.UserTestData.admin;
import static ru.ustinov.voting.web.user.UserTestData.user;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 18.11.2021
 */
class RootControlllerTest extends AbstractControllerTest {

    @Test
    void voting() throws Exception {
        perform(MockMvcRequestBuilders.get("/voting")
                .with(userAuth(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("voting"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/voting.jsp"));
    }

    @Test
    void getUsers() throws Exception {
        perform(MockMvcRequestBuilders.get("/users")
                .with(userAuth(admin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/users.jsp"));
    }

    @Test
    void getUsersForbidden() throws Exception {
        perform(MockMvcRequestBuilders.get("/users")
                .with(userAuth(user)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void unAuth() throws Exception {
        perform(get("/users"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void getMenusList() throws Exception {
        perform(MockMvcRequestBuilders.get("/menusList")
                .with(userAuth(admin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("menusList"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/menusList.jsp"));
    }

    @Test
    void getMenus() throws Exception {
        perform(MockMvcRequestBuilders.get("/menus")
                .with(userAuth(admin))
                .param("date", String.valueOf(LocalDate.now())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("menus"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/menus.jsp"));
    }

    @Test
    void getDishes() throws Exception {
        perform(MockMvcRequestBuilders.get("/dishes/" + RESTAURAUNT_HARBIN_ID + "/" + LocalDate.now())
                .with(userAuth(admin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("dishes"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/dishes.jsp"));
    }

    @Test
    void getRestaurants() throws Exception {
        perform(MockMvcRequestBuilders.get("/restaurants")
                .with(userAuth(admin)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("restaurants"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/restaurants.jsp"));
    }
}