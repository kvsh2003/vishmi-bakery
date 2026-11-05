package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.ProductAvailabilityDTO;
import lk.ijse.vishmibakehouse.dto.ProductDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductBO extends SuperBO {
    boolean saveProduct(ProductDTO dto) throws SQLException;
    boolean updateProduct(ProductDTO dto) throws SQLException;
    boolean deleteProduct(long productId) throws SQLException;
    Optional<ProductDTO> findProductById(long productId) throws SQLException;
    List<ProductDTO> getAllProducts() throws SQLException;
    List<ProductDTO> getActiveProducts() throws SQLException;
    List<ProductDTO> searchProducts(String keyword) throws SQLException;
    List<ProductAvailabilityDTO> getProductAvailability() throws SQLException;
}
