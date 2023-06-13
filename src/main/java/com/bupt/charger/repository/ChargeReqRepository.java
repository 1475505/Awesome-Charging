package com.bupt.charger.repository;

import com.bupt.charger.entity.ChargeRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ll （ created: 2023-05-27 18:37 )
 */
@Repository
public interface ChargeReqRepository extends JpaRepository<ChargeRequest, Long> {
    ChargeRequest findTopByCarIdAndStatusNotOrderByCreatedAtDesc(String carId, ChargeRequest.Status status);

    ChargeRequest findTopByCarIdAndStatusOrderByCreatedAtDesc(String carId, ChargeRequest.Status status);
    
    default ChargeRequest getLatestUnDoneRequests(String carId) {
        return this.findTopByCarIdAndStatusNotOrderByCreatedAtDesc(carId, ChargeRequest.Status.DONE);
    }

    default ChargeRequest getLatestDoneRequests(String carId) {
        return this.findTopByCarIdAndStatusOrderByCreatedAtDesc(carId, ChargeRequest.Status.DONE);
    }

    List<ChargeRequest> findAllByCarIdOrderById(String carId);
}