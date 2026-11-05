package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;

public class IngredientStockDTO {
    private long ingredientId;
    private String name;
    private String unit;
    private BigDecimal reorderLevel;
    private boolean active;
    private BigDecimal purchasedQty;
    private BigDecimal usedQty;
    private BigDecimal availableQty;
    private boolean lowStock;

    public IngredientStockDTO() {}

    public IngredientStockDTO(long ingredientId, String name, String unit, BigDecimal reorderLevel, boolean active, BigDecimal purchasedQty, BigDecimal usedQty, BigDecimal availableQty, boolean lowStock) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.unit = unit;
        this.reorderLevel = reorderLevel;
        this.active = active;
        this.purchasedQty = purchasedQty;
        this.usedQty = usedQty;
        this.availableQty = availableQty;
        this.lowStock = lowStock;
    }

    public long getIngredientId() { return ingredientId; }
    public void setIngredientId(long ingredientId) { this.ingredientId = ingredientId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getReorderLevel() { return reorderLevel; }
    public void setReorderLevel(BigDecimal reorderLevel) { this.reorderLevel = reorderLevel; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public BigDecimal getPurchasedQty() { return purchasedQty; }
    public void setPurchasedQty(BigDecimal purchasedQty) { this.purchasedQty = purchasedQty; }

    public BigDecimal getUsedQty() { return usedQty; }
    public void setUsedQty(BigDecimal usedQty) { this.usedQty = usedQty; }

    public BigDecimal getAvailableQty() { return availableQty; }
    public void setAvailableQty(BigDecimal availableQty) { this.availableQty = availableQty; }

    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }
}
