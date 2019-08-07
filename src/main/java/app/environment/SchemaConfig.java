package app.environment;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author OAK
 *
 */
@Component
@ConfigurationProperties(prefix = "spring.schema")
public class SchemaConfig {

    private String [] tableNames;

    private String [] indexes;

    private Boolean mapping;

    private Boolean updateMapping;

    private Integer bulkSize;

    private Integer pageSize;

    public String[] getTableNames() {
        return tableNames;
    }

    public void setTableNames(String[] tableNames) {
        this.tableNames = tableNames;
    }

    public String[] getIndexes() {
        return indexes;
    }

    public void setIndexes(String[] indexes) {
        this.indexes = indexes;
    }

    public Boolean getMapping() {
        return mapping;
    }

    public void setMapping(Boolean mapping) {
        this.mapping = mapping;
    }

    public Boolean getUpdateMapping() {
        return updateMapping;
    }

    public void setUpdateMapping(Boolean updateMapping) {
        this.updateMapping = updateMapping;
    }

    public Integer getBulkSize() {
        return bulkSize;
    }

    public void setBulkSize(Integer bulkSize) {
        this.bulkSize = bulkSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
