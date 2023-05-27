package com.bupt.charger.repository;

import com.bupt.charger.entity.ChargeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ll （ created: 2023-05-27 18:37 )
 */
@Repository
public interface ChargeReqRepository extends JpaRepository<ChargeRequest, Long> {

}