package org.example.campconnect.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender emailSender;

    public void sendEmail(String to, String subject, String text) {
        if (emailSender == null) {
            System.out.println("Mock Email to: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Text: " + text);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage(); 
            message.setFrom("noreply@campconnect.com");
            message.setTo(to); 
            message.setSubject(subject); 
            message.setText(text);
            emailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }
}
