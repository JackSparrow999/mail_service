package com.ykn.mail_service.service;

import com.ykn.mail_service.dtos.Article;
import com.ykn.mail_service.dtos.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MailServiceImpl implements MailService{

    @Autowired
    AwsSesService awsSesService;

    @Override
    public boolean sendArticlesMail(EmailRequest emailRequest) {
        String name = emailRequest.getName();
        String email = emailRequest.getEmail();
        String subject = String.format("Daily dose of ", emailRequest.getFieldType(), "!");
        String body = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "\n" +
                "Hi \n" +
                emailRequest.getName() +
                "<br><br>\n" +
                "Greetings from the ykn team.\n" +
                "Here's your daily dose of \n" +
                emailRequest.getFieldType() +
                ".\n" +
                listToHtmlUnorderedList(emailRequest.getArticles()) +
                "Happy \n" +
                emailRequest.getFieldAction() +
                "!<br><br>\n" +
                "Regards<br>\n" +
                "YKN team\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        return awsSesService.sendEmail(email, subject, body);
    }

    public String listToHtmlUnorderedList(List<Article> articles){
        StringBuilder htmlList = new StringBuilder("");
        htmlList.append("<ul>\n");
        for(Article article: articles)
            htmlList.append("<li><b><a href='" + article.getUrl() + "'>" + article.getTitle() + "</a></b></li>\n");
        htmlList.append("</ul>\n");
        return htmlList.toString();
    }
}
