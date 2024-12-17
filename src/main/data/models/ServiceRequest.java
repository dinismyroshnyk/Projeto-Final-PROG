package main.data.models;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;
import java.time.Duration;


@SuppressWarnings("unused") // Temporary, until the model is implemented
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

    private enum RequestState {
        SUBMITTED,
        ACCEPTED,
        CONCLUDED
    }

    private class OrderedProduct {
        private Equipment product;
        private int quantity;
    }
}