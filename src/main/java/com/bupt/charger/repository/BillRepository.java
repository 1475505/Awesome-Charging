package com.bupt.charger.repository;

/**
 * @author ll （ created: 2023-05-27 17:42 )
 */
import com.bupt.charger.entity.Bill;
import com.bupt.charger.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    Bill findBillById(long billId);
    List<Bill> findAllByCarIdOrderByStartTime(String carId);


}
