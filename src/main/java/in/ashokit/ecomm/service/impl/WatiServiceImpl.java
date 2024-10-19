package in.ashokit.ecomm.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import in.ashokit.ecomm.model.WatiParameters;
import in.ashokit.ecomm.model.WatiRequest;
import in.ashokit.ecomm.model.WatiResponse;
import in.ashokit.ecomm.service.WatiService;

@Service
public class WatiServiceImpl implements WatiService{

	@Value("${wati.notification.template.name}")
	private String watiNotificationTemplateName;
	
	@Value("${wati.payment.reminder.template.name}")
	private String watiPaymentReminderTemplateName;

	@Value("${wati.send.template.msg.url}")
	private String watiTemplateMsgApiUrl;

	@Value("${wati.token}")
	private String token;
	
	
	@Override
	public WatiResponse sendDeliveryNotification(String phno,String name, String orderTrackingNumber){
		System.out.println("WatiService.sendDeliveryNotification()");
        RestTemplate rt = new RestTemplate();
        String apiUrl = watiTemplateMsgApiUrl + "?whatsappNumber=91" + phno;

        WatiParameters nameParams = new WatiParameters();
        nameParams.setName("name");
        nameParams.setValue(name);
        
        WatiParameters orderTrackingNumberParams = new WatiParameters();
        orderTrackingNumberParams.setName("order_number");
        orderTrackingNumberParams.setValue(orderTrackingNumber);

        WatiRequest requestDTO = new WatiRequest();
        requestDTO.setTemplate_name(watiNotificationTemplateName);
        requestDTO.setBroadcast_name(watiNotificationTemplateName);
        requestDTO.setParameters(Arrays.asList(nameParams,orderTrackingNumberParams));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<WatiRequest> requestEntity = new HttpEntity<>(requestDTO, headers);
        ResponseEntity<WatiResponse> response = rt.postForEntity(apiUrl, requestEntity, WatiResponse.class);
        response.getBody().setName(name);
        response.getBody().setOrderNumber(orderTrackingNumber);
        System.out.println("Whatsup Notification sent successfully");
        return response.getBody();
    }
	
	@Override
	public WatiResponse sendPaymentReminder(String phno,String name, String orderTrackingNumber){
		System.out.println("WatiService.sendPaymentReminder()");
        RestTemplate rt = new RestTemplate();
        String apiUrl = watiTemplateMsgApiUrl + "?whatsappNumber=91" + phno;

        WatiParameters nameParams = new WatiParameters();
        nameParams.setName("name");
        nameParams.setValue(name);
        
        WatiParameters orderTrackingNumberParams = new WatiParameters();
        orderTrackingNumberParams.setName("order_number");
        orderTrackingNumberParams.setValue(orderTrackingNumber);

        WatiRequest requestDTO = new WatiRequest();
        requestDTO.setTemplate_name(watiPaymentReminderTemplateName);
        requestDTO.setBroadcast_name(watiPaymentReminderTemplateName);
        requestDTO.setParameters(Arrays.asList(nameParams,orderTrackingNumberParams));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token);

        HttpEntity<WatiRequest> requestEntity = new HttpEntity<>(requestDTO, headers);
        ResponseEntity<WatiResponse> response = rt.postForEntity(apiUrl, requestEntity, WatiResponse.class);
        response.getBody().setName(name);
        response.getBody().setOrderNumber(orderTrackingNumber);
        System.out.println("Whatsup Reminder sent successfully");
        System.out.println(response.getBody());
        return response.getBody();
    }

}
