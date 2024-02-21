package com.qiuzhitech.onlineshopping.configuration;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

    @Bean
    public RestHighLevelClient EsClient(){
        // 创造一个http client与ElasticSearch进行交互，9200是ES的端口号
        return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }


}
