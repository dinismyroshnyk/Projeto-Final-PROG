package main;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.time.Duration;


public class ServiceRequest {
    private int id;
    private Client client;
    private Technician technician;
    private List<OrderedProduct> products;
    private Date requestDate;
    private String description;
    private RequestState state;
    private BigDecimal totalCost;
    private Duration timeSpent;

    enum RequestState {
        SUBMITTED,
        ACCEPTED,
        CONCLUDED
    }

    private class OrderedProduct {
        private Equipment product;
        private int quantity;
    }
}