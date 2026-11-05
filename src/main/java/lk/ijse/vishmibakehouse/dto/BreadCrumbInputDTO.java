package lk.ijse.vishmibakehouse.dto;

public class BreadCrumbInputDTO {
    private long breadCrumbBatchId;
    private long leftoverBatchId;
    private int inputQtyPieces;

    public BreadCrumbInputDTO() {}

    public BreadCrumbInputDTO(long breadCrumbBatchId, long leftoverBatchId, int inputQtyPieces) {
        this.breadCrumbBatchId = breadCrumbBatchId;
        this.leftoverBatchId = leftoverBatchId;
        this.inputQtyPieces = inputQtyPieces;
    }

    public long getBreadCrumbBatchId() { return breadCrumbBatchId; }
    public void setBreadCrumbBatchId(long breadCrumbBatchId) { this.breadCrumbBatchId = breadCrumbBatchId; }

    public long getLeftoverBatchId() { return leftoverBatchId; }
    public void setLeftoverBatchId(long leftoverBatchId) { this.leftoverBatchId = leftoverBatchId; }

    public int getInputQtyPieces() { return inputQtyPieces; }
    public void setInputQtyPieces(int inputQtyPieces) { this.inputQtyPieces = inputQtyPieces; }
}
