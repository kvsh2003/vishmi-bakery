package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Ingredient {
    private long ingredientId;
    private String name;
    private String unit;
    private BigDecimal reorderLevel;
    private boolean active;
    private Timestamp createdAt;

    public Ingredient() {}

    public Ingredient(long ingredientId, String name, String unit, BigDecimal reorderLevel, boolean active) {
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

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
