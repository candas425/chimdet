package ro.utcn.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcn.backend.model.Price;

import java.util.List;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    List<Price> findAllByBusinessIdOrderByProductCantitate(int businessId);

    Price findByBusinessIdAndProductId(int businessId, int productId);
}
