package app.util;

/**
 * pagination operations object.
 *
 * @author OAK
 *
 */
public class Pagination implements Cloneable {

    private String sql;

    private Integer total;

    private Integer currentPageNo = 0;

    private Integer pageSize = 0;

    private Integer writeToBulkSize;

    private Integer currentWriteToBulkSize = 0;

    public Pagination(){}

    public Pagination(String sql, Integer total, Integer pageSize,
                      Integer writeToBulkSize){
        this.sql = sql;
        this.total = total;
        this.pageSize = pageSize;
        this.writeToBulkSize = writeToBulkSize;
    }

    public Pagination(String sql, Integer total, Integer currentPageNo, Integer pageSize,
               Integer writeToBulkSize){
        this.sql = sql;
        this.total = total;
        this.currentPageNo = currentPageNo;
        this.pageSize = pageSize;
        this.writeToBulkSize = writeToBulkSize;
    }

    public Integer getSubRecords() {
        return (total / pageSize);
    }

    public Integer getStart(){
        return (currentPageNo * getSubRecords());
    }

    public Integer getLimit(){
        return ((currentPageNo.compareTo(pageSize - 1) != 0) ? ((currentPageNo + 1) * getSubRecords()) : total);
    }

    public Integer getCurrentRecords(){
        return this.currentWriteToBulkSize + getStart();
    }

    public boolean isWriteTo() {
        return (++currentWriteToBulkSize % writeToBulkSize == 0);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrentPageNo() {
        return currentPageNo;
    }

    public void setCurrentPageNo(Integer currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getWriteToBulkSize() {
        return writeToBulkSize;
    }

    public void setWriteToBulkSize(Integer writeToBulkSize) {
        this.writeToBulkSize = writeToBulkSize;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
