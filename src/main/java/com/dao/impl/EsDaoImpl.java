package com.dao.impl;

import com.alibaba.fastjson.JSON;
import com.config.EsClient;
import com.dao.EsDao;
import com.entity.EsField;
import org.apache.lucene.index.Fields;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by xiaozhi on 2019/12/31.
 */
@Component
public class EsDaoImpl implements EsDao {

    @Autowired
    private EsClient esClient;

    private TransportClient getClient() {
        return esClient.getClient();
    }


    public String queryByName(String index, String type, String queryByName) {

        TransportClient client = this.getClient();
        //搜索
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name", queryByName);
        searchSourceBuilder.query(matchQueryBuilder);
        searchRequest.source(searchSourceBuilder);

        System.out.println("请求参数:"+JSON.toJSONString(searchRequest));

        ActionFuture<SearchResponse> actionFutureResponse = client.search(searchRequest);
        String responseStr = null;
        try {
            SearchResponse response = actionFutureResponse.get();
            List<Map<String, Object>> resultList = new ArrayList<>();

            SearchHits searchHits = response.getHits();
            for (SearchHit hit : searchHits) {
                Map<String, Object> row = hit.getSource();
                row.put(EsField.SCHEMA, hit.getType());
                row.put(EsField.ID, hit.getId());
                row.put(EsField.SCORE, hit.getScore());
                resultList.add(row);
            }
            responseStr = JSON.toJSONString(resultList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("响应参数:"+responseStr);
        return responseStr;

    }
}
