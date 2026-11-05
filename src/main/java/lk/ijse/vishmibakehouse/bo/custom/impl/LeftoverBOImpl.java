package lk.ijse.vishmibakehouse.bo.custom.impl;

import lk.ijse.vishmibakehouse.bo.custom.LeftoverBO;
import lk.ijse.vishmibakehouse.dao.DAOFactory;
import lk.ijse.vishmibakehouse.dao.DAOTypes;
import lk.ijse.vishmibakehouse.dao.custom.*;
import lk.ijse.vishmibakehouse.db.DBConnection;
import lk.ijse.vishmibakehouse.dto.BreadCrumbBatchDTO;
import lk.ijse.vishmibakehouse.dto.BreadCrumbInputDTO;
import lk.ijse.vishmibakehouse.dto.LeftoverBalanceDTO;
import lk.ijse.vishmibakehouse.dto.LeftoverBatchDTO;
import lk.ijse.vishmibakehouse.entity.BreadCrumbBatch;
import lk.ijse.vishmibakehouse.entity.BreadCrumbInput;
import lk.ijse.vishmibakehouse.entity.LeftoverBatch;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeftoverBOImpl implements LeftoverBO {
    private final LeftoverBatchDAO leftoverBatchDAO = DAOFactory.getInstance().getDAO(DAOTypes.LEFTOVER_BATCH);
    private final BreadCrumbBatchDAO breadCrumbBatchDAO = DAOFactory.getInstance().getDAO(DAOTypes.BREAD_CRUMB_BATCH);
    private final ProductionBatchDAO productionBatchDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCTION_BATCH);
    private final ProductDAO productDAO = DAOFactory.getInstance().getDAO(DAOTypes.PRODUCT);
    private final EmployeeDAO employeeDAO = DAOFactory.getInstance().getDAO(DAOTypes.EMPLOYEE);
    private final QueryDAO queryDAO = DAOFactory.getInstance().getDAO(DAOTypes.QUERY);

    @Override
    public boolean recordLeftoverBatch(LeftoverBatchDTO dto) throws SQLException {
        return leftoverBatchDAO.save(new LeftoverBatch(
                0,
                dto.getProductionBatchId(),
                dto.getRecordedByEmployeeId(),
                dto.getRecordedOn(),
                dto.getQtyPieces()
        ));
    }

    @Override
    public List<LeftoverBatchDTO> getAllLeftovers() throws SQLException {
        List<LeftoverBatch> list = leftoverBatchDAO.findAll();
        List<LeftoverBatchDTO> dtos = new ArrayList<>();
        for (LeftoverBatch lb : list) {
            LeftoverBatchDTO dto = new LeftoverBatchDTO(
                    lb.getLeftoverBatchId(),
                    lb.getProductionBatchId(),
                    "",
                    lb.getRecordedByEmployeeId(),
                    "",
                    lb.getRecordedOn(),
                    lb.getQtyPieces()
            );
            productionBatchDAO.findById(lb.getProductionBatchId()).ifPresent(pb -> {
                try {
                    productDAO.findById(pb.getProductId()).ifPresent(p -> dto.setProductName(p.getName()));
                } catch (SQLException ignored) {}
            });
            employeeDAO.findById(lb.getRecordedByEmployeeId()).ifPresent(e -> dto.setEmployeeName(e.getName()));
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<LeftoverBalanceDTO> getLeftoverBalances() throws SQLException {
        return queryDAO.getLeftoverBalances();
    }

    @Override
    public boolean produceBreadCrumbs(BreadCrumbBatchDTO crumbBatchDTO) throws SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        boolean originalAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);

            // 1. Validate remaining pieces for each input lot
            List<LeftoverBalanceDTO> balances = queryDAO.getLeftoverBalances();
            for (BreadCrumbInputDTO input : crumbBatchDTO.getInputs()) {
                int available = 0;
                for (LeftoverBalanceDTO b : balances) {
                    if (b.getLeftoverBatchId() == input.getLeftoverBatchId()) {
                        available = b.getRemainingPieces();
                        break;
                    }
                }
                if (input.getInputQtyPieces() > available) {
                    connection.rollback();
                    throw new SQLException("Cannot consume " + input.getInputQtyPieces() + " pieces from leftover lot #" +
                            input.getLeftoverBatchId() + " (only " + available + " pieces remaining)");
                }
            }

            // 2. Insert BreadCrumbBatch header
            BreadCrumbBatch batch = new BreadCrumbBatch(
                    0,
                    crumbBatchDTO.getOutputProductId(),
                    crumbBatchDTO.getRecordedByEmployeeId(),
                    crumbBatchDTO.getProducedAt(),
                    crumbBatchDTO.getOutputQtyKg()
            );

            long batchId = breadCrumbBatchDAO.saveAndReturnId(batch);
            if (batchId <= 0) {
                connection.rollback();
                return false;
            }
            crumbBatchDTO.setBreadCrumbBatchId(batchId);

            // 3. Insert lot inputs
            for (BreadCrumbInputDTO input : crumbBatchDTO.getInputs()) {
                BreadCrumbInput bci = new BreadCrumbInput(
                        batchId,
                        input.getLeftoverBatchId(),
                        input.getInputQtyPieces()
                );
                if (!breadCrumbBatchDAO.saveBreadCrumbInput(bci)) {
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
    public List<BreadCrumbBatchDTO> getAllBreadCrumbBatches() throws SQLException {
        List<BreadCrumbBatch> list = breadCrumbBatchDAO.findAll();
        List<BreadCrumbBatchDTO> dtos = new ArrayList<>();
        for (BreadCrumbBatch bcb : list) {
            BreadCrumbBatchDTO dto = new BreadCrumbBatchDTO(
                    bcb.getBreadCrumbBatchId(),
                    bcb.getOutputProductId(),
                    "",
                    bcb.getRecordedByEmployeeId(),
                    "",
                    bcb.getProducedAt(),
                    bcb.getOutputQtyKg()
            );
            productDAO.findById(bcb.getOutputProductId()).ifPresent(p -> dto.setOutputProductName(p.getName()));
            employeeDAO.findById(bcb.getRecordedByEmployeeId()).ifPresent(e -> dto.setEmployeeName(e.getName()));
            dtos.add(dto);
        }
        return dtos;
    }
}
