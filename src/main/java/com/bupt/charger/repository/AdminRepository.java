package com.bupt.charger.repository;

import com.bupt.charger.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author wyf （ created: 2023-05-29 13:30 )
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByAdminNameAndPassword(String AdminName, String password);

}
