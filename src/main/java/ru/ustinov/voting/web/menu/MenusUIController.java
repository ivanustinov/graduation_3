package ru.ustinov.voting.web.menu;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.ustinov.voting.to.MenusTo;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Ivan Ustinov(ivanustinov1985@yandex.ru)
 * @version 1.0
 * @since 25.10.2021
 */
@RestController
@RequestMapping(value = "/admin/menusList", produces = MediaType.APPLICATION_JSON_VALUE)
public class MenusUIController extends AbstractMenusController{

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
    public void copy(@RequestParam LocalDate date) {
        super.copy(date);
    }
}
