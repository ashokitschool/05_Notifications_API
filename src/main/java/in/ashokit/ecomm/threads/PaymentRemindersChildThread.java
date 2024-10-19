package in.ashokit.ecomm.threads;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import in.ashokit.ecomm.entity.Order;
import in.ashokit.ecomm.listener.SpringContext;
import in.ashokit.ecomm.util.PaymentReminderUtility;

public class PaymentRemindersChildThread implements Callable<List<Order>>{
	
	private List<Order> orders;
	
	private PaymentReminderUtility getPaymentReminderUtility() {
        return SpringContext.getBean(PaymentReminderUtility.class);
    }
	
	@Override
	public List<Order> call() throws Exception {
		sendReminder(orders);
		return orders;
	}

	private void sendReminder(List<Order> orders) {
		if(Objects.nonNull(orders) && !orders.isEmpty()) {
			for (Order order : orders) {
				getPaymentReminderUtility().sendPaymentReminder(order);
			}
		}
	}
	
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

}
