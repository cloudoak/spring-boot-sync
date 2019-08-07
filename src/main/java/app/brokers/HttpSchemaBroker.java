//package app.factory;
//
//import app.config.HttpClientConfig;
//import app.environment.ThreadPoolConfig;
//import app.service.HttpConnectionPoolService;
//import com.alibaba.fastjson.JSONObject;
//import org.joda.time.Instant;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import java.lang.ref.ReferenceQueue;
//import java.lang.ref.WeakReference;
//import java.util.Arrays;
//import java.util.concurrent.*;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//
///**
// *
// *  Mysql produce and Elasticsearch consume broker service.
// *
// * @author OAK
// *
// */
//@Service
//public class HttpSchemaBroker implements Broker
//{
//
//    private final static Logger logger = LoggerFactory.getLogger(HttpSchemaBroker.class);
//
//    /**
//     * Thread pool configuration.
//     */
//    @Autowired
//    ThreadPoolConfig threadPoolConfig;
//
//    /**
//     * Thread pool factory.
//     */
//    @Autowired
//    ThreadFactory threadFactory;
//
//    @Autowired
//    HttpClientConfig httpClientConfig;
//
//    @Autowired
//    HttpConnectionPoolService httpConnectionPoolService;
//
//    /**
//     * Thread pool executor service.
//     */
//    ExecutorService executorService;
//
//    /**
//     * Time consume summary number.
//     */
//    AtomicLong timeConsume;
//
//    /**
//     * Garbage collect weak reference queue.
//     */
//    ReferenceQueue referenceQueue;
//
//    /**
//     * Mysql produce and Elasticsearch consume blocking queue.
//     */
//    volatile LinkedBlockingQueue<String> queues;
//
//    /**
//     * Garbage collect number.
//     */
//    AtomicInteger collectNum;
//
//    /**
//     * Garbage collect recycle quantity range.
//     */
//    volatile Integer recycleQuantity;
//
//    /**
//     * Daemon thread.
//     */
//    Thread daemon;
//
//    volatile boolean FLAG = false;
//
//    /**
//     * Broker field and method and thread pool and queue init.
//     */
//    @PostConstruct
//    public void init(){
//        timeConsume = new AtomicLong(0);
//        collectNum = new AtomicInteger(0);
//        queues = new LinkedBlockingQueue<String>(threadPoolConfig.getQueueQuantity());
//        referenceQueue = new ReferenceQueue();
//        recycleQuantity = threadPoolConfig.getRecycleQuantity();
//        executorService = new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(),
//                threadPoolConfig.getMaximumPoolSize(), threadPoolConfig.getKeepAliveTime(),
//                TimeUnit.valueOf(threadPoolConfig.getUnit()), new LinkedBlockingQueue<>(),
//                threadFactory, new ThreadPoolExecutor.AbortPolicy());
//        String uris = Arrays.stream(httpClientConfig.getUris()).findFirst().get();
//        Integer port = httpClientConfig.getPort();
//        httpConnectionPoolService.createHttpClient( uris, port);
//    }
//
//    /**
//     * Start a daemon thread.
//     */
//    @Override
//    public void start(){
//        FLAG = true;
//        daemon = new Thread(() -> {
//            while(FLAG){
//                consume();
//            }
//        });
//        daemon.setDaemon(true);
//        daemon.start();
//    }
//
//    @Override
//    public boolean isStart(){
//        return FLAG;
//    }
//
//    /**
//     * Produce a message.
//     * @param message a message.
//     * @return Whether produce a message success.
//     */
//    @Override
//    public boolean produce(String message){
//        boolean flag = queues.offer(message);
//        return flag;
//    }
//
//    @Override
//    public boolean produce(StringBuilder messageBuilder) {
//        return false;
//    }
//
//    /**
//     * Collect recyclable object.
//     * @param builder recyclable object.
//     */
//    @Override
//    public void collect(StringBuilder builder){
//        WeakReference weakReference = new WeakReference(builder, referenceQueue);
//        Integer collectTotalNum = collectNum.addAndGet(1);
//        if(collectTotalNum > recycleQuantity){
//            System.gc();
//        }
//    }
//
//    /**
//     * Consume from queues take a message.
//     */
//    public void consume(){
//        String element;
//        while((element = queues.poll()) != null){
//            final String requestBody = element;
//            executorService.submit(() -> {
//                try {
//                    Instant start = Instant.now();
//                    JSONObject jsonObject = httpConnectionPoolService.post(httpClientConfig.getRestUris(), requestBody);
//                    long end = Instant.now().minus(start.getMillis()).getMillis(), total = timeConsume.addAndGet(end);
//                    logger.info("thread Name: {} result total size: {} cost Time: {}", Thread.currentThread().getName(),
//                            jsonObject.getJSONObject("hits").getInteger("total"), end);
//                    logger.debug("current Elasticsearch write cost Time: {}, summary total cost Time: {}", end, total);
//                } catch (Exception e) {
//                    logger.error("current Es write find a fail, {}", e);
//                }
//            });
//        }
//    }
//
//    /**
//     * Current task time consume millis.
//     * @return Time consume millis.
//     */
//    @Override
//    public Long getTaskInMillis(){
//        return timeConsume.get();
//    }
//
//    /**
//     * Shut down a thread pool.
//     */
//    @Override
//    public void shutdown(){
//        executorService.shutdown();
//    }
//
//}
