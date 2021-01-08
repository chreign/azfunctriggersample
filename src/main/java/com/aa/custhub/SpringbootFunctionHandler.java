package com.aa.custhub;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import java.util.UUID;

public class SpringbootFunctionHandler extends AzureSpringBootRequestHandler<String, Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(SpringbootFunctionHandler.class);

    @FunctionName("timer")
    public void timer(
            @TimerTrigger(name = "springbootTrigger", schedule = "%schedule%") String timerInfo,
            ExecutionContext context
    ) {
        context.getLogger().info("azfunctionspringboot is triggered: " + timerInfo);
        logger.info("this is triggered during timer trigger");
        Boolean result = handleRequest(UUID.randomUUID().toString(), context);
        logger.info("anything happened? " + result);
    }

    @FunctionName("hello")
    public HttpResponseMessage execute(
            @HttpTrigger(name = "request", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<String> request,
            ExecutionContext context) {
        logger.info("this is triggered during http trigger in spring boot");
        String user = request.getQueryParameters().get("user");
        context.getLogger().info("Greeting user name: " + user);
        return request
                .createResponseBuilder(HttpStatus.OK)
                .body(handleRequest(user, context))
                .header("Content-Type", "application/json")
                .build();
    }
}
