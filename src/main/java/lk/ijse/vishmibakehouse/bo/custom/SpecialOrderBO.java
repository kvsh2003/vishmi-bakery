package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.CustomerDTO;
import lk.ijse.vishmibakehouse.dto.SpecialOrderDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface SpecialOrderBO extends SuperBO {
    boolean saveCustomer(CustomerDTO customer) throws SQLException;
    boolean updateCustomer(CustomerDTO customer) throws SQLException;
    List<CustomerDTO> getAllCustomers() throws SQLException;
    Optional<CustomerDTO> findCustomerByPhone(String phone) throws SQLException;

    boolean placeSpecialOrder(SpecialOrderDTO orderDTO) throws SQLException;
    boolean updateOrderStatus(long specialOrderId, String status) throws SQLException;
    Optional<SpecialOrderDTO> findOrderById(long specialOrderId) throws SQLException;
    List<SpecialOrderDTO> getAllOrders() throws SQLException;
    List<SpecialOrderDTO> getOrdersByStatus(String status) throws SQLException;
}
