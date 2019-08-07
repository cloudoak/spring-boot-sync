package app.brokers;

import app.environment.ThreadPoolConfig;
import app.message.MessageBody;
import app.util.Pagination;
import app.service.ElasticsearchService;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 *
 *  Mysql produce and Elasticsearch consume broker service.
 *
 * @author OAK
 *
 */
@Service
public class SchemaBroker implements Broker
{

    private final static Logger logger = LoggerFactory.getLogger(SchemaBroker.class);

    /**
     * Thread pool configuration.
     */
    @Autowired
    ThreadPoolConfig threadPoolConfig;

    /**
     * Produce Thread pool factory.
     */
    @Autowired
    ThreadFactory produceThreadFactory;

    /**
     * Consume Thread pool factory.
     */
    @Autowired
    ThreadFactory consumeThreadFactory;

    /**
     * Elasticsearch operation service.
     */
    @Autowired
    ElasticsearchService elasticsearchService;

    /**
     * Produce Thread pool executor service.
     */
    ExecutorService produceExecutorService;

    /**
     * Consume Thread pool executor service.
     */
    ExecutorService consumeExecutorService;

    /**
     * Time consume summary number.
     */
    AtomicLong timeConsume;

    /**
     * Garbage collect weak reference queue.
     */
    ReferenceQueue referenceQueue;

    /**
     * Mysql produce and Elasticsearch consume blocking queue.
     */
    volatile LinkedBlockingQueue<String> queues;

    /**
     * Garbage collect number.
     */
    AtomicInteger collectNum;

    /**
     * Garbage collect recycle quantity range.
     */
    volatile Integer recycleQuantity;

    /**
     * Daemon thread.
     */
    Thread daemon;

    volatile boolean FLAG = false;

    AtomicInteger finishStatistics;

    volatile ConcurrentMap stopArgumentsMap;

    /**
     * Broker field and method and thread pool and queue init.
     */
    @PostConstruct
    public void init(){
        timeConsume = new AtomicLong(0);
        collectNum = new AtomicInteger(0);
        queues = new LinkedBlockingQueue<String>(threadPoolConfig.getQueueQuantity());
        referenceQueue = new ReferenceQueue();
        recycleQuantity = threadPoolConfig.getRecycleQuantity();
        finishStatistics = new AtomicInteger(threadPoolConfig.getCorePoolSize());
        produceExecutorService = new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(),
                threadPoolConfig.getMaximumPoolSize(), threadPoolConfig.getKeepAliveTime(),
                TimeUnit.valueOf(threadPoolConfig.getUnit()), new LinkedBlockingQueue<>(),
                produceThreadFactory, new ThreadPoolExecutor.AbortPolicy());
        consumeExecutorService = new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(),
                threadPoolConfig.getMaximumPoolSize(), threadPoolConfig.getKeepAliveTime(),
                TimeUnit.valueOf(threadPoolConfig.getUnit()), new LinkedBlockingQueue<>(),
                consumeThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * Start a daemon thread.
     */
    @Override
    public void start(){
        FLAG = true;
        daemon = new Thread(() -> {
            while(FLAG){
                consume();
                if(finishStatistics.get() == 0){
                    FLAG = false;
                    try {
                        elasticsearchService.updateNumberOfReplicas(stopArgumentsMap.get("index").toString(),
                                Integer.parseInt(stopArgumentsMap.get("replicas").toString()));
                    } catch (IOException e) {
                        logger.error("Elasticsearch Service update settings number of replicas, {}", e);
                    }
                }
            }
        });
        daemon.setDaemon(true);
        daemon.start();
    }

    @Override
    public void stop(ConcurrentMap arg){
        stopArgumentsMap = arg;
    }

    @Override
    public boolean isStart(){
        return FLAG;
    }

    /**
     * Produce a message.
     * @param message a message.
     * @return Whether produce a message success.
     */
    @Override
    public boolean send(String message){
        boolean flag = queues.offer(message);
        return flag;
    }

    /**
     * Produce a message.
     * @param messageBuilder a message builder.
     * @return Whether produce a message success.
     */
    @Override
    public boolean send(StringBuilder messageBuilder){
        String message = messageBuilder.toString();
        collect(messageBuilder);
        boolean flag = queues.offer(message);
        return flag;
    }

    /**
     * Send a message.
     * @param messageBody a message body.
     */
    @Override
    public void sendMessage(MessageBody messageBody){
        Map attrs = messageBody.getAttrs();
        Pagination pagination = new Pagination();
        pagination.setWriteToBulkSize(Integer.parseInt(attrs.getOrDefault("bulkSize", 0).toString()));
        pagination.setPageSize(Integer.parseInt(attrs.getOrDefault("pageSize", 0).toString()));
        pagination.setTotal(Integer.parseInt(attrs.getOrDefault("total", 0).toString()));
        pagination.setSql(attrs.get("sql").toString());
        IntStream.range(0, threadPoolConfig.getCorePoolSize()).forEach( c -> produceExecutorService.submit(() -> {
            try {
                Pagination pagina = (Pagination) pagination.clone();
                pagina.setCurrentPageNo(c);
                messageBody.callBack(this, pagina, attrs);
            } catch (CloneNotSupportedException e) {
                logger.error("Pagination clone not supported,{}", e);
            } finally {
                finishStatistics.decrementAndGet();
            }
        }));
    }

    /**
     * Collect recyclable object.
     * @param builder recyclable object.
     */
    @Override
    public void collect(StringBuilder builder){
        WeakReference weakReference = new WeakReference(builder, referenceQueue);
        Integer collectTotalNum = collectNum.addAndGet(1);
        if(collectTotalNum > recycleQuantity){
            System.gc();
        }
    }

    /**
     * Consume from queues take a message.
     */
    public void consume(){
        String element;
        while((element = queues.poll()) != null){
            final String requestBody = element;
            consumeExecutorService.submit(() -> {
                try {
                    Instant start = Instant.now();
                    elasticsearchService.bulkAsync(requestBody);
                    long end = Instant.now().minus(start.getMillis()).getMillis(), total = timeConsume.addAndGet(end);
                    logger.debug("current Elasticsearch write cost Time: {}, summary total cost Time: {}", end, total);
                } catch (Exception e) {
                    logger.error("current Es write find a fail, {}", e);
                }
            });
        }
    }

    /**
     * Current task time consume millis.
     * @return Time consume millis.
     */
    @Override
    public Long getTaskInMillis(){
        return timeConsume.get();
    }

    /**
     * Shut down a thread pool.
     */
    @Override
    public void shutdown(){
        produceExecutorService.shutdown();
        consumeExecutorService.shutdown();
    }

}
