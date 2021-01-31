package com.ykn.mail_service.service;

import com.ykn.mail_service.dtos.EmailRequest;

public interface MailService {

    boolean sendArticlesMail(EmailRequest emailRequest);

}
