package app.enums;

/**
 *
 *
 *
 * @author OAK
 *
 */
public enum Operations {

    BULK("BULK", 1),
    UPDATE("UPDATE", 2),
    DELETE("DELETE", 3);

    private String displayName;

    private Integer code;

    Operations(String displayName, Integer code){
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
