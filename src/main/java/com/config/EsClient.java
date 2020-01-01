package com.config;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xiaozhi on 2020/1/1.
 */

@Repository("esClient")
public class EsClient {


    private static final Map<String, TransportClient> POOL = new ConcurrentHashMap<>();

    private static TransportClient client;

    @Value("${elasticsearch.client.host}")
    private String clusterUrl;

    @Value("${elasticsearch.client.name}")
    private String clusterName;

    @PostConstruct
    public void connectDefault() {
        client = create(clusterUrl);
    }

    @PreDestroy
    public void cleanup() {
        if (client != null){
            client.close();
        }
        POOL.values().forEach(TransportClient::close);
    }

    public TransportClient getClient() {
        return client;
    }

    public TransportClient getClient(String url) {
        TransportClient client = POOL.get(url);
        if (client != null) {
            return client;
        }
        client = create(url);
        POOL.put(url, client);
        return client;
    }

    ///////////////////////
    // private functions
    ///////////////////////
    private TransportClient create(String url) {
        TransportClient client = null;
        try {
            if (StringUtils.contains(url, "://")) {
                url = url.split("://")[1];
            }
            String[] hosts = StringUtils.substringBefore(url, ":").split(",");
            String port = StringUtils.substringAfter(url, ":");

            Settings settings = Settings.builder().put("cluster.name", clusterName).build();
            client = new PreBuiltTransportClient(settings);
            for (String host : hosts) {
                client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), Integer
                        .parseInt(port)));
            }

            System.out.println("success to create client of elasticsearch server cluster["+clusterName+"] url["+url+"]");
            if(client.connectedNodes().size() == 0){
                System.out.println("the ES client doesn't connect to the ES server cluster["+clusterName+"] url["+url+"]");
            }
        } catch (Exception e) {
            System.out.println("failed to create client of elasticsearch server cluster["+e+"] url["+url+"].");
        }
        return client;
    }
}

