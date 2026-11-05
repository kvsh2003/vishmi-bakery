package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.DeliveryBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.*;
import lk.ijse.vishmibakehouse.db.DBConnection;
import lk.ijse.vishmibakehouse.dto.DeliveryDTO;
import lk.ijse.vishmibakehouse.dto.DeliveryItemDTO;
import lk.ijse.vishmibakehouse.dto.DeliverySummaryDTO;
import lk.ijse.vishmibakehouse.dto.ShopDTO;
import lk.ijse.vishmibakehouse.dto.VehicleDTO;
import lk.ijse.vishmibakehouse.entity.Delivery;
import lk.ijse.vishmibakehouse.entity.DeliveryItem;
import lk.ijse.vishmibakehouse.entity.Shop;
import lk.ijse.vishmibakehouse.entity.Vehicle;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeliveryBOImpl implements DeliveryBO {
    private final ShopDAO shopDAO = DAOFactory.getInstance().getDAO(DAOTypes.SHOP);
    private final VehicleDAO vehicleDAO = DAOFactory.getInstance().getDAO(DAOTypes.VEHICLE);
    private final DeliveryDAO deliveryDAO = DAOFactory.getInstance().getDAO(DAOTypes.DELIVERY);
    private final DeliveryItemDAO deliveryItemDAO = DAOFactory.getInstance().getDAO(DAOTypes.DELIVERY_ITEM);
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);
    private final ProductDAO productDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCT);
    private final QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOTypes.QUERY);

    @Override
    public boolean saveShop(ShopDTO shop) throws SQLException {
        return shopDAO.save(new Shop(0, shop.getName(), shop.getPhone(), shop.getAddress(), shop.isActive()));
    }

    @Override
    public boolean updateShop(ShopDTO shop) throws SQLException {
        return shopDAO.update(new Shop(shop.getShopId(), shop.getName(), shop.getPhone(), shop.getAddress(), shop.isActive()));
    }

    @Override
    public List<ShopDTO> getAllShops() throws SQLException {
        List<Shop> list = shopDAO.findAll();
        List<ShopDTO> dtos = new ArrayList<>();
        for (Shop s : list) {
            dtos.add(new ShopDTO(s.getShopId(), s.getName(), s.getPhone(), s.getAddress(), s.isActive()));
        }
        return dtos;
    }

    @Override
    public List<ShopDTO> getActiveShops() throws SQLException {
        List<Shop> list = shopDAO.findActiveShops();
        List<ShopDTO> dtos = new ArrayList<>();
        for (Shop s : list) {
            dtos.add(new ShopDTO(s.getShopId(), s.getName(), s.getPhone(), s.getAddress(), s.isActive()));
        }
        return dtos;
    }

    @Override
    public boolean saveVehicle(VehicleDTO vehicle) throws SQLException {
        return vehicleDAO.save(new Vehicle(0, vehicle.getRegistrationNo(), vehicle.getModel(), vehicle.isActive()));
    }

    @Override
    public boolean updateVehicle(VehicleDTO vehicle) throws SQLException {
        return vehicleDAO.update(new Vehicle(vehicle.getVehicleId(), vehicle.getRegistrationNo(), vehicle.getModel(), vehicle.isActive()));
    }

    @Override
    public List<VehicleDTO> getAllVehicles() throws SQLException {
        List<Vehicle> list = vehicleDAO.findAll();
        List<VehicleDTO> dtos = new ArrayList<>();
        for (Vehicle v : list) {
            dtos.add(new VehicleDTO(v.getVehicleId(), v.getRegistrationNo(), v.getModel(), v.isActive()));
        }
        return dtos;
    }

    @Override
    public List<VehicleDTO> getActiveVehicles() throws SQLException {
        List<Vehicle> list = vehicleDAO.findActiveVehicles();
        List<VehicleDTO> dtos = new ArrayList<>();
        for (Vehicle v : list) {
            dtos.add(new VehicleDTO(v.getVehicleId(), v.getRegistrationNo(), v.getModel(), v.isActive()));
        }
        return dtos;
    }

    @Override
    public boolean prepareDelivery(DeliveryDTO deliveryDTO) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            Delivery delivery = new Delivery(
                    0,
                    deliveryDTO.getShopId(),
                    deliveryDTO.getVehicleId(),
                    deliveryDTO.getDeliveryEmployeeId(),
                    deliveryDTO.getPackedByEmployeeId(),
                    deliveryDTO.getDeliveryDate(),
                    "PACKED"
            );

            long deliveryId = deliveryDAO.saveAndReturnId(delivery);
            if (deliveryId <= 0) {
                connection.rollback();
                return false;
            }
            deliveryDTO.setDeliveryId(deliveryId);

            for (DeliveryItemDTO itemDTO : deliveryDTO.getItems()) {
                DeliveryItem item = new DeliveryItem(
                        0,
                        deliveryId,
                        itemDTO.getProductId(),
                        itemDTO.getPackedQty(),
                        BigDecimal.ZERO
                );
                if (!deliveryItemDAO.save(item)) {
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
    public boolean dispatchDelivery(long deliveryId, long vehicleId, long deliveryEmployeeId) throws SQLException {
        return deliveryDAO.updateStatusAndVehicle(deliveryId, "DISPATCHED", vehicleId, deliveryEmployeeId);
    }

    @Override
    public boolean completeDelivery(long deliveryId, List<Long> deliveryItemIds, List<BigDecimal> deliveredQuantities) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            for (int i = 0; i < deliveryItemIds.size(); i++) {
                long itemId = deliveryItemIds.get(i);
                BigDecimal delivered = deliveredQuantities.get(i);
                if (!deliveryItemDAO.updateDeliveredQty(itemId, delivered)) {
                    connection.rollback();
                    return false;
                }
            }

            Optional<Delivery> opt = deliveryDAO.findById(deliveryId);
            if (opt.isPresent()) {
                Delivery d = opt.get();
                d.setStatus("DELIVERED");
                if (!deliveryDAO.update(d)) {
                    connection.rollback();
                    return false;
                }
            } else {
                connection.rollback();
                return false;
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
    public boolean cancelDelivery(long deliveryId) throws SQLException {
        return deliveryDAO.delete(deliveryId);
    }

    @Override
    public Optional<DeliveryDTO> findDeliveryById(long deliveryId) throws SQLException {
        Optional<Delivery> opt = deliveryDAO.findById(deliveryId);
        if (opt.isPresent()) {
            Delivery d = opt.get();
            DeliveryDTO dto = mapToDTO(d);
            List<DeliveryItem> items = deliveryItemDAO.findByDeliveryId(deliveryId);
            List<DeliveryItemDTO> itemDTOs = new ArrayList<>();
            for (DeliveryItem di : items) {
                DeliveryItemDTO itemDTO = new DeliveryItemDTO(
                        di.getDeliveryItemId(),
                        di.getDeliveryId(),
                        di.getProductId(),
                        "",
                        di.getPackedQty(),
                        di.getDeliveredQty()
                );
                productDAO.findById(di.getProductId()).ifPresent(p -> itemDTO.setProductName(p.getName()));
                itemDTOs.add(itemDTO);
            }
            dto.setItems(itemDTOs);
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    @Override
    public List<DeliveryDTO> getAllDeliveries() throws SQLException {
        List<Delivery> list = deliveryDAO.findAll();
        List<DeliveryDTO> dtos = new ArrayList<>();
        for (Delivery d : list) {
            dtos.add(mapToDTO(d));
        }
        return dtos;
    }

    @Override
    public List<DeliveryDTO> getDeliveriesByDate(Date date) throws SQLException {
        List<Delivery> list = deliveryDAO.findByDate(date);
        List<DeliveryDTO> dtos = new ArrayList<>();
        for (Delivery d : list) {
            dtos.add(mapToDTO(d));
        }
        return dtos;
    }

    @Override
    public List<DeliverySummaryDTO> getDeliverySummaries() throws SQLException {
        return queryDAO.getDeliverySummaries();
    }

    private DeliveryDTO mapToDTO(Delivery d) throws SQLException {
        DeliveryDTO dto = new DeliveryDTO(
                d.getDeliveryId(),
                d.getShopId(),
                "",
                d.getVehicleId(),
                "",
                d.getDeliveryEmployeeId(),
                "",
                d.getPackedByEmployeeId(),
                "",
                d.getDeliveryDate(),
                d.getStatus()
        );
        shopDAO.findById(d.getShopId()).ifPresent(s -> dto.setShopName(s.getName()));
        if (d.getVehicleId() != null) {
            vehicleDAO.findById(d.getVehicleId()).ifPresent(v -> dto.setVehicleRegistration(v.getRegistrationNo()));
        }
        if (d.getDeliveryEmployeeId() != null) {
            employeeDAO.findById(d.getDeliveryEmployeeId()).ifPresent(e -> dto.setDeliveryEmployeeName(e.getName()));
        }
        employeeDAO.findById(d.getPackedByEmployeeId()).ifPresent(e -> dto.setPackedByEmployeeName(e.getName()));
        return dto;
    }
}
