package in.ashokit.ecomm.dto;

import jakarta.persistence.Column;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
public class CustomerDTO {

    private String name;
    private String email;
    private String phno;

    @CreationTimestamp
    @Column(name="date_created")
    private Date dateCreated;
    
    @UpdateTimestamp
    @Column(name="last_updated")
    private Date lastUpdated;


}
