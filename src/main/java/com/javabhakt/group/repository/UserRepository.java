package com.javabhakt.group.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.javabhakt.group.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByUserName(String username);

}
