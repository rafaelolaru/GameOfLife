package BHive;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class EventReader {

    private final ConnectionFactory factory;
    private final String exchangeName;
    private final Gson gson = new Gson();

    private AtomicInteger totalLivingThings = new AtomicInteger(0);
    private AtomicInteger totalBees = new AtomicInteger(0);
    private AtomicInteger totalBacterias = new AtomicInteger(0);
    private AtomicInteger foodInHive = new AtomicInteger(0);
    private AtomicInteger foodInWild = new AtomicInteger(0);
    private AtomicInteger dronesInMatingQueue = new AtomicInteger(0);
    private AtomicInteger totalQueenBees = new AtomicInteger(0);
    private AtomicInteger totalMaleBees = new AtomicInteger(0);
    private AtomicInteger totalWorkerBees = new AtomicInteger(0);

    public EventReader(ConnectionFactory factory, String exchangeName) {
        this.factory = factory;
        this.exchangeName = exchangeName;
    }

    public void startListening(String routingKey) {
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT, true);
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, exchangeName, routingKey);
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                //String message = new String(delivery.getBody(), "UTF-8");
                //System.out.println("message" + message);
                System.out.println("i got a message");
                //JsonObject eventObject = gson.fromJson(message, JsonObject.class);
                //System.out.println(eventObject);
                //processEvent(eventObject, routingKey);
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void processEvent(JsonObject eventObject, String routingKey) {
        System.out.println("Routing Key: " + routingKey);
        System.out.println("Event Object: " + eventObject);

        String type = eventObject.get("type").getAsString();
        switch (routingKey) {
            case "birth":
                System.out.println("it fucking entered");
                handleBirthEvent(type, eventObject);
                break;
            case "death":
                handleDeathEvent(type, eventObject);
                break;
            case "foodAteFromHive":
                handleFoodAteFromHiveEvent();
                break;
            case "foodCollectedSuccessfully":
                handleFoodCollectedSuccessfullyEvent();
                break;
            case "DroneInMatingQueue":
                handleDroneInMatingQueueEvent();
                break;
            default:
                System.out.println("Unhandled routing key: " + routingKey);
                break;
        }
    }

    private void handleBirthEvent(String type, JsonObject eventObject) {
        totalLivingThings.incrementAndGet();
        System.out.println("Total living things incremented: " + totalLivingThings);

        if ("Bacteria".equals(type)) {
            totalBacterias.incrementAndGet();
            System.out.println("Total bacterias incremented: " + totalBacterias);
        } else if ("Bee".equals(type)) {
            totalBees.incrementAndGet();
            System.out.println("Total bees incremented: " + totalBees);
            updateBeeCounters(eventObject, 1);
        }
    }

    private void handleDeathEvent(String type, JsonObject eventObject) {
        totalLivingThings.decrementAndGet();
        System.out.println("Total living things decremented: " + totalLivingThings);

        if ("Bacteria".equals(type)) {
            totalBacterias.decrementAndGet();
            System.out.println("Total bacterias decremented: " + totalBacterias);
        } else if ("Bee".equals(type)) {
            totalBees.decrementAndGet();
            System.out.println("Total bees decremented: " + totalBees);
            updateBeeCounters(eventObject, -1);
        }
    }

    private void handleFoodAteFromHiveEvent() {
        foodInHive.decrementAndGet();
        System.out.println("Food in hive decremented: " + foodInHive);
    }

    private void handleFoodCollectedSuccessfullyEvent() {
        foodInWild.decrementAndGet();
        System.out.println("Food in wild decremented by 10: " + foodInWild);
        foodInHive.incrementAndGet();
        System.out.println("Food in hive incremented: " + foodInHive);
    }

    private void handleDroneInMatingQueueEvent() {
        dronesInMatingQueue.incrementAndGet();
        System.out.println("Drones in mating queue incremented: " + dronesInMatingQueue);
    }

    private void updateBeeCounters(JsonObject beeObject, int increment) {
        String beeType = beeObject.get("beeType").getAsString();
        System.out.println("Bee Type: " + beeType);

        switch (beeType) {
            case "QueenBee":
                totalQueenBees.incrementAndGet();
                System.out.println("Total queen bees " + (increment > 0 ? "incremented: " : "decremented: ") + totalQueenBees);
                break;
            case "MaleBee":
                totalMaleBees.incrementAndGet();
                System.out.println("Total male bees " + (increment > 0 ? "incremented: " : "decremented: ") + totalMaleBees);
                break;
            case "WorkerBee":
                totalWorkerBees.incrementAndGet();
                System.out.println("Total worker bees " + (increment > 0 ? "incremented: " : "decremented: ") + totalWorkerBees);
                break;
        }
    }


}
