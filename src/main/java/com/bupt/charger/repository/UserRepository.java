package com.bupt.charger.repository;

import com.bupt.charger.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ll （ created: 2023-05-26 19:39 )
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    User findByUsernameAndPassword(String username, String password);
}
