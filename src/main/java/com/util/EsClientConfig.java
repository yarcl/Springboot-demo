package com.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaozhi on 2020/1/1.
 */

@Slf4j
/*@Configuration*/
public class EsClientConfig {


    @Value("${elasticsearch.client.host}")
    private String host;

    @Value("${elasticsearch.client.port}")
    private Integer port;

    @Value("${elasticsearch.client.scheme}")
    private String scheme;

    // @Value("${elasticsearch.client.hosts}")
    private String hosts = null;

    @Bean
    ThreadPoolExecutor exportBigThreadPool() {
        return new ThreadPoolExecutor(2, 16, 10, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>());
    }

    @Bean
    RestHighLevelClient getRestHighLevelClient() {
        RestClientBuilder builder;
        if (StringUtils.isNotBlank(hosts)) {
            String[] split = hosts.split(",");
            List<HttpHost> httpHostList = new ArrayList<>();
            for (String ho : split) {
                String[] split1 = ho.split("://");
                String scheme = split1[0];
                String[] split2 = split1[1].split(":");
                String port = split2[1];
                String host = split2[0];
                httpHostList.add(new HttpHost(host, Integer.parseInt(port), scheme));
            }
            builder = RestClient.builder(httpHostList.toArray(new HttpHost[]{}));
        } else {
            builder = RestClient.builder(
                    new HttpHost(host, port, scheme));
        }
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.addInterceptorFirst(
                    (HttpRequestInterceptor) (request, context) -> {
                        log.info(request.toString());
                        if (request instanceof HttpEntityEnclosingRequest) {
                            HttpEntity entity =
                                    ((HttpEntityEnclosingRequest) request)
                                            .getEntity();
                            if (entity != null) {
                                log.info(IOUtils.toString(entity.getContent(),
                                        "utf-8"));
                            }
                        }
                    });
            return httpClientBuilder;
        });
        builder.setMaxRetryTimeoutMillis(300000);
        return new RestHighLevelClient(
                builder.build());
    }

}
