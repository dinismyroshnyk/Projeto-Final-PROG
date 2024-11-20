package main;
import java.math.BigDecimal;
import java.util.List;

public class Equipment {
    private String brand;
    private String model;
    private String internalCode;
    private Series series;
    private Version version;
    private BigDecimal voltage;
    private int stockQuantity;
    private BigDecimal salePrice;
    private boolean oem;
    private List<Category> categories;
    private List<Supplier> suppliers;
}
