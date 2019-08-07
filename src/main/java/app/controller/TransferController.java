//package app.controller;
//
//import app.schema.*;
//import org.apache.http.HttpHost;
//import org.elasticsearch.client.RestClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.io.IOException;
//import java.sql.SQLException;
//import java.util.Map;
//
///**
// *
// * @author OAK
// *
// */
//@Controller
//public class TransferController {
//
//    @Autowired
//    private ParamFactory paramFactory;
//
//    @Autowired
//    private ESWriteHandler esWriteHandler;
//
//    @Autowired
//    @Qualifier("priceFactory")
//    private MysqlReaderFactory factory;
//
//    /**
//     * eq：http://localhost:8081/transfer?maxSize=5&pageSize=1&key=price_channel&index=cpm_1&mapping=true&esHost=10.251.65.37&esPort=9200&esProtocol=http&updateMapping=true
//     * @param maxSize 同步的最大条数，-1为同步整表
//     * @param pageSize 每次写入es的个数
//     * @param key 要同步的表的唯一标志:price_channel price_cto price_mtm cost_pcg_mtm cost_pcg_cto(error) cost_dcg_mtm cost_dcg_cto(error)
//     * @param index es对应的index
//     * @param mapping 为true表示要对该表到es的结构创建mapping
//     * @param esHost es的host
//     * @param esPort es的端口，如果是aws那么为-1
//     * @param esProtocol es的协议，如果是aws那么为http
//     * @param updateMapping 为true则表示是更新es里的mapping，否则是创建mapping
//     * @return 耗时
//     * @throws SQLException
//     * @throws IOException
//     */
//    @RequestMapping("transfer")
//    @ResponseBody
//    public String transfer(int maxSize, int pageSize, String key, String index, Boolean mapping, String esHost, int esPort, String esProtocol, Boolean updateMapping) throws SQLException, IOException {
//        SchemaInfo dbConnectionInfo = paramFactory.getDbConnectionInfo(key);
//        if (null == dbConnectionInfo) {
//            return "errorKey";
//        }
//        RestClient restClient = RestClient.builder(new HttpHost(esHost, esPort, esProtocol)).build();
//        long begin = System.currentTimeMillis();
//        int page = maxSize / pageSize;
//        if (maxSize % pageSize > 0) {
//            page = page + 1;
//        }
//        if (maxSize == -1) {
//            page = 1;
//        }
//
//        if (mapping) {
//            esWriteHandler.writeMappingToEs(restClient, index, dbConnectionInfo, updateMapping);
//        }
//
//        Map<String, Integer> result = factory.parallelReadWrite(restClient, dbConnectionInfo, index);
//        int bulkNum = result.get("bulkNum");
//        int esTime = result.get("esTime");
//        System.out.println("handle " + pageSize + "success, result size :" + bulkNum);
//        long end = System.currentTimeMillis();
//        System.out.println("total: " + bulkNum + "; costTime: " + (end - begin) + "; esTime: " + esTime);
//
//        return "total: " + bulkNum + "; costTime: " + (end - begin) + "; esTime: " + esTime;
//    }
//}
