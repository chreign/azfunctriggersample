package com.mine.sample;

import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BlobTriggerAndBinding {
    @FunctionName("blobprocessor")
    public void run(
            // internal it is queue, default concurrency is 24
            /* can be configured in host.json under queues session
            {
                "version": "2.0",
                "extensions": {
                    "queues": {
                        "maxPollingInterval": "00:00:02",
                        "visibilityTimeout" : "00:00:30",
                        "batchSize": 16,
                        "maxDequeueCount": 5,
                        "newBatchThreshold": 8,
                        "messageEncoding": "base64"
                    }
                }
            }
             */
            @BlobTrigger(name = "file",
                    dataType = "binary",
                    path = "myblob/{name}",
                    connection = "AzureWebJobsStorage") byte[] content,
            @BindingName("name") String filename,
            final ExecutionContext context
    ) {
        context.getLogger().info("Name: " + filename + " Size: " + content.length + " bytes");
    }

    // curl http://localhost:7071/api/getBlobSizeHttp?file=sample.txt
    @FunctionName("getBlobSizeHttp")
    @StorageAccount("AzureWebJobsStorage")
    public HttpResponseMessage blobSize(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Optional<String>> request,
            @BlobInput(
                    name = "file",
                    dataType = "binary",
                    path = "samples-workitems/{Query.file}")
                    byte[] content,
            final ExecutionContext context) {
        // build HTTP response with size of requested blob
        return request.createResponseBuilder(HttpStatus.OK)
                .body("The size of \"" + request.getQueryParameters().get("file") + "\" is: " + content.length + " bytes")
                .build();
    }

    // curl http://localhost:7071/api/copyBlobHttp?file=sample.txt
    @FunctionName("copyBlobHttp")
    @StorageAccount("AzureWebJobsStorage")
    public HttpResponseMessage copyBlobHttp(
            @HttpTrigger(name = "req",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS)
                    HttpRequestMessage<Optional<String>> request,
            @BlobInput(
                    name = "file",
                    dataType = "binary",
                    path = "samples-workitems/{Query.file}")
                    byte[] content,
            @BlobOutput(
                    name = "target",
                    path = "myblob/{Query.file}-CopyViaHttp")
                    OutputBinding<String> outputItem,
            final ExecutionContext context) {
        // Save blob to outputItem
        outputItem.setValue(new String(content, StandardCharsets.UTF_8));

        // build HTTP response with size of requested blob
        return request.createResponseBuilder(HttpStatus.OK)
                .body("The size of \"" + request.getQueryParameters().get("file") + "\" is: " + content.length + " bytes")
                .build();
    }
}
