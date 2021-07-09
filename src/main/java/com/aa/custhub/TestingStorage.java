package com.aa.custhub;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class TestingStorage {
    private static String endpoint = "https://pocstorageaccount6476.blob.core.windows.net";

    @FunctionName("storage")
    public HttpResponseMessage run(
            @com.microsoft.azure.functions.annotation.HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("storage HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        doSomething(query);

        return request.createResponseBuilder(HttpStatus.OK).body("ok").build();

    }

    private void doSomething(String container) {
        BlobContainerClient containerClient = new BlobContainerClientBuilder()
                .endpoint(endpoint)
                .credential(new DefaultAzureCredentialBuilder().build())
                .containerName(container)
                .buildClient();
        for (BlobItem blobItem : containerClient.listBlobs()) {
            System.out.println("name of blob is: " + blobItem.getName());
        }
    }
}
