package com.mine.sample;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

// check later on https://github.com/Azure/azure-functions-java-worker/blob/dev/endtoendtests/src/main/java/com/microsoft/azure/functions/endtoend/EventHubTriggerTests.java#L27
public class EventTrigger {
    @FunctionName("ehprocessor")
    public void eventHubProcessor(
            @EventHubTrigger(name = "messages",
                    eventHubName = "wingtiptoys",
                    // connection has to be the env variable
                    connection = "AzureEventHubConnection",
                    dataType = "string") String message,
            final ExecutionContext context )
    {
        context.getLogger().info(message);
    }
}
