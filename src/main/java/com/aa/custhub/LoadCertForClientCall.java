package com.aa.custhub;

import com.aa.custhub.config.AppConfig;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Optional;

public class LoadCertForClientCall {

    @FunctionName("certtest")
    public HttpResponseMessage run(
            @HttpTrigger(name = "cert", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        context.getLogger().info("certtest HTTP trigger processed a request.");

        String url = request.getQueryParameters().get("target");
        if (url == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a target on the query string").build();
        }
        AppConfig.initSSLContext(System.getenv("certName"), true); // call once will apply to all following service calls

        String result = callService(url);
        return request.createResponseBuilder(HttpStatus.OK).body(result).build();
    }

    private String callService(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(URI.create(url), String.class);
        return responseEntity.getBody();
    }

}
