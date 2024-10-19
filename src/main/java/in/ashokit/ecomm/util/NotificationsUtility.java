package in.ashokit.ecomm.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.ashokit.ecomm.entity.Order;
import in.ashokit.ecomm.model.EmailDetails;
import in.ashokit.ecomm.service.EmailService;
import in.ashokit.ecomm.service.OrderService;
import in.ashokit.ecomm.service.WatiService;

@Component
public class NotificationsUtility {
	
	@Autowired
	private PdfGeneratorUtility pdfGenerator;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private WatiService watiService;

	public void sendDeliveryNotification(Order order) {
		String invoiceUrl = pdfGenerator.generatePdfReport(order);
		order.setInvoice(invoiceUrl);
		orderService.updateOrder(order);
		EmailDetails emailDetails = EmailDetails.builder().recipient(order.getEmail()).attachment("invoices"+invoiceUrl.substring(invoiceUrl.lastIndexOf("/"), invoiceUrl.length())).
		subject("Your ashokit.in order #"+ order.getOrderTrackingNum()+" has been delivered").msgBody("Hi Your order has been delivered successfully, Please find the invoice for your reference").build();
		emailService.sendMailWithAttachment(emailDetails);
		watiService.sendDeliveryNotification(order.getCustomer().getPhno(), order.getCustomer().getName(), order.getOrderTrackingNum());
		orderService.deleteInvoiceFromLocalStorage(order.getOrderTrackingNum());
	}
}
