package com.qiuzhitech.onlineshopping.service;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EsService {

    @Resource
    RestHighLevelClient client;

    // search
    public List<OnlineShoppingCommodity> searchCommodityByKeyword(String keyword, int from, int size) throws IOException {
        SearchRequest searchRequest = new SearchRequest("commodity");  // 设置查找索引
        // 构建search builder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "commodityName", "commodityDesc");  // 匹配多个field
        searchSourceBuilder.query(multiMatchQueryBuilder); // 添加查找
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        searchSourceBuilder.sort("price", SortOrder.ASC);

        searchRequest.source(searchSourceBuilder);  // 将search builder放入request
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT); // elastic search进行查找，并返回结果为string类型
        // 把response分解并转化成一条record
        SearchHits hits = response.getHits();
        long totalNum = hits.getTotalHits().value;
        SearchHit[] hitResult = hits.getHits();  // 需要拿到第二层hits，对应的是具体数据
        // 构建record
        List<OnlineShoppingCommodity> res = new ArrayList<>();
        for(SearchHit hit: hitResult){
            OnlineShoppingCommodity record = JSON.parseObject(hit.getSourceAsString(), OnlineShoppingCommodity.class); // 转换为商品类
            res.add(record);
        }
        log.info("Number of records found: {}", totalNum);
        return res;
    }

    // insert
    public int insertCommodity(OnlineShoppingCommodity record) throws IOException {
        // 如果存在index索引则继续，如果不存在创建索引
        GetIndexRequest getIndexRequest = new GetIndexRequest("commodity");
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!exists){
            // create index with mapping
            XContentBuilder builder = XContentFactory.jsonBuilder();
            // 动态template -- 如果传入是字符串类型，则转换问text类型并添加ik_smart分词器
            builder.startObject()
                    .startObject("dynamic_templates")
                    .startObject("strings")
                    .field("match_mapping_type", "string")
                    .startObject("mapping")
                    .field("type", "text")
                    .field("analyzer", "ik_smart")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
            CreateIndexRequest request = new CreateIndexRequest("commodity");
            request.source(builder);
            CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
            if (!response.isAcknowledged()){
                throw new RuntimeException("Elastic Search Commodity Index Creation Failed");
            }
        }
        // Convert Document into Commodity Index
        String data = JSON.toJSONString(record);
        // 找到对应的index，把数据放进索引
        IndexRequest request = new IndexRequest("commodity").source(data, XContentType.JSON);  // 生成request操作，Sets the document source to index.
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);  //执行操作，Index a document using the Index API.
        log.info("Add Commodity to Elastic Search, commodity:{}, result:{}", data, indexResponse);
        return indexResponse.status().getStatus();  // 返回status code，例如200，500等
    }
}
