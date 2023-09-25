package com.lrc.missionO2.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final String baseUrl = "https://missiono2.com/app/auth/verification.html";
    @Value("${MAIL_USERNAME}")
    private String username;

    public void sendMail(String url, String recipientEmail, String subject) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setFrom(username);

            String htmlContent = "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"background-color: #f8f8f8;\">\n" +
                    "        <tr>\n" +
                    "            <td align=\"center\" style=\"padding: 5px;\">\n" +
                    "               <h1> Mission O2</h1>" +
                    "            </td>" +
                    "        </tr>" +
                    "        <tr>" +
                    "            <td style=\"padding: 20px;\">\n" +
                    "                <h3 style=\"font-size: 24px; margin-bottom: 20px;\">Email Verification</h3>\n" +
                    "                <p style=\"font-size: 16px; margin-bottom: 20px;\">Dear User,</p>\n" +
                    "                <p style=\"font-size: 16px; margin-bottom: 20px;\">Thank you for registering with our company. please click the button below:</p>\n" +
                    "                <a href=\"" + url + "\" style=\"display: inline-block; background-color: #4caf50; color: #ffffff; font-size: 16px; text-decoration: none; padding: 10px 20px; border-radius: 5px;\">Verify Email</a>\n" +
                    "                <p style=\"font-size: 16px; margin-top: 20px;\">If you did not create an account with us, please ignore this email.</p>\n" +
                    "            </td>\n" +
                    "        </tr>\n" +
                    "    </table>";
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MailSendException ex) {
            System.out.println("exception" + ex.getFailedMessages());
        }

    }

    private String welcomeMail() {
        return """
<div class="container"
     style="
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            text-align: center;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);">
    <div class="header"
         style="
            background-color: #7bb06e;
            color: #013e01;
            padding: 20px 0;
            border-top-left-radius: 10px;
            border-top-right-radius: 10px;
            display: flex;
            align-content: center">
        <div>
            <img alt="Logo" src="cid:image" style="height: 80px; width: 80px; padding: 0 50px"/>

        </div>
        <div style="
        margin-top: 18px;
     justify-content: center;
     align-content: center;">
            <h1 style=" font-size: 28px;
            margin: 0;">Welcome to Mission O2</h1>
            <p style=" font-size: 16px;
            line-height: 1.5;
            margin: 0;">MISSION O2 – GREEN GLOBE</p>
        </div>
    </div>
    <div class="content" style="padding: 20px;">
        <p style="  font-size: 16px;
            line-height: 1.5;
            margin: 0;">
            Plant a tree and plant hope for the future.
        </p>
        <p style="  font-size: 16px;
            line-height: 1.5;
            margin: 0;">
            Thank you for showing interest in Mission O2.
        </p>

        <br>
        <p style="  font-size: 16px;
            line-height: 1.5;
            margin: 0;">
            For more Details, visit out website <a href="legalrightscouncil.in/missiono2">Mission O2</a>
        </p>
        <p style="  font-size: 16px;
            line-height: 1.5;
            margin: 0;">
            Toll Free Number 1800 309 1050
        </p>
    </div>
</div>
        """;
    }


    public void sendResetPasswordMail(String email, String otp) throws MessagingException {
        String url = baseUrl + "?task=resetPassword&code=" + otp;
        sendMail(url, email, "Reset Password");
    }

    public void sendVerificationEmail(String email, String otp) throws MessagingException {
        String url = baseUrl + "?task=verify&code=" + otp;
        sendMail(url, email, "Verification");
    }

    public void sendWelcomeMail(String email) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Welcome To Mission-O2");
            helper.setFrom(username);

            helper.setText(welcomeMail(), true);
            helper.addInline("image", new ClassPathResource("Mission-O2-Logo-1.png"));

            javaMailSender.send(message);
        } catch (MailSendException ex) {
            System.out.println("exception" + ex.getFailedMessages());
        }
    }
}
