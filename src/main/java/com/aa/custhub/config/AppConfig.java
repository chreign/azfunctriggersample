package com.aa.custhub.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.certificates.CertificateClient;
import com.azure.security.keyvault.certificates.CertificateClientBuilder;
import com.azure.security.keyvault.certificates.models.KeyVaultCertificate;
import com.azure.security.keyvault.jca.KeyVaultJcaProvider;
import com.azure.security.keyvault.jca.KeyVaultLoadStoreParameter;

import com.azure.security.keyvault.jca.implementation.shaded.org.apache.http.ssl.SSLContexts;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Enumeration;

@Configuration
@PropertySources(value = {@PropertySource(ignoreResourceNotFound = true, value = "classpath:/application.properties")})
public class AppConfig {

    private static String keyVaultUri = System.getenv("AZURE_KEYVAULT_URI");
    @Value("${testEnv}")
    private String value;
    @Value("${testSecret}")
    private String secret;

    @Autowired
    private Environment env;

    @Bean
    public static SSLContext initSSLContext(@Value("${certName}") String certName, @Value("${isSslcontextDefault:false}") boolean setToDefault) throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException, KeyManagementException, UnrecoverableKeyException {

        KeyStore trustStore = getCertFromJca();
        KeyStore keyStore = getCertFromCertSecret(certName);

        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, "".toCharArray())
                .loadTrustMaterial(trustStore, null)
                .build();
        if (setToDefault) {
            SSLContext.setDefault(sslContext);
        }
        return sslContext;
    }

    private static KeyStore getCertFromJca() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        KeyVaultJcaProvider provider = new KeyVaultJcaProvider();
        Security.addProvider(provider);

        KeyStore cert = KeyStore.getInstance("AzureKeyVault");
        // auth by managed-identity
        KeyVaultLoadStoreParameter parameter = new KeyVaultLoadStoreParameter(
                keyVaultUri//,
//                System.getenv("azure.keyvault.managed-identity")
        );
        // another way of auth
//        parameter = new KeyVaultLoadStoreParameter(
//                keyVaultUri,
//                System.getenv("AZURE_TENANT_ID"),
//                System.getenv("AZURE_CLIENT_ID"),
//                System.getenv("AZURE_CLIENT_SECRET"));
        cert.load(parameter);
        System.out.println("-----------getCertFromJca------------");
        System.out.println(cert.getType());
        System.out.println(cert.size());
        Enumeration<String> enumeration = cert.aliases();
        while (enumeration.hasMoreElements()) {
            System.out.println(enumeration.nextElement());
        }
        return cert;
    }

    // works when enable JCA, not working without
    private static KeyStore getCertFromCertClient(String certName) throws KeyStoreException, CertificateException, NoSuchAlgorithmException {

        CertificateClient certificateClient = new CertificateClientBuilder()
                .vaultUrl(keyVaultUri)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        KeyVaultCertificate retrievedCertificate = certificateClient.getCertificate(certName);
        KeyStore keyStore = KeyStore.getInstance("AzureKeyVault");
        try (InputStream inputStream = new ByteArrayInputStream(retrievedCertificate.getCer())) {
            keyStore.load(inputStream, "".toCharArray());
            System.out.println("----------getCertFromCertClient-------------");
            Enumeration<String> enumeration = keyStore.aliases();
            while (enumeration.hasMoreElements()) {
                System.out.println(enumeration.nextElement());
            }
            System.out.println(keyStore.getType());
            System.out.println(keyStore.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-----------------------");
        System.out.println("details of keyvalult cert");
        System.out.print("length: ");
        System.out.println(retrievedCertificate.getCer().length);
        System.out.print("id: ");
        System.out.println(retrievedCertificate.getId());
        System.out.print("keyid: ");
        System.out.println(retrievedCertificate.getKeyId());
        System.out.print("name: ");
        System.out.println(retrievedCertificate.getName());
        System.out.print("secret id: ");
        System.out.println(retrievedCertificate.getSecretId());
        System.out.print("x509: ");
        System.out.println(new String(retrievedCertificate.getProperties().getX509Thumbprint(), StandardCharsets.UTF_8));
//        System.out.println(Base64.getEncoder().encodeToString(retrievedCertificate.getCer()));
        return keyStore;
    }

    private static KeyStore getCertFromCertSecret(String certName) throws KeyStoreException, CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUri)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        System.out.println("----------getCertFromCertSecret-------------");
//        System.out.println(secretClient.getSecret(certName).getValue());
        byte[] bytes = Base64.getDecoder().decode(secretClient.getSecret(certName).getValue());
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            keyStore.load(inputStream, "".toCharArray());
            System.out.println(keyStore.getType());
            System.out.println(keyStore.size());
            Enumeration<String> enumeration = keyStore.aliases();
            while (enumeration.hasMoreElements()) {
                System.out.println(enumeration.nextElement());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    @Bean
    public void test() {
        System.out.println("spring triggered");
        System.out.println("env testEnv: " + env.getProperty("testEnv"));
        System.out.println("env testSecret: " + env.getProperty("testSecret"));
        System.out.println("value: " + value);
        System.out.println("secret: " + secret);
    }
}
