package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;

public class ProductAvailabilityDTO {
    private long productId;
    private String name;
    private String unit;
    private boolean active;
    private BigDecimal producedQty;
    private BigDecimal outletSoldQty;
    private BigDecimal shopAllocatedQty;
    private BigDecimal leftoverQty;
    private BigDecimal availableQty;

    public ProductAvailabilityDTO() {}

    public ProductAvailabilityDTO(long productId, String name, String unit, boolean active, BigDecimal producedQty, BigDecimal outletSoldQty, BigDecimal shopAllocatedQty, BigDecimal leftoverQty, BigDecimal availableQty) {
        this.productId = productId;
        this.name = name;
        this.unit = unit;
        this.active = active;
        this.producedQty = producedQty;
        this.outletSoldQty = outletSoldQty;
        this.shopAllocatedQty = shopAllocatedQty;
        this.leftoverQty = leftoverQty;
        this.availableQty = availableQty;
    }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public BigDecimal getProducedQty() { return producedQty; }
    public void setProducedQty(BigDecimal producedQty) { this.producedQty = producedQty; }

    public BigDecimal getOutletSoldQty() { return outletSoldQty; }
    public void setOutletSoldQty(BigDecimal outletSoldQty) { this.outletSoldQty = outletSoldQty; }

    public BigDecimal getShopAllocatedQty() { return shopAllocatedQty; }
    public void setShopAllocatedQty(BigDecimal shopAllocatedQty) { this.shopAllocatedQty = shopAllocatedQty; }

    public BigDecimal getLeftoverQty() { return leftoverQty; }
    public void setLeftoverQty(BigDecimal leftoverQty) { this.leftoverQty = leftoverQty; }

    public BigDecimal getAvailableQty() { return availableQty; }
    public void setAvailableQty(BigDecimal availableQty) { this.availableQty = availableQty; }
}
