package org.userrequsts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.userrequsts.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(
        "SELECT u FROM User u WHERE name LIKE :name"
    )
    List<User> getUsersByName(String name);

    @Query(
        "SELECT u FROM User u WHERE username = :login"
    )
    User getUserByLogin(String login);
}
