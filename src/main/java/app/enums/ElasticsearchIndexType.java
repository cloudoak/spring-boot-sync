package app.enums;

/**
 *
 * Elasticsearch index type.
 *
 * @author OAK
 *
 */
public enum ElasticsearchIndexType {

    INDEX("index", 1),
    DELETE("delete", 2),
    CREATE("create", 3),
    UPDATE("update", 4);

    private String displayName;

    private Integer code;

    ElasticsearchIndexType(String displayName, Integer code){
        this.code = code;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
