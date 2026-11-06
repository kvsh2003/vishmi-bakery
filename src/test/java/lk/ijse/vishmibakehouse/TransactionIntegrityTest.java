package lk.ijse.vishmibakehouse;

import lk.ijse.vishmibakehouse.bo.BOFactory;
import lk.ijse.vishmibakehouse.bo.BOTypes;
import lk.ijse.vishmibakehouse.bo.custom.LeftoverBO;
import lk.ijse.vishmibakehouse.bo.custom.SaleBO;
import lk.ijse.vishmibakehouse.dto.BreadCrumbBatchDTO;
import lk.ijse.vishmibakehouse.dto.BreadCrumbInputDTO;
import lk.ijse.vishmibakehouse.dto.SaleDTO;
import lk.ijse.vishmibakehouse.dto.SaleItemDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionIntegrityTest {

    @Test
    @DisplayName("Test Sale Placement Atomic Transaction Rollback on Invalid Product")
    public void testSaleAtomicRollback() {
        SaleBO saleBO = BOFactory.getInstance().getBO(BOTypes.SALE);

        SaleDTO invalidSale = new SaleDTO();
        invalidSale.setCashierEmployeeId(7); // Ruwan
        invalidSale.setSoldAt(new Timestamp(System.currentTimeMillis()));
        invalidSale.setCashReceived(new BigDecimal("5000.00"));

        // Add valid item and an invalid item referencing non-existent product ID
        invalidSale.getItems().add(new SaleItemDTO(0, 0, 1, "White Bread", new BigDecimal("2.0"), new BigDecimal("180.0"), new BigDecimal("360.0")));
        invalidSale.getItems().add(new SaleItemDTO(0, 0, 999999, "Non-existent product", new BigDecimal("1.0"), new BigDecimal("100.0"), new BigDecimal("100.0")));

        assertThrows(SQLException.class, () -> {
            saleBO.placeSale(invalidSale);
        }, "Inserting a sale with invalid foreign key must throw SQLException and trigger rollback");
    }

    @Test
    @DisplayName("Test Sale Placement Rejection When Stock Is Insufficient")
    public void testSalePlacementRejectionOnInsufficientStock() {
        SaleBO saleBO = BOFactory.getInstance().getBO(BOTypes.SALE);

        SaleDTO excessSale = new SaleDTO();
        excessSale.setCashierEmployeeId(7);
        excessSale.setSoldAt(new Timestamp(System.currentTimeMillis()));
        excessSale.setCashReceived(new BigDecimal("50000000.00"));

        // Requesting 999,999 units exceeds any available stock in the bakery
        excessSale.getItems().add(new SaleItemDTO(0, 0, 1, "White Bread 450g", new BigDecimal("999999.0"), new BigDecimal("180.0"), new BigDecimal("179999820.0")));

        SQLException exception = assertThrows(SQLException.class, () -> {
            saleBO.placeSale(excessSale);
        }, "Selling items exceeding available stock must throw SQLException and roll back");

        assertTrue(exception.getMessage().contains("insufficient stock"), "Exception message should indicate insufficient stock");
    }

    @Test
    @DisplayName("Test Leftover Lot Overconsumption Validation")
    public void testLeftoverExcessiveConsumptionRejection() {
        LeftoverBO leftoverBO = BOFactory.getInstance().getBO(BOTypes.LEFTOVER);

        BreadCrumbBatchDTO batch = new BreadCrumbBatchDTO();
        batch.setOutputProductId(7); // Bread crumbs
        batch.setRecordedByEmployeeId(3);
        batch.setProducedAt(new Timestamp(System.currentTimeMillis()));
        batch.setOutputQtyKg(new BigDecimal("50.0"));

        // Lot 1 has only 5 pieces remaining (25 total - 20 used). Asking for 100 pieces must fail!
        batch.getInputs().add(new BreadCrumbInputDTO(0, 1, 100));

        assertThrows(SQLException.class, () -> {
            leftoverBO.produceBreadCrumbs(batch);
        }, "Consuming more than remaining leftover pieces must be rejected");
    }
}
