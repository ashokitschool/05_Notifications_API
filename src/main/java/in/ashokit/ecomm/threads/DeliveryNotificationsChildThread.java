package in.ashokit.ecomm.threads;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

import in.ashokit.ecomm.entity.Order;
import in.ashokit.ecomm.listener.SpringContext;
import in.ashokit.ecomm.util.NotificationsUtility;

public class DeliveryNotificationsChildThread implements Callable<List<Order>>{
	
	private List<Order> orders;
	
	private NotificationsUtility getNotificationsUtility() {
        return SpringContext.getBean(NotificationsUtility.class);
    }
	
	@Override
	public List<Order> call() throws Exception {
		sendDeliveryNotifications(orders);
		return orders;
	}

	private void sendDeliveryNotifications(List<Order> orders) {
		if(Objects.nonNull(orders) && !orders.isEmpty()) {
			for (Order order : orders) {
				getNotificationsUtility().sendDeliveryNotification(order);
			}
		}
	}
	
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

}
