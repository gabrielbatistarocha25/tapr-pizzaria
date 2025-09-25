package com.example.authservice.infrastructure.mail;

import com.example.authservice.application.ports.MailSender;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class SmtpMailSender implements MailSender {

    private static final Logger logger = LoggerFactory.getLogger(SmtpMailSender.class);
    private final JavaMailSender javaMailSender;

    @Override
    public void sendMagicLink(String to, String magicUrl, Instant expiresAt) {
        try {
            logger.info("Enviando magic link via SMTP para o e-mail: {}", to);
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@authservice.com");
            message.setTo(to);
            message.setSubject("Seu Link Mágico de Login");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm:ss")
                                                          .withZone(ZoneId.systemDefault());
            String formattedExpiresAt = formatter.format(expiresAt);

            String body = String.format(
                "Olá!\n\nUse o link abaixo para fazer login. Este link expira em %s.\n\n%s\n\nSe você não solicitou este link, por favor, ignore este e-mail.",
                formattedExpiresAt,
                magicUrl
            );
            message.setText(body);
            
            javaMailSender.send(message);
        } catch (Exception e) {
            logger.error("Falha ao enviar e-mail via SMTP para: {}", to, e);
        }
    }
}