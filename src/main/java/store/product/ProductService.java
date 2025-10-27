package store.product;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product create(Product product) {
        if (product.name() == null || product.name().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name is mandatory.");
        }

        if (product.price() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product price is mandatory.");
        }

        if (productRepository.findByName(product.name()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product name already registered.");
        }

        ProductModel savedModel = productRepository.save(new ProductModel(product));
        return savedModel.to();
    }

    public List<Product> findAll() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .map(ProductModel::to)
                .toList();
    }

    public Product findById(String id) {
        return productRepository.findById(id)
                .map(ProductModel::to)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found."));
    }

    public void delete(String id) {
        if (!productRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found.");
        }
        productRepository.deleteById(id);
    }
}
