package com.petcare.petcare_api.coredomain.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "sender", "noreply@petcare.com");
    }

    @Test
    void shouldSendHtmlEmailSuccessfully() {
        String to = "recipient@example.com";
        String subject = "Teste de E-mail";
        String htmlContent = "<h1>Olá Mundo</h1>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendHtml(to, subject, htmlContent);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender, times(1)).send(mimeMessageCaptor.capture());

        verify(javaMailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldGenerateResetTokenEmailTemplate() {
        String token = "abc-123-token";
        String emailBody = emailService.resetToken(token);

        assertTrue(emailBody.contains("Redefinição de Senha"));
        assertTrue(emailBody.contains("href=\"http://localhost:3000/reset-password?token=abc-123-token\""));
    }

    @Test
    void shouldGenerateWaitingForPickupEmailTemplate() {
        String name = "João";
        String serviceName = "Banho e Tosa";
        String emailBody = emailService.waitingForPickupEmail(name, serviceName);

        assertTrue(emailBody.contains("Olá, João!"));
        assertTrue(emailBody.contains("O serviço <strong>Banho e Tosa</strong> foi finalizado com sucesso."));
        assertTrue(emailBody.contains("Seu pet está pronto para ser retirado!"));
    }
}