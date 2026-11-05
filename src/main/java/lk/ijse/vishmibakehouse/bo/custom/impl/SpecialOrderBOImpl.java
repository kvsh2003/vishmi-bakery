package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.SpecialOrderBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.CustomerDAO;
import lk.ijse.vishmibakehouse.dao.custom.EmployeeDAO;
import lk.ijse.vishmibakehouse.dao.custom.ProductDAO;
import lk.ijse.vishmibakehouse.dao.custom.SpecialOrderDAO;
import lk.ijse.vishmibakehouse.dao.custom.SpecialOrderItemDAO;
import lk.ijse.vishmibakehouse.db.DBConnection;
import lk.ijse.vishmibakehouse.dto.CustomerDTO;
import lk.ijse.vishmibakehouse.dto.SpecialOrderDTO;
import lk.ijse.vishmibakehouse.dto.SpecialOrderItemDTO;
import lk.ijse.vishmibakehouse.entity.Customer;
import lk.ijse.vishmibakehouse.entity.SpecialOrder;
import lk.ijse.vishmibakehouse.entity.SpecialOrderItem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpecialOrderBOImpl implements SpecialOrderBO {
    private final CustomerDAO customerDAO = DAOFactory.getInstance().getDAO(DAOTypes.CUSTOMER);
    private final SpecialOrderDAO specialOrderDAO = DAOFactory.getInstance().getDAO(DAOTypes.SPECIAL_ORDER);
    private final SpecialOrderItemDAO specialOrderItemDAO = DAOFactory.getInstance().getDAO(DAOTypes.SPECIAL_ORDER_ITEM);
    private final ProductDAO productDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCT);
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);

    @Override
    public boolean saveCustomer(CustomerDTO customer) throws SQLException {
        return customerDAO.save(new Customer(0, customer.getName(), customer.getPhone(), customer.getAddress()));
    }

    @Override
    public boolean updateCustomer(CustomerDTO customer) throws SQLException {
        return customerDAO.update(new Customer(customer.getCustomerId(), customer.getName(), customer.getPhone(), customer.getAddress()));
    }

    @Override
    public List<CustomerDTO> getAllCustomers() throws SQLException {
        List<Customer> list = customerDAO.findAll();
        List<CustomerDTO> dtos = new ArrayList<>();
        for (Customer c : list) {
            dtos.add(new CustomerDTO(c.getCustomerId(), c.getName(), c.getPhone(), c.getAddress()));
        }
        return dtos;
    }

    @Override
    public Optional<CustomerDTO> findCustomerByPhone(String phone) throws SQLException {
        return customerDAO.findByPhone(phone).map(c -> new CustomerDTO(c.getCustomerId(), c.getName(), c.getPhone(), c.getAddress()));
    }

    @Override
    public boolean placeSpecialOrder(SpecialOrderDTO orderDTO) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            SpecialOrder order = new SpecialOrder(
                    0,
                    orderDTO.getCustomerId(),
                    orderDTO.getRecordedByEmployeeId(),
                    orderDTO.getOrderType(),
                    orderDTO.getRequiredAt(),
                    orderDTO.getStatus() != null ? orderDTO.getStatus() : "PENDING",
                    orderDTO.getInstructions(),
                    orderDTO.getDepositAmount() != null ? orderDTO.getDepositAmount() : BigDecimal.ZERO
            );

            long orderId = specialOrderDAO.saveAndReturnId(order);
            if (orderId <= 0) {
                connection.rollback();
                return false;
            }
            orderDTO.setSpecialOrderId(orderId);

            for (SpecialOrderItemDTO itemDTO : orderDTO.getItems()) {
                SpecialOrderItem item = new SpecialOrderItem(
                        0,
                        orderId,
                        itemDTO.getProductId(),
                        itemDTO.getDescription(),
                        itemDTO.getQuantity(),
                        itemDTO.getUnitPrice()
                );
                if (!specialOrderItemDAO.save(item)) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    @Override
    public boolean updateOrderStatus(long specialOrderId, String status) throws SQLException {
        return specialOrderDAO.updateStatus(specialOrderId, status);
    }

    @Override
    public Optional<SpecialOrderDTO> findOrderById(long specialOrderId) throws SQLException {
        Optional<SpecialOrder> opt = specialOrderDAO.findById(specialOrderId);
        if (opt.isPresent()) {
            SpecialOrder so = opt.get();
            SpecialOrderDTO dto = mapToDTO(so);

            List<SpecialOrderItem> items = specialOrderItemDAO.findBySpecialOrderId(specialOrderId);
            List<SpecialOrderItemDTO> itemDTOs = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            for (SpecialOrderItem soi : items) {
                SpecialOrderItemDTO itemDTO = new SpecialOrderItemDTO(
                        soi.getSpecialOrderItemId(),
                        soi.getSpecialOrderId(),
                        soi.getProductId(),
                        "",
                        soi.getDescription(),
                        soi.getQuantity(),
                        soi.getUnitPrice(),
                        soi.getLineTotal()
                );
                if (soi.getProductId() != null) {
                    productDAO.findById(soi.getProductId()).ifPresent(p -> itemDTO.setProductName(p.getName()));
                }
                itemDTOs.add(itemDTO);
                total = total.add(soi.getLineTotal() != null ? soi.getLineTotal() : soi.getQuantity().multiply(soi.getUnitPrice()));
            }
            dto.setItems(itemDTOs);
            dto.setTotalAmount(total);
            dto.setBalanceAmount(total.subtract(dto.getDepositAmount()));

            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<SpecialOrderDTO> getAllOrders() throws SQLException {
        List<SpecialOrder> list = specialOrderDAO.findAll();
        List<SpecialOrderDTO> dtos = new ArrayList<>();
        for (SpecialOrder so : list) {
            SpecialOrderDTO dto = mapToDTO(so);
            List<SpecialOrderItem> items = specialOrderItemDAO.findBySpecialOrderId(so.getSpecialOrderId());
            BigDecimal total = BigDecimal.ZERO;
            for (SpecialOrderItem soi : items) {
                total = total.add(soi.getLineTotal() != null ? soi.getLineTotal() : soi.getQuantity().multiply(soi.getUnitPrice()));
            }
            dto.setTotalAmount(total);
            dto.setBalanceAmount(total.subtract(dto.getDepositAmount()));
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<SpecialOrderDTO> getOrdersByStatus(String status) throws SQLException {
        List<SpecialOrder> list = specialOrderDAO.findByStatus(status);
        List<SpecialOrderDTO> dtos = new ArrayList<>();
        for (SpecialOrder so : list) {
            SpecialOrderDTO dto = mapToDTO(so);
            dtos.add(dto);
        }
        return dtos;
    }

    private SpecialOrderDTO mapToDTO(SpecialOrder so) throws SQLException {
        SpecialOrderDTO dto = new SpecialOrderDTO(
                so.getSpecialOrderId(),
                so.getCustomerId(),
                "",
                "",
                so.getRecordedByEmployeeId(),
                "",
                so.getOrderType(),
                so.getRequiredAt(),
                so.getStatus(),
                so.getInstructions(),
                so.getDepositAmount(),
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
        customerDAO.findById(so.getCustomerId()).ifPresent(c -> {
            dto.setCustomerName(c.getName());
            dto.setCustomerPhone(c.getPhone());
        });
        employeeDAO.findById(so.getRecordedByEmployeeId()).ifPresent(e -> dto.setEmployeeName(e.getName()));
        return dto;
    }
}
