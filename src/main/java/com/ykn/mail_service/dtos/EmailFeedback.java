package com.ykn.mail_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class EmailFeedback {

    EmailRequest emailRequest;

    Boolean success;

}
