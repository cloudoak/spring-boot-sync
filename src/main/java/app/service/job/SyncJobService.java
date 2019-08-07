package app.service.job;

import app.enums.ElasticsearchIndexType;

/**
 *
 *
 * @author OAK
 *
 */
public interface SyncJobService {

    public void run();

    public void run(String indexName, ElasticsearchIndexType indexType, String version);

}
