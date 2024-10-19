package in.ashokit.ecomm.dto;

import lombok.Data;

@Data
public class PaymentCallbackDTO {

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
}
