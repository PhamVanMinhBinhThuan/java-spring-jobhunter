package vn.hoidanit.jobhunter.service;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private final MailSender mailSender;
    
    public EmailService(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("phamminhpro1405@gmail.com");
        msg.setSubject("Test Email");
        msg.setText("This is a test email.");
        this.mailSender.send(msg);
    }
}
