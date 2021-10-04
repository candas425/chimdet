package ro.utcn.backend.backendservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Product;
import ro.utcn.backend.model.factory.IEntity;
import ro.utcn.backend.repositories.ProductRepository;

import java.util.List;
import java.util.Set;

/**
 * Created by Lucian on 3/26/2017.
 */

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
public class ProductService extends BaseService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAllByOrderByCantitate();
    }

    public List<Product> getAvailableProductsInCommandTable() {
        return productRepository.findAllByExistaInTabelComenziOrderByCantitate("Adevarat");
    }

    public Product getProductByIdentifierCode(String codIdentificare) {
        return productRepository.findByIdentificatorProdus(codIdentificare);
    }

    public Product getProductByIdCodeAndAvailableInTable(String codIdentificare) {
        return productRepository.findByIdentificatorProdusAndExistaInTabelComenzi(codIdentificare, "Adevarat");
    }

    public List<Product> getProductsByIds(Set<Integer> ids) {
        return productRepository.findAllByIdIn(ids);
    }

    public void deleteProduct(Product product) throws PersistanceException {
        productRepository.delete(product);
    }

    public void saveProduct(Product product) throws PersistanceException {
        List<Product> productList = productRepository.findAllByNumeOrIdentificatorProdusAndCantitate(product.getNume(), product.getIdentificatorProdus() != null ? product.getIdentificatorProdus() : "", product.getCantitate());
        if (productList.isEmpty()) {
            productRepository.save(product);
        } else {
            throw new PersistanceException(PersistanceException.OBJECT_EXIST);
        }
    }

    public double getProductCantityById(int productId) {
        return productRepository.findById(productId).getCantitate();
    }

    @Override
    public void update(IEntity IEntity) throws PersistanceException {
        Product product = (Product) IEntity;
        if (product.getIdentificatorProdus() != null && !product.getIdentificatorProdus().isEmpty()) {
            Product productFromDb = productRepository.findById(product.getId());
            if (productFromDb != null && !product.getIdentificatorProdus().equals(productFromDb.getIdentificatorProdus())) {
                Product getAnotherProductWithSameCode = getProductByIdentifierCode(product.getIdentificatorProdus());
                if (getAnotherProductWithSameCode != null) {
                    throw new PersistanceException(PersistanceException.CODE_EXIST);
                }
            }
        }

        productRepository.save((Product) IEntity);
    }
}
