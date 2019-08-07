package app.factory;

import java.util.List;
import java.util.Map;

public interface SchemaInfo {
    String getTableName();
    String [] getColumns();
    Map<String, String> columnMaps();
    String sql();
    List<String> mapping();
}
