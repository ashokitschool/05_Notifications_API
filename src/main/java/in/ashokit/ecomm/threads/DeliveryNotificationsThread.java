package in.ashokit.ecomm.threads;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import in.ashokit.ecomm.entity.Order;

public class DeliveryNotificationsThread implements Runnable{

	private List<Order> orders;
	
	public DeliveryNotificationsThread(List<Order> orders){
		this.orders = orders;
	}
	
	private ExecutorService executorService;
	
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		System.out.println("NotificationThread started...");
		process();
		System.out.println("NotificationThread response time "+(System.currentTimeMillis()-startTime)+" ms");
	}

	private void process() {
		int batchSize = 1000;
		List<List<Order>> beansBatchList = splitBeansIntoBatches(orders, batchSize);
		List<Callable<List<Order>>> callablesList = new ArrayList<Callable<List<Order>>>();
		
		for (int i = 0; i < beansBatchList.size(); i++) {
			DeliveryNotificationsChildThread childThread = new DeliveryNotificationsChildThread();
			childThread.setOrders(orders);
			callablesList.add(childThread);
			if (callablesList.size() == 100 || (callablesList.size() > 0 && i== (beansBatchList.size() - 1))) {
				executeCallableList(callablesList);
				callablesList.clear();
			}
		}
		
	}
	
	private void executeCallableList(List<Callable<List<Order>>> callablesList) {
		int threadPoolSize = callablesList.size();
		if (threadPoolSize > 0) {
			executorService = Executors.newFixedThreadPool(100);
			try {
				List<Future<List<Order>>> futures = executorService.invokeAll(callablesList);
				if (Objects.nonNull(futures) && futures.size() > 0) {
					for (Future<List<Order>> future : futures) {
						try {
							List<Order> orders = future.get();
							if(Objects.nonNull(orders) && orders.size() > 0) {
								System.out.println("future objects size "+ orders.size());
								for (int i = 0; i < orders.size(); i++) {
									Order order = orders.get(i);
									System.out.println("Delivery Notifications sent for customer with email "+order.getCustomer().getEmail());
								}
							}
						}catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}

	public <T> List<List<T>> splitBeansIntoBatches(List<T> list, int size){
		AtomicInteger counter = new AtomicInteger();
		return new ArrayList<List<T>>(list.stream().collect(Collectors.groupingBy(classifier -> counter.getAndIncrement()/size)).values());
	}

}
