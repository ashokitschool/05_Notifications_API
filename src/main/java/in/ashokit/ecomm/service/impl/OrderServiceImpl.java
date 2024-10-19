package in.ashokit.ecomm.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.ashokit.ecomm.constant.OrderStatus;
import in.ashokit.ecomm.entity.Order;
import in.ashokit.ecomm.repository.OrderRepository;
import in.ashokit.ecomm.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepo;

	private LocalDate today = LocalDate.now();

	@Override
	public Order updateOrder(Order order) {
		return orderRepo.save(order);
	}

	@Override
	public void deleteInvoiceFromLocalStorage(String orderTrackingNumber) {
		try {
			Files.deleteIfExists(Paths.get(getInvoiceName(orderTrackingNumber)));
			System.out.println("Invoice deleted...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Order> getOrdersEligibleForDelivery() {
		return orderRepo.findByOrderDeliveryDate(today);
	}
	
	@Override
	public List<Order> getUnconfimedOrders() {
		return orderRepo.findByStatusNot(OrderStatus.CONFIRMED.name());
	}

	@Override
	public String getInvoiceName(String orderTrackingNumber) {
		return "invoices/invoice"+"-"+orderTrackingNumber+".pdf";
	}

}
