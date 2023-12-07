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
        init();
    }
    private void init() {
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void publishEvent(String routingKey, LivingThing livingThing) {
        LivingThingDTO dto = new LivingThingDTO(livingThing);
        String message = gson.toJson(dto);
        System.out.println(message);
        try {
            if (channel != null) {
                channel.basicPublish(exchangeName, routingKey, null, message.getBytes("UTF-8"));
            } else {
                System.out.println("Channel is null. Cannot publish the event.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
