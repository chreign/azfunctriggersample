package com.mine.sample;

import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;

import java.time.LocalDateTime;

public class EventProducer {
    @FunctionName("sendTime")
    @EventHubOutput(name = "event", eventHubName = "wingtiptoys", connection = "AzureEventHubConnection")
    public String sendTime(
            @TimerTrigger(name = "sendTimeTrigger", schedule = "0 */1 * * * *") String timerInfo)  {
        // sent to event hub at schedule
        return LocalDateTime.now().toString();
    }
}
