package ru.ustinov.voting.web.menu;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ustinov.voting.to.MenusTo;

import java.time.LocalDate;
import java.util.List;

import static ru.ustinov.voting.web.menu.MenusRestController.REST_URL;

/**
 * //TODO add comments.
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 17.11.2021
 */
@Tag(name = "Menu Controller")
@RestController
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MenusRestController extends AbstractMenusController {

    public final static String REST_URL = "/rest/admin/menusList";

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestParam LocalDate date) {
        super.delete(date);
    }

    @GetMapping
    public List<MenusTo> getAll() {
        return super.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void copy(@RequestParam LocalDate date) {
        super.copy(date);
    }

}
