package ru.ustinov.voting.web.menu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.service.MenuServise;
import ru.ustinov.voting.web.AbstractControllerTest;
import ru.ustinov.voting.web.formatter.DateFormatter;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.ustinov.voting.web.menu.MenusTestData.*;
import static ru.ustinov.voting.web.user.UserTestData.ADMIN_MAIL;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 17.11.2021
 */
@WithUserDetails(ADMIN_MAIL)
class MenusRestControllerTest extends AbstractControllerTest {

    @Autowired
    private MenuServise menuServise;

    @Test
    void getAll() throws Exception {
        perform(MockMvcRequestBuilders.get(MenusRestController.REST_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(MenusTestData.MENUS_TO_MATCHER.contentJson(MENUS_TO_NOW, MENUS_TO_2015_04_16))
                .andDo(print());
    }

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(MenusRestController.REST_URL)
                .param("date", DateFormatter.format(LocalDate.now())))
                .andExpect(status().isNoContent());
        assertEquals(List.of(MENUS_TO_2015_04_16), menuServise.getAll());
    }

    @Test
    void copy() throws Exception {
        perform(MockMvcRequestBuilders.post(MenusRestController.REST_URL)
                .param("date", DateFormatter.format(LocalDate.now())))
                .andExpect(status().isCreated());
        assertEquals(menuServise.getAll(), List.of(MENUS_TO_COPIED, MENUS_TO_NOW,  MENUS_TO_2015_04_16));
    }
}