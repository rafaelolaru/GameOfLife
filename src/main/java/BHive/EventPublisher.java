package BHive;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class EventPublisher {

    private final Gson gson = new Gson();
    //private final ConnectionFactory connectionFactory;
    private final String exchangeName;
    private Channel channel;
    private Connection connection;

    public EventPublisher(Channel channel, String exchangeName) {
        this.channel = channel;
        this.exchangeName = exchangeName;
    }

    public <T> void publishEvent(String routingKey, T eventData) {
        Event<T> event = new Event<>(eventData);
        String message = gson.toJson(event);

        try {
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class Event<T> {
        private final T data;

        public Event(T data) {
            this.data = data;
        }
    }
}
