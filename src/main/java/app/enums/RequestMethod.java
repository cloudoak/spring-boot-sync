package app.enums;

/**
 * Request method.
 *
 * @author OAK
 *
 */
public enum RequestMethod {

    GET("GET", 1),
    POST("POST", 2),
    PUT("PUT", 3),
    DELETE("DELETE", 4);

    private String displayName;

    private Integer code;

    RequestMethod(String displayName, Integer code){
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
