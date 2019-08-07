package app.service.impl;

import app.enums.RequestMethod;
import app.factory.SchemaInfo;
import app.service.ElasticsearchService;
import app.util.StreamUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * Elasticsearch CRUD Service implements class.
 *
 * @author  OAK
 *
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private final static Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

    @Autowired
    RestClient restClient;

    @Override
    public void addMapping(String indexName, SchemaInfo schemaInfo) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n      \"properties\": { ");
        String [] columns = schemaInfo.getColumns();
        int i = 0, len = columns.length;
        for(; i < len; i++){
            String keyMap = schemaInfo.columnMaps().get(columns[i]);
            if (!StringUtils.isEmpty(keyMap)) {
                columns[i] = keyMap;
            }
            builder.append(String.format("\"%s\": { \"type\": \"keyword\" %s}, ", columns[i], (!schemaInfo.mapping().contains(columns[i]) ? (", \"index\": false") : "")));
        }
        builder.append("\"TYPE\": {\n\"type\": \"keyword\"\n}}\n}");
        logger.debug("Rest client call add mappings : {}", builder.toString());
        performRequest(RequestMethod.PUT, String.format("/%s/_mapping/_doc", indexName), builder.toString());
    }

    @Override
    public void addSettings(String indexName, SchemaInfo schemaInfo,
                            Integer numberOfShards, Integer numberOfReplicas, Integer refresh_interval) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("{\n\"settings\" : {\n\"number_of_shards\" : %d,\n\"number_of_replicas\" : %d,\n\"refresh_interval\" : %d\n},  \"mappings\": {\n    \"_doc\": { \n      \"properties\": { ", numberOfShards, numberOfReplicas, refresh_interval));
        String [] columns = schemaInfo.getColumns();
        int i = 0, len = columns.length;
        for(; i < len; i++){
            String keyMap = schemaInfo.columnMaps().get(columns[i]);
            if (!StringUtils.isEmpty(keyMap)) {
                columns[i] = keyMap;
            }
            builder.append(String.format("\"%s\": { \"type\": \"keyword\" %s}, ", columns[i], (!schemaInfo.mapping().contains(columns[i]) ? (", \"index\": false") : "")));
        }
        builder.append("\"TYPE\": {\n                    \"type\": \"keyword\"\n                },");
        builder.append("\"created\":  {\n          \"type\":   \"date\", \n          \"format\": \"strict_date_optional_time||epoch_millis\"\n        }\n      }\n    }\n  }\n}");
        logger.debug("Rest client call add settings: {}", builder.toString());
        performRequest(RequestMethod.PUT, String.format("/%s", indexName), builder.toString());
    }

    @Override
    public void updateSettings(String indexName, Integer numberOfShards, Integer numberOfReplicas, Integer refreshInterval) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("{\n\"number_of_shards\" : %d,\n\"number_of_replicas\" : %d,\n\"refresh_interval\" : %d\n}", numberOfShards, numberOfReplicas, refreshInterval));
        logger.debug("Rest client call update settings: {}", builder.toString());
        performRequest(RequestMethod.PUT, String.format("/%s/_settings", indexName), builder.toString());
    }

    @Override
    public void updateNumberOfReplicas(String indexName, Integer numberOfReplicas) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("{\n\"number_of_replicas\" : %d}", numberOfReplicas));
        logger.debug("Rest client call update number of replicas settings: {}", builder.toString());
        performRequest(RequestMethod.PUT, String.format("/%s/_settings", indexName), builder.toString());
    }

    @Override
    public void bulk(String body) throws IOException {
       performRequest(RequestMethod.POST, "/_bulk", body);
    }

    @Override
    public void bulkAsync(String body) {
        performRequestAsync(RequestMethod.POST, "/_bulk", body);
    }

    @Override
    public <T> T msearch(String body, Class<T> clazz) throws IOException {
        Response response = performRequest(RequestMethod.GET, "/_msearch", body);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream in = entity.getContent();
            return JSONObject.parseObject(StreamUtils.ofBytes(in), clazz);
        }
        return null;
    }

    /**
     *
     *
     *  example e.g.
     *  {
     *     "script" : "ctx._source.new_field = 'value_of_new_field'"
     *  }
     *
     *
     * @param indexName
     * @param id
     * @param body
     */
    @Override
    public void update(String indexName, String id, String body) {
        performRequestAsync(RequestMethod.POST, String.format("%s/_doc/%s/_update", indexName, id), body);
    }

    @Override
    public void delete(String indexName, String id) {
        performRequestAsync(RequestMethod.DELETE, String.format("%s/_doc/%s", indexName, id), "{ timeout: 5m }");
    }

    @Override
    public Response performRequest(RequestMethod method, String endpoint, String body) throws IOException {
        Instant instant = Instant.now();
        Request request = new Request(method.getDisplayName(), endpoint);
        request.addParameter("pretty", "true");
        request.setEntity(new NStringEntity(body, ContentType.APPLICATION_JSON));
        Response response = restClient.performRequest(request);
        logger.debug("Rest client call performRequest method costTime: {}, response status code: {}", Instant.now().minus(instant.getMillis()).getMillis(), response.getStatusLine().getStatusCode());
        return response;
    }

    @Override
    public void performRequestAsync(RequestMethod method, String endpoint, String body) {
        Instant instant = Instant.now();
        Request request = new Request(method.getDisplayName(), endpoint);
        request.addParameter("pretty", "true");
        request.setEntity(new NStringEntity(body, ContentType.APPLICATION_JSON));
        restClient.performRequestAsync(request,new ResponseListener() {
            @Override
            public void onSuccess(Response response) {
                logger.debug("Rest client call performRequestAsync method costTime: {}, response status code: {}", Instant.now().minus(instant.getMillis()).getMillis(), response.getStatusLine().getStatusCode());
            }
            @Override
            public void onFailure(Exception exception) {
                logger.debug("Rest client call performRequestAsync method costTime: {}, exception: {}", Instant.now().minus(instant.getMillis()).getMillis(), exception);
            }
        });
    }

}
