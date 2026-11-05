package lk.ijse.vishmibakehouse.bo.custom;

import lk.ijse.vishmibakehouse.bo.SuperBO;
import lk.ijse.vishmibakehouse.dto.BreadCrumbBatchDTO;
import lk.ijse.vishmibakehouse.dto.LeftoverBalanceDTO;
import lk.ijse.vishmibakehouse.dto.LeftoverBatchDTO;

import java.sql.SQLException;
import java.util.List;

public interface LeftoverBO extends SuperBO {
    boolean recordLeftoverBatch(LeftoverBatchDTO dto) throws SQLException;
    List<LeftoverBatchDTO> getAllLeftovers() throws SQLException;
    List<LeftoverBalanceDTO> getLeftoverBalances() throws SQLException;

    boolean produceBreadCrumbs(BreadCrumbBatchDTO crumbBatchDTO) throws SQLException;
    List<BreadCrumbBatchDTO> getAllBreadCrumbBatches() throws SQLException;
}
