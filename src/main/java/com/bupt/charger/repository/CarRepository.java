package com.bupt.charger.repository;

/**
 * @author ll （ created: 2023-05-27 17:42 )
 */
import com.bupt.charger.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    boolean existsByCarId(String carId);
    Car findByCarId(String carId);

    List<Car> findAllByPileId(String pileId);
}
