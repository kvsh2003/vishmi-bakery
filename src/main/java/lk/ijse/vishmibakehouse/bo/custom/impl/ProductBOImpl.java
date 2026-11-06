package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.ProductBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.ProductDAO;
import lk.ijse.vishmibakehouse.dao.custom.QueryDAO;
import lk.ijse.vishmibakehouse.dto.ProductAvailabilityDTO;
import lk.ijse.vishmibakehouse.dto.ProductDTO;
import lk.ijse.vishmibakehouse.entity.Product;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductBOImpl implements ProductBO {
    private final ProductDAO productDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCT);
    private final QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOTypes.QUERY);

    @Override
    public boolean saveProduct(ProductDTO dto) throws SQLException {
        for (Product p : productDAO.findAll()) {
            if (p.getName().trim().equalsIgnoreCase(dto.getName().trim())) {
                throw new SQLException("A product with the name '" + dto.getName().trim() + "' already exists!");
            }
        }
        return productDAO.save(new Product(
                0, dto.getName(), dto.getCategory(), dto.getUnit(), dto.getSellingPrice(), dto.isActive()
        ));
    }

    @Override
    public boolean updateProduct(ProductDTO dto) throws SQLException {
        for (Product p : productDAO.findAll()) {
            if (p.getProductId() != dto.getProductId() && p.getName().trim().equalsIgnoreCase(dto.getName().trim())) {
                throw new SQLException("Another product with the name '" + dto.getName().trim() + "' already exists!");
            }
        }
        return productDAO.update(new Product(
                dto.getProductId(), dto.getName(), dto.getCategory(), dto.getUnit(), dto.getSellingPrice(), dto.isActive()
        ));
    }

    @Override
    public boolean deleteProduct(long productId) throws SQLException {
        return productDAO.delete(productId);
    }

    @Override
    public Optional<ProductDTO> findProductById(long productId) throws SQLException {
        return productDAO.findById(productId).map(p -> new ProductDTO(
                p.getProductId(), p.getName(), p.getCategory(), p.getUnit(), p.getSellingPrice(), p.isActive()
        ));
    }

    @Override
    public List<ProductDTO> getAllProducts() throws SQLException {
        List<Product> list = productDAO.findAll();
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : list) {
            dtos.add(new ProductDTO(
                    p.getProductId(), p.getName(), p.getCategory(), p.getUnit(), p.getSellingPrice(), p.isActive()
            ));
        }
        return dtos;
    }

    @Override
    public List<ProductDTO> getActiveProducts() throws SQLException {
        List<Product> list = productDAO.findActiveProducts();
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : list) {
            dtos.add(new ProductDTO(
                    p.getProductId(), p.getName(), p.getCategory(), p.getUnit(), p.getSellingPrice(), p.isActive()
            ));
        }
        return dtos;
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) throws SQLException {
        List<Product> list = productDAO.searchByName(keyword);
        List<ProductDTO> dtos = new ArrayList<>();
        for (Product p : list) {
            dtos.add(new ProductDTO(
                    p.getProductId(), p.getName(), p.getCategory(), p.getUnit(), p.getSellingPrice(), p.isActive()
            ));
        }
        return dtos;
    }

    @Override
    public List<ProductAvailabilityDTO> getProductAvailability() throws SQLException {
        return queryDAO.getProductAvailabilityList();
    }
}
