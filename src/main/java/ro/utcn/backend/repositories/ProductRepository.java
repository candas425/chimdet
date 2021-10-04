package ro.utcn.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcn.backend.model.Product;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderByCantitate();

    List<Product> findAllByIdIn(Set<Integer> ids);

    Product findByIdentificatorProdus(String identificatorProdus);

    Product findById(int id);

    Product findByIdentificatorProdusAndExistaInTabelComenzi(String identificatorProdus, String existaInTabelComenzi);

    List<Product> findAllByExistaInTabelComenziOrderByCantitate(String existaInTabelComenzi);

    List<Product> findAllByNumeOrIdentificatorProdusAndCantitate(String nume, String identificatorProdus, double cantitate);
}
