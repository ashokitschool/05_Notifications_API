package in.ashokit.ecomm.service;

import in.ashokit.ecomm.model.EmailDetails;

public interface EmailService {

    public String sendMailWithAttachment(EmailDetails details);
}
