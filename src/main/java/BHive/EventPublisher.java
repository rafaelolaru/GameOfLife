package BHive;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class EventPublisher {

    private final Gson gson = new Gson();
    private final ConnectionFactory connectionFactory;
    private final String exchangeName;
    private Channel channel;
    private Connection connection;

    public EventPublisher(ConnectionFactory factory, String exchangeName) {
        this.connectionFactory = factory;
        this.exchangeName = exchangeName;
    }

    public void publishEvent(String routingKey, LivingThing livingThing) {
        //String message = gson.toJson(livingThing);
        String message =
        System.out.println(message);
        System.out.println(livingThing);
        try {
            channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
