# ykn

This application allowsa subscribed/unsubscribed user to get emails of good articles at regular intervals. We have 3 microservices and 3 AWS sqs to execute this:

User Service (https://github.com/JackSparrow999/user_service):

- Authentication
- workflow trigger
-> Written in **Spring Boot**

Search Service (https://github.com/JackSparrow999/search_service):

- Creation of search event out of API call (Search SQS)
- Processes search event using Google Search APIs
- Creation of mail event (Mail SQS)
- Creation of search event out of feedback event (Feedback SQS)
-> Written in **Spring Webflux**

Mail Service (https://github.com/JackSparrow999/mail_service):

- Processed mail event using AWS SES
- Creation of feedback event (Feedback SQS)
-> Written in **Spring Boot**

Please check the Architecture.jpg file for more info

Note: Unlike a DAG which runs at fixed intervals this archtecture allows us to trigger a mail at any interval that the user selects. Although CPU cycles are wasted here if the mail's time to send has not been crossed but during heavy loads the system corrects itself beacause of the longer wait time for each queue event.