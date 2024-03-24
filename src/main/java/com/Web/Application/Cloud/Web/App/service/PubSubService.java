package com.Web.Application.Cloud.Web.App.service;

import com.Web.Application.Cloud.Web.App.entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.Web.Application.Cloud.Web.App.util.JwtTokenGenerator.generateJwtToken;

@Service
public class PubSubService {

    private final String projectId;
    private final String topicId;

    @Autowired
    public PubSubService(@Value("${pubsub.projectId}") String projectId,
                         @Value("${pubsub.topicId}") String topicId) {
        this.projectId = projectId;
        this.topicId = topicId;
    }

    public void publishUserInformation(User user) {
        Publisher publisher = null;
        try {
            TopicName topicName = TopicName.of(projectId, topicId);
            publisher = Publisher.newBuilder(topicName).build();



            String jsonPayload = String.format("{\"UserName\":\"%s\",\"UserId\":\"%s\",\"JwtToken\":\"%s\"}",
                    user.getUsername(), user.getId().toString(),user.getJwtToken());


            PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
                    .setData(ByteString.copyFromUtf8(jsonPayload))
                    .build();
            



            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();
            System.out.println("Published message ID: " + messageId);
        }
        catch (Exception e) {

            e.printStackTrace();
        }
        finally {
            if (publisher != null) {
                try {
                    publisher.shutdown();
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
