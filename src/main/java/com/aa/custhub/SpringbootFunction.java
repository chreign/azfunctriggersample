package com.aa.custhub;

import com.aa.custhub.config.AppConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.messaging.Message;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.util.function.Function;

@SpringBootApplication
public class SpringbootFunction {
    private static final Logger logger = LoggerFactory.getLogger(SpringbootFunction.class);

    @Value("${testEnv}")
    private String testVariable;

    public static void main(String[] args) {
        SpringApplication.run(SpringbootFunction.class, args);
    }

    // the method name has to be the same as function name
    @Bean
    public Function<String, String> timer() {
        logger.info("this is triggered during init include below");
        logger.info("------not suppose to process the job here------");
        logger.info("this is triggered during init include above");
        return input -> {
            logger.info("------trigger the job here------");
            logger.info("testVariable value is: " + testVariable);
            return Boolean.TRUE.toString();
        };
    }

    @Bean
    public Function<String, String> hello() {
        logger.info("this is triggered during init and http trigger in spring boot");
        return user -> "Hello, " + user;
    }

    @Bean
    public Function<Message<String>, String> echo() {
        return message -> message.getPayload();
    }

    @Bean
    public Function<String, String> ssl(RestTemplate restTemplateWithTLS) {
        return url -> {
            ResponseEntity<String> responseEntity = restTemplateWithTLS.getForEntity(URI.create(url), String.class);
            System.out.println(responseEntity.getBody());
            return responseEntity.getBody();
        };
    }

    @Bean
    public RestTemplate restTemplateWithTLS(@Value("${certName}") String certName) throws Exception {
//        String keyVaultUri = System.getenv("AZURE_KEYVAULT_URI");
//        KeyStore azureKeyVaultKeyStore = KeyStore.getInstance("AzureKeyVault");
//        KeyVaultLoadStoreParameter parameter = new KeyVaultLoadStoreParameter(
//                keyVaultUri
//        );
//        azureKeyVaultKeyStore.load(parameter);
//
//        CertificateClient certificateClient = new CertificateClientBuilder()
//                .vaultUrl(keyVaultUri)
//                .credential(new DefaultAzureCredentialBuilder().build())
//                .buildClient();
//        KeyVaultCertificate certificate = certificateClient.getCertificate(certName);
//        System.out.printf("Recevied certificate with name \"%s\", version %s and secret id %s%n",
//                certificate.getProperties().getName(), certificate.getProperties().getVersion(), certificate.getSecretId());
//
//        SSLContext sslContext = SSLContexts.custom()
//                .loadTrustMaterial(azureKeyVaultKeyStore, new TrustSelfSignedStrategy())
//                .build()
//                ;
        SSLContext sslContext = AppConfig.initSSLContext(certName, false);
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext,
                (hostname, session) -> true);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(socketFactory)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(requestFactory);
    }
}
