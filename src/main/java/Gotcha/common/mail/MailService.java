package Gotcha.common.mail;

import Gotcha.common.exception.CustomException;
import Gotcha.common.exception.exceptionCode.GlobalExceptionCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RequiredArgsConstructor
@EnableAsync
@Service
public class MailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value(("${mail.subject}"))
    private String subject;

    @Async
    public void sendMail(String email, String code) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-verification.html");
            String htmlContent = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

            String finalContent = htmlContent.replace("{{CODE}}", code);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(finalContent, true);
            helper.setFrom(new InternetAddress(senderEmail, "Gotcha!", "UTF-8"));

            javaMailSender.send(message);
        } catch (IOException | MessagingException e) {
            throw new CustomException(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
