package com.expedia.adaptivealerting.modelservice.dao.es;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.google.common.base.Supplier;
import lombok.Data;
import lombok.Getter;
import lombok.val;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vc.inreach.aws.request.AWSSigner;
import vc.inreach.aws.request.AWSSigningRequestInterceptor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Configuration
@Data
@ConfigurationProperties(prefix = "datasource-es")
public class ElasticSearchConfig {

    @Value("${index.name}")
    private String indexName;
    @Value("${create.index.if.not.found:false}")
    private boolean createIndexIfNotFound;
    @Value("${doctype}")
    private String docType;
    @Value("${urls}")
    private String urls;
    @Value("${connection.timeout}")
    private int connectionTimeout;
    @Value("${max.connection.idletime}")
    private int maxConnectionIdleTime;
    @Value("${max.total.connection}")
    private int maxTotalConnection;
    @Value("${read.timeout}")
    private int readTimeout;
    @Value("${request.compression:false}")
    private boolean requestCompression;
    @Value("${username:@null}")
    private String username;
    @Value("${password:@null}")
    private String password;

    @Getter
    @Value("${enabled:false}")
    private boolean enabled;

    private RestHighLevelClient client;


    @PostConstruct
    public void init() {
        RestClientBuilder builder  = RestClient
                .builder(HttpHost.create(urls))
                .setRequestConfigCallback( req -> {
                    req.setConnectionRequestTimeout(connectionTimeout);
                    req.setConnectTimeout(connectionTimeout);
                    req.setSocketTimeout(connectionTimeout);
                    return req;
                }).setMaxRetryTimeoutMillis(connectionTimeout)
                .setHttpClientConfigCallback(req -> {
                            req.setMaxConnTotal(maxTotalConnection);
                            req.setMaxConnPerRoute(500);
                            return req;
                });
        addAWSRequestSignerInterceptor(builder);
        client = new RestHighLevelClient(builder);
    }
    
    private void addAWSRequestSignerInterceptor(RestClientBuilder clientBuilder) {
        AWSSigningRequestInterceptor signingInterceptor = getAWSRequestSignerInterceptor();
        clientBuilder.setHttpClientConfigCallback(
                clientConf -> clientConf.addInterceptorLast(signingInterceptor));
    }
    
    private AWSSigningRequestInterceptor getAWSRequestSignerInterceptor() {
        final Supplier<LocalDateTime> clock = () -> LocalDateTime.now(ZoneOffset.UTC);
        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
        val awsSigner = new AWSSigner(credentialsProvider, "us-west-2", "es", clock);
        return new AWSSigningRequestInterceptor(awsSigner);
    }

    @PreDestroy
    public void destroy() throws IOException {
        client.close();
    }

    @Bean
    public RestHighLevelClient getClient() {
        return client;
    }
}
