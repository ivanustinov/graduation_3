package ru.ustinov.voting.repository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.ustinov.voting.model.User;

import java.util.Optional;

@Transactional(readOnly = true)
@CacheConfig(cacheNames = "users")
public interface UserRepository extends BaseRepository<User> {

    @Cacheable
    @Query("SELECT u FROM User u WHERE u.email = LOWER(:email)")
    Optional<User> getByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> get(int id);
}