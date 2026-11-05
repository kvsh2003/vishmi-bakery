package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.DeliveryDTO;
import lk.ijse.vishmibakehouse.dto.DeliverySummaryDTO;
import lk.ijse.vishmibakehouse.dto.ShopDTO;
import lk.ijse.vishmibakehouse.dto.VehicleDTO;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DeliveryBO extends SuperBO {
    boolean saveShop(ShopDTO shop) throws SQLException;
    boolean updateShop(ShopDTO shop) throws SQLException;
    List<ShopDTO> getAllShops() throws SQLException;
    List<ShopDTO> getActiveShops() throws SQLException;

    boolean saveVehicle(VehicleDTO vehicle) throws SQLException;
    boolean updateVehicle(VehicleDTO vehicle) throws SQLException;
    List<VehicleDTO> getAllVehicles() throws SQLException;
    List<VehicleDTO> getActiveVehicles() throws SQLException;

    boolean prepareDelivery(DeliveryDTO deliveryDTO) throws SQLException;
    boolean dispatchDelivery(long deliveryId, long vehicleId, long deliveryEmployeeId) throws SQLException;
    boolean completeDelivery(long deliveryId, List<Long> deliveryItemIds, List<BigDecimal> deliveredQuantities) throws SQLException;
    boolean cancelDelivery(long deliveryId) throws SQLException;

    Optional<DeliveryDTO> findDeliveryById(long deliveryId) throws SQLException;
    List<DeliveryDTO> getAllDeliveries() throws SQLException;
    List<DeliveryDTO> getDeliveriesByDate(Date date) throws SQLException;
    List<DeliverySummaryDTO> getDeliverySummaries() throws SQLException;
}
