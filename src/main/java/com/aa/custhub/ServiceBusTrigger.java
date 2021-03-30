package com.aa.custhub;

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
                    queueName = "sbq-postal-address",
                    connection = "ServiceBusQueueConnection") String message,
            final ExecutionContext context
    ) {
        context.getLogger().info("receiver" + message);
    }

    @FunctionName("ServiceBusTopicTrigger")
    public void serviceBusTopicTrigger(
            @ServiceBusTopicTrigger(name = "message", topicName = "SBTopicNameSingle", subscriptionName="SBTopicNameSingleSubName",connection = "ServiceBusTopicConnection") String message,
            @QueueOutput(name = "output", queueName = "test-servicebustopicbatch-java", connection = "AzureWebJobsStorage") OutputBinding<String> output,
            final ExecutionContext context
    ) {
        context.getLogger().info("Java Service Bus Topic trigger function processed a message: " + message);
        output.setValue(message);
    }
}
