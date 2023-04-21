package com.mine.sample;

import com.mine.sample.config.AppConfig;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.TimerTrigger;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan(basePackages = { "com.mine.sample" })
@Import(value = { AppConfig.class })
public class TimeTrigger {
    private AnnotationConfigApplicationContext context;

    @FunctionName("azfunctiontimer")
    public void timer(
            @TimerTrigger(name = "nativeTrigger", schedule = "%schedule%") String timerInfo,
            ExecutionContext context
    ) {
        // timeInfo is a JSON string, you can deserialize it to an object using your favorite JSON library
        context.getLogger().info("azfunctiontimer is triggered: " + timerInfo);
        // start here for normal spring process like the main method
        context.getLogger().info("this is triggered by native timer trigger");
        context.getLogger().info("we can call the main function here as a normal Spring app");
        this.init();
    }

    private void init() {
        context = new AnnotationConfigApplicationContext(TimeTrigger.class);
    }
}
