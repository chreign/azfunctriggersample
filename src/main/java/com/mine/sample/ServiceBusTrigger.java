package com.mine.sample;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueOutput;
import com.microsoft.azure.functions.annotation.ServiceBusQueueTrigger;
import com.microsoft.azure.functions.annotation.ServiceBusTopicTrigger;

public class ServiceBusTrigger {
    @FunctionName("ServiceBusQueueTrigger")
    public void serviceBusQueueTrigger(
            @ServiceBusQueueTrigger(name = "msg",
                    queueName = "poc-queue",
                    // connection has to be the env variable
                    connection = "ServiceBusQueueConnection") String message,
            final ExecutionContext context
    ) {
        context.getLogger().info("queue receiver:" + message);
    }

    @FunctionName("ServiceBusTopicTrigger")
    public void serviceBusTopicTrigger(
            @ServiceBusTopicTrigger(name = "message", topicName = "SBTopicNameSingle", subscriptionName = "SBTopicNameSingleSubName",
                    // connection has to be the env variable
                    connection = "ServiceBusTopicConnection") String message,
            // storage queue
            @QueueOutput(name = "output", queueName = "test-servicebustopicbatch-java", connection = "AzureWebJobsStorage") OutputBinding<String> output,
            final ExecutionContext context
    ) {
        context.getLogger().info("topic receiver: " + message);
        output.setValue(message);
    }
}
