package lk.ijse.vishmibakehouse.dto;

import java.math.BigDecimal;

public class IngredientDTO {
    private long ingredientId;
    private String name;
    private String unit;
    private BigDecimal reorderLevel;
    private boolean active;

    public IngredientDTO() {}

    public IngredientDTO(long ingredientId, String name, String unit, BigDecimal reorderLevel, boolean active) {
        this.ingredientId = ingredientId;
        this.name = name;
        this.unit = unit;
        this.reorderLevel = reorderLevel;
        this.active = active;
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

    @Override
    public String toString() {
        return name + " (" + unit + ")";
    }
}
