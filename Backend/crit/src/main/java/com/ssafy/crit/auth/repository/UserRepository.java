package com.ssafy.crit.auth.repository;

import com.querydsl.core.Tuple;
import com.ssafy.crit.auth.entity.User;
import com.ssafy.crit.auth.entity.enumType.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    boolean existsById(String id);
    boolean existsByPassword(String password);
    boolean existsByIdAndAuthProvider(String id, AuthProvider authProvider);
    Optional<User> findByNickname(String nickname);

}
