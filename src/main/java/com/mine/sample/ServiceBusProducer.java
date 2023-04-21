package com.mine.sample;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.*;

import java.util.Optional;

public class ServiceBusProducer {
    @FunctionName("ServiceBusQueueSend")
    public void serviceBusQueueOutput(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @ServiceBusQueueOutput(name = "output", queueName = "poc-queue",
                    // connection has to be the env variable
                    connection = "ServiceBusQueueConnection") OutputBinding<String> output,
            final ExecutionContext context
    ) {
        String message = request.getBody().orElse("testing message");
        output.setValue(message);
        context.getLogger().info("Service Bus output queue message: " + message);
    }

    @FunctionName("ServiceBusTopicSend")
    public void serviceBusTopicOutput(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            @ServiceBusTopicOutput(name = "output", topicName = "SBTopicName", subscriptionName = "SBTopicSubName",
                    // connection has to be the env variable
                    connection = "ServiceBusTopicConnection") OutputBinding<String> output,
            final ExecutionContext context
    ) {
        String message = request.getBody().orElse("dummy message");
        output.setValue(message);
        context.getLogger().info("Service Bus output topic message: " + message);
    }
}
