package com.misiontic.c17g4.securityBackend.repositories;

import com.misiontic.c17g4.securityBackend.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    @Query(value = "SELECT * FROM user WHERE email=? AND password=?;", nativeQuery = true)
    Optional<User> validateLogin(String email, String password);
}
