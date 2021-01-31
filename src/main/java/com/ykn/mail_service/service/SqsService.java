package com.ykn.mail_service.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ykn.mail_service.dtos.EmailFeedback;
import com.ykn.mail_service.dtos.EmailRequest;
import com.ykn.mail_service.exceptions.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SqsService {

    @Autowired
    AmazonSQS sqs;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    MailService mailService;

    @Autowired
    ObjectMapper objectMapper;

    private final String mailQueue = "MailQueue.fifo";

    @Value("${cloud.aws.end-point.feedback.uri}")
    private String feedbackQueue;

    public void feedbackQueueProducer(String payload){
        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(feedbackQueue)
                .withMessageBody(payload)
                .withMessageGroupId("feedback");

        try{
            sqs.sendMessage(sendMessageRequest);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    @SqsListener(value = mailQueue, deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void emailRequestConsumer(String message){
        EmailRequest emailRequest = null;
        try {
            emailRequest = objectMapper.readValue(message, EmailRequest.class);
        } catch (JsonProcessingException e) {
            throw new AppException("Parsing email request failed!");
        }

        EmailFeedback emailFeedback = new EmailFeedback(emailRequest, true);

        if(emailRequest.getSendAfterDate() > Instant.now().toEpochMilli()){
            //if feedback success was false then the article finder service will regenerate the email
            emailFeedback.setSuccess(false);
            try {
                feedbackQueueProducer(objectMapper.writeValueAsString(emailFeedback));
            } catch (JsonProcessingException e) {
                throw new AppException("Serializing feedback request failed!");
            }
            return;
        }

        Boolean success = mailService.sendArticlesMail(emailRequest);

        if(!success)
            throw new AppException("SES failed to send email!");

        emailFeedback.getEmailRequest().setOffset(emailFeedback.getEmailRequest().getOffset() + emailFeedback.getEmailRequest().getArticles().size());

        try {
            feedbackQueueProducer(objectMapper.writeValueAsString(emailFeedback));
        } catch (JsonProcessingException e) {
            throw new AppException("Serializing feedback request failed!");
        }
    }

}
