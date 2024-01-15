package Coria.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailServicio {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String from, String to, String subject, String body) {

        SimpleMailMessage MimeMessage = new SimpleMailMessage();

        MimeMessage.setFrom(from);
        MimeMessage.setTo(to);
        MimeMessage.setSubject(subject);
        MimeMessage.setText(body);

        javaMailSender.send(MimeMessage);
    }
}