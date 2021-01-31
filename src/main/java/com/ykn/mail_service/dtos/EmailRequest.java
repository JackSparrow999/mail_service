package com.ykn.mail_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class EmailRequest {

    String trackerId;

    String userId;

    String name;

    String email;

    //like knowledge
    String fieldType;

    //like learning
    String fieldAction;

    Long sendAfterDate;

    List<Article> articles = new ArrayList<>();

    String query;

    Long offset;

}
