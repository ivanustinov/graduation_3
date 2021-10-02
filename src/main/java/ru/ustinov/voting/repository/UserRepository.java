package ru.ustinov.voting.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.User;

import java.util.Optional;

@Transactional(readOnly = true)
public interface UserRepository extends BaseRepository<User> {

    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    @Cacheable("users")
    Optional<User> getByEmail(String email);

    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = "users", key = "#user.email")
    User save(User user);


    @Override
    @Modifying
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    void deleteById(Integer user_id);
}