package gotcha_common.mail;

import gotcha_common.exception.CustomException;
import gotcha_common.exception.exceptionCode.GlobalExceptionCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@EnableAsync
@Service
@Slf4j
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
            String htmlContent = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String finalContent = htmlContent.replace("{{CODE}}", code);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(finalContent, true);
            helper.setFrom(new InternetAddress(senderEmail, "Gotcha!", "UTF-8"));

            javaMailSender.send(message);
        } catch (IOException | MessagingException e) {
            log.error("메일 전송 중 예외 발생", e); // <- 추가
            throw new CustomException(GlobalExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
