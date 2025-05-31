package com.elitefolk.authservice.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

/**
 * Service class for sending emails using AWS SES.
 */
@Service
public class AwsEmailService {

    @Value("${aws.ses.from}")
    private String fromEmail;

    private final String region;

    private final SesClient sesClient;

    /**
     * Constructor for AwsEmailService.
     *
     * @param region          AWS region for SES
     * @param accessKeyId     AWS access key ID
     * @param secretAccessKey AWS secret access key
     */
    public AwsEmailService(@Value("${aws.ses.region}") String region,
                           @Value("${aws.ses.accessKeyId}") String accessKeyId,
                           @Value("${aws.ses.secretAccessKey}") String secretAccessKey) {
        this.region = region;
        sesClient = SesClient.builder()
                .region(Region.of(this.region))
                .credentialsProvider(() -> AwsBasicCredentials.create(accessKeyId, secretAccessKey))
                .build();
    }

    /**
     * Sends an email using AWS SES.
     *
     * @param subject  Subject of the email
     * @param body     Body of the email
     * @param toEmail  Recipient's email address
     * @return Response message indicating success or failure
     */
    public String sendEmail(String subject, String body, String toEmail) {
        Destination toEmailDestination = Destination.builder()
                .toAddresses(toEmail)
                .build();

        Content subjectContent = Content.builder()
                .data(subject)
                .build();

        Content bodyContent = Content.builder()
                .data(body)
                .build();

        Message message = Message.builder()
                .subject(subjectContent)
                .body(Body.builder().text(bodyContent).build())
                .build();

        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .source(fromEmail)
                .destination(toEmailDestination)
                .message(message)
                .build();

        SendEmailResponse sendEmailResponse = sesClient.sendEmail(sendEmailRequest);
        String response = sendEmailResponse.messageId();
        if (sendEmailResponse.sdkHttpResponse().isSuccessful()) {
            response = response + " - Email sent successfully to " + toEmail;
            System.out.println("Email sent successfully to " + toEmail);
        } else {
            response = response + " - Failed to send email with status " + sendEmailResponse.sdkHttpResponse().statusCode();
            System.err.println("Failed to send email: " + sendEmailResponse.sdkHttpResponse().statusCode());
        }
        return response;
    }
}
