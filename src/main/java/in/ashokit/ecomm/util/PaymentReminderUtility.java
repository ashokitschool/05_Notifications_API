package in.ashokit.ecomm.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.ashokit.ecomm.entity.Order;
import in.ashokit.ecomm.service.WatiService;

@Component
public class PaymentReminderUtility {

	@Autowired
	private WatiService watiService;

	public void sendPaymentReminder(Order order) {
		watiService.sendPaymentReminder(order.getCustomer().getPhno(), order.getCustomer().getName(), order.getOrderTrackingNum());
	}
}
