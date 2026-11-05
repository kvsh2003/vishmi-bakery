package lk.ijse.vishmibakehouse.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private long productId;
    private String name;
    private String category;
    private String unit;
    private BigDecimal sellingPrice;
    private boolean active;
    private Timestamp createdAt;

    public Product() {}

    public Product(long productId, String name, String category, String unit, BigDecimal sellingPrice, boolean active) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.sellingPrice = sellingPrice;
        this.active = active;
    }

    public long getProductId() { return productId; }
    public void setProductId(long productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
