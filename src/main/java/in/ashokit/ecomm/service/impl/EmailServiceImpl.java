package in.ashokit.ecomm.service.impl;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import in.ashokit.ecomm.model.EmailDetails;
import in.ashokit.ecomm.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService{

	private JavaMailSender javaMailSender;

	public EmailServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender= javaMailSender;
	}

	@Value("${spring.mail.username}") private String sender;

	@Override
	public String sendMailWithAttachment(EmailDetails details) {
		if(StringUtils.isNotEmpty(details.getAttachment())) {
			// Creating a mime message
			MimeMessage mimeMessage	= javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper;
			try {
				mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
				mimeMessageHelper.setFrom(sender);
				mimeMessageHelper.setTo(details.getRecipient());
				mimeMessageHelper.setText(details.getMsgBody());
				mimeMessageHelper.setSubject(details.getSubject());
				// Adding the attachment
				FileSystemResource file	= new FileSystemResource(new File(details.getAttachment()));
				mimeMessageHelper.addAttachment(file.getFilename(), file);
				// Sending the mail
				javaMailSender.send(mimeMessage);
				return "Mail sent Successfully";
			}catch (MessagingException e) {
				// Display message when exception occurred
				return "Error while sending mail!!!";
			}
		}
		return null;
	}
}
