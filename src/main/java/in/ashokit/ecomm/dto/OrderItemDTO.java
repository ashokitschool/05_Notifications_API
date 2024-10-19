package in.ashokit.ecomm.dto;

import lombok.Data;

@Data
public class OrderItemDTO {

    private String imageUrl;
    private double unitPrice;
    private int quantity;
    private String prodname;

}
