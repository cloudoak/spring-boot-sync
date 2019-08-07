package app.brokers;

import app.message.MessageBody;

import java.util.concurrent.ConcurrentMap;

/**
 *
 *
 * @author OAK
 *
 */
public interface Broker {

    public void start();

    public void stop(ConcurrentMap arg);

    public boolean isStart();

    public boolean send(String message);

    /**
     * Produce a message.
     * @param messageBuilder a message builder.
     * @return Whether produce a message success.
     */
    public boolean send(StringBuilder messageBuilder);

    public void sendMessage(MessageBody messageBody);

    public void collect(StringBuilder builder);

    public Long getTaskInMillis();

    public void shutdown();

}
