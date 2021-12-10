package ru.ustinov.voting.util.validation;

import lombok.experimental.UtilityClass;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.lang.NonNull;
import ru.ustinov.voting.HasId;
import ru.ustinov.voting.error.IllegalRequestDataException;
import ru.ustinov.voting.error.NotFoundException;

@UtilityClass
public class ValidationUtil {

    public static <T> T checkNotFoundWithId(T object, int id) {
        checkNotFoundWithId(object == null, id);
        return object;
    }

    public static void checkNotFoundWithId(boolean found, int id) {
        checkNotFound(found, id);
    }

    public static void checkNotFound(boolean found, int id) {
        if (found) {
            throw new NotFoundException("error.entityWithIdNotFound", String.valueOf(id));
        }
    }

    public static void checkNew(HasId bean) {
        if (!bean.isNew()) {
            throw new IllegalRequestDataException("error.entityMustBeNew", bean.getClass().getSimpleName());
        }
    }

    //  Conservative when you reply, but accept liberally (http://stackoverflow.com/a/32728226/548473)
    public static void assureIdConsistent(HasId bean, int id) {
        if (bean.isNew()) {
            bean.setId(id);
        } else if (bean.id() != id) {
            throw new IllegalRequestDataException("error.entityMustHaveId", bean.getClass().getSimpleName(), String.valueOf(id));
        }
    }

    public static void checkModification(int count, int id) {
        if (count == 0) {
            throw new NotFoundException("error.entityWithIdNotFound", String.valueOf(id));
        }
    }

    //  https://stackoverflow.com/a/65442410/548473
    @NonNull
    public static Throwable getRootCause(@NonNull Throwable t) {
        Throwable rootCause = NestedExceptionUtils.getRootCause(t);
        return rootCause != null ? rootCause : t;
    }

    public static String getMessage(Throwable e) {
        return e.getLocalizedMessage() != null ? e.getLocalizedMessage() : e.getClass().getName();
    }
}