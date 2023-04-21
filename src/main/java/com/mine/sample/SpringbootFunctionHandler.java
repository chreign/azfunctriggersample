package com.mine.sample;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.TimerTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.Optional;
import java.util.UUID;

public class SpringbootFunctionHandler extends FunctionInvoker<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(SpringbootFunctionHandler.class);

    @FunctionName("timer")
    public void timer(
            @TimerTrigger(name = "springbootTrigger", schedule = "%schedule%") String timerInfo,
            ExecutionContext context
    ) {
        context.getLogger().info("azfunctionspringboot is triggered: " + timerInfo);
        logger.info("this is triggered during timer trigger");
        String result = handleRequest(UUID.randomUUID().toString(), context);
        logger.info("anything happened? " + result);
    }

    @FunctionName("hello")
    public HttpResponseMessage hello(
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

    @FunctionName("echo")
    public String execute(@HttpTrigger(name = "req", methods = {HttpMethod.GET,
            HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
                          ExecutionContext context) {
        return handleRequest(request.getBody().get(), context);
    }

    @FunctionName("ssl")
    public String test(@HttpTrigger(name = "req", methods = {HttpMethod.GET,
            HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
                          ExecutionContext context) {
        String url = request.getQueryParameters().get("target");
        if (url == null) {
            return handleRequest("bad requests", context);
        }
        return handleRequest(url, context);
    }
}
