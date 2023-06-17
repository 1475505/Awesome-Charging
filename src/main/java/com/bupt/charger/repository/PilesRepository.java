package com.bupt.charger.repository;
import com.bupt.charger.entity.Pile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author wxl,wyf （ created: 2023-05-29 20:43 )
 */
@Repository
public interface PilesRepository extends JpaRepository<Pile, Long>{
    Pile findByPileId(String pileId);

    default Pile findByPileId(int pile) {
        final String[] piles = {"CFA", "CFB", "CTA", "CTB", "CTC"};

        return findByPileId(piles[pile]);
    }
}