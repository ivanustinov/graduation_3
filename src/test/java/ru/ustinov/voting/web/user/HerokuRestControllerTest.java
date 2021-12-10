package ru.ustinov.voting.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.ustinov.voting.web.AbstractControllerTest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.ustinov.voting.Profiles.*;
import static ru.ustinov.voting.TestUtil.userHttpBasic;
import static ru.ustinov.voting.error.UpdateRestrictionException.EXCEPTION_UPDATE_RESTRICTION;
import static ru.ustinov.voting.web.user.UserTestData.*;

/**
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 10.12.2021
 */
@ActiveProfiles({HEROKU, POSTGRES_DB})
public class HerokuRestControllerTest extends AbstractControllerTest {

    private static final String REST_URL = AdminRestUserController.REST_URL + '/';

    @Test
    void delete() throws Exception {
        perform(MockMvcRequestBuilders.delete(REST_URL + USER_ID)
                .with(userHttpBasic(admin)))
                .andDo(print())
                .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage(EXCEPTION_UPDATE_RESTRICTION)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        perform(MockMvcRequestBuilders.put(REST_URL + USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(admin))
                .content(UserTestData.jsonWithPassword(user, "password")))
                .andExpect(jsonPath("$.message").value(messageSourceAccessor.getMessage(EXCEPTION_UPDATE_RESTRICTION)))
                .andExpect(status().isUnprocessableEntity());
    }
}
