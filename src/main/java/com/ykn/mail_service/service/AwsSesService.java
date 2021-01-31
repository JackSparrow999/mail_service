package com.ykn.mail_service.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AwsSesService {

    private static final String CHAR_SET = "UTF-8";

    @Autowired
    private AmazonSimpleEmailService emailService;

    private final String sender;

    @Autowired
    public AwsSesService(AmazonSimpleEmailService emailService,
                         @Value("${email.from}") String sender) {
        this.emailService = emailService;
        this.sender = sender;
    }

    public boolean sendEmail(String email, String subject, String body) {
        try {
            int requestTimeout = 3000;
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(email))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset(CHAR_SET)
                                            .withData(body)))
                            .withSubject(new Content()
                                    .withCharset(CHAR_SET).withData(subject)))
                    .withSource(sender).withSdkRequestTimeout(requestTimeout);
            emailService.sendEmail(request);
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

}
