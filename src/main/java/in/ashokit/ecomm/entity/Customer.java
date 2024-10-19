package in.ashokit.ecomm.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;


@Entity
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String phno;
    @CreationTimestamp
    @Column(name="date_created")
    private Date dateCreated;
    
    @UpdateTimestamp
    @Column(name="last_updated")
    private Date lastUpdated;  

}
