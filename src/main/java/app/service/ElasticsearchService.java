package app.service;

import app.enums.RequestMethod;
import app.factory.SchemaInfo;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author OAK
 *
 */
public interface ElasticsearchService {

    void addMapping(String index, SchemaInfo schemaInfo) throws IOException;

    void addSettings(String index, SchemaInfo schemaInfo,
                     Integer numberOfShards, Integer numberOfReplicas, Integer refresh_interval) throws IOException;

    void updateSettings(String index, Integer numberOfShards, Integer numberOfReplicas, Integer refresh_interval) throws IOException;

    void updateNumberOfReplicas(String index, Integer numberOfReplicas) throws IOException;

    void bulk(String body) throws IOException;

    void bulkAsync(String body);

    <T> T msearch(String body, Class<T> clazz) throws IOException;

    void update(String index, String id, String body);

    void delete(String index, String id);

    Response performRequest(RequestMethod method, String endpoint, String body) throws IOException;

    void performRequestAsync(RequestMethod method, String endpoint, String body);

}
