package in.ashokit.ecomm.entity;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer orderId;

    @Column(name = "order_tracking_num")
    private String orderTrackingNum;

    @Column(name = "razorpay_order_id")
    private String razorPayOrderId;

    @Column(name = "email")
    private String email;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "totalprice")
    private double totalPrice;

    @Column(name = "totalquantity")
    private int totalQuantity;

    @Column(name = "razorpay_payment_id")
    private String razorPayPaymentId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    @CreationTimestamp
    @Column(name="date_created", updatable = false)
    private LocalDate dateCreated;
    
    @Column(name="delivery_date")
    private LocalDate deliveryDate;

    @UpdateTimestamp
    @Column(name="last_updated", insertable = false)
    private LocalDate lastUpdated;
    
    //@ElementCollection
    //private List<OrderItem> orderItems;
    
    
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<OrderItem> items;
    
    private String invoice;
}
