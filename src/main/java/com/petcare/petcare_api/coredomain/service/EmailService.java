package com.petcare.petcare_api.coredomain.service;

import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Value("${spring.mail.username}")
    private String sender;

    @Async
    public void sendHtml(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new InternalException("Erro ao enviar email");
        }
    }

    public String resetToken(String token) {
        return """
                <html>
                  <head>
                    <meta charset="UTF-8">
                    <style>
                      body {
                        margin: 0;
                        padding: 0;
                        background: linear-gradient(135deg, #2536a7, #5c6ff5);
                        font-family: 'Arial', sans-serif;
                        color: #fff;
                      }
                      .container {
                        background: #fff;
                        color: #333;
                        max-width: 500px;
                        margin: 40px auto;
                        border-radius: 10px;
                        padding: 40px 30px;
                        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
                      }
                      .title {
                        font-size: 24px;
                        font-weight: bold;
                        text-align: center;
                        margin-bottom: 25px;
                        color: #2536a7;
                      }
                      .content {
                        font-size: 16px;
                        line-height: 1.6;
                      }
                      .btn-container {
                        text-align: center;
                        margin: 30px 0;
                      }
                      .footer {
                        font-size: 12px;
                        text-align: center;
                        color: #888;
                        margin-top: 30px;
                      }
                    </style>
                  </head>
                  <body>
                    <div class="container">
                      <div class="title">Redefinição de Senha</div>
                      <div class="content">
                        <p>Olá,</p>
                        <p>Recebemos uma solicitação para redefinir sua senha.</p>
                        <p>Clique no botão abaixo para continuar:</p>
                
                        <div class="btn-container">
                          <a href="http://localhost:3000/reset-password?token=%s"
                             style="background-color: #2563eb; color: #ffffff; text-decoration: none; padding: 14px 28px; border-radius: 6px; font-size: 16px; font-weight: bold; display: inline-block;">
                            Redefinir Senha
                          </a>
                        </div>
                
                        <p>Se você não solicitou essa redefinição, pode ignorar este e-mail.</p>
                      </div>
                
                      <div class="footer">
                        Este é um e-mail automático. Não é necessário respondê-lo.
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(token);
    }
}
