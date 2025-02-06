package com.ptitB22CN539.LaptopShop.Service.SendEmail;

import com.ptitB22CN539.LaptopShop.ExceptionAdvice.DataInvalidException;
import com.ptitB22CN539.LaptopShop.ExceptionAdvice.ExceptionVariable;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(String to, String subject, String template, Map<String, Object> properties) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, StandardCharsets.UTF_8.name());
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            Context context = new Context(Locale.getDefault(), properties);
            String html = templateEngine.process(template, context);
            mimeMessageHelper.setText(html, true);
            mailSender.send(mimeMessage);
        } catch (Exception exception) {
            throw new DataInvalidException(ExceptionVariable.EMAIL_INVALID);
        }
    }
}
