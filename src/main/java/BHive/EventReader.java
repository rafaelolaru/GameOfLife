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
    private AtomicInteger totalQueenBees = new AtomicInteger(0);
    private AtomicInteger totalMaleBees = new AtomicInteger(0);
    private AtomicInteger totalWorkerBees = new AtomicInteger(0);
    private AtomicInteger BacteriaEatenFood = new AtomicInteger(0);
    private AtomicInteger BeeEatenFood = new AtomicInteger(0);
    private AtomicInteger totalFoodInHive = new AtomicInteger(0);
    private AtomicInteger totalDronesInMatingQueue = new AtomicInteger(0);

    public void printCounters() {
        System.out.println("Total Living Things: " + totalLivingThings.get());
        System.out.println("Total Bees: " + totalBees.get());
        System.out.println("Total Bacterias: " + totalBacterias.get());
        System.out.println("Total Queen Bees: " + totalQueenBees.get());
        System.out.println("Total Male Bees: " + totalMaleBees.get());
        System.out.println("Total Worker Bees: " + totalWorkerBees.get());
        System.out.println("Bacteria Eaten Food: " + BacteriaEatenFood.get());
        System.out.println("Bee Eaten Food: " + BeeEatenFood.get());
        System.out.println("Total Food in Hive: " + totalFoodInHive.get());
        System.out.println("Total Drones in Mating Queue: " + totalDronesInMatingQueue.get());
    }
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
                String message = new String(delivery.getBody(), "UTF-8");
                LivingThingDTO dto = gson.fromJson(message, LivingThingDTO.class);
                processEvent(dto, routingKey);
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }
    private void processEvent(LivingThingDTO dto, String routingKey) {
        switch (routingKey) {
            case "birth":
                incrementCounters(dto);
                break;
            case "death":
                decrementCounters(dto);
                break;
            case "food":
                processFoodEvent(dto);
                break;
            case "matingqueue":
                processMatingQueueEvent(dto);
                break;
        }
    }
    private void incrementCounters(LivingThingDTO dto) {
        totalLivingThings.incrementAndGet();
        if ("Bacteria".equals(dto.getType())) {
            totalBacterias.incrementAndGet();
        } else if ("Bee".equals(dto.getType())) {
            totalBees.incrementAndGet();
            incrementBeeTypeCounters(dto.getSubtype());
        }
    }
    private void decrementCounters(LivingThingDTO dto) {
        totalLivingThings.decrementAndGet();
        if ("Bacteria".equals(dto.getType())) {
            totalBacterias.decrementAndGet();
        } else if ("Bee".equals(dto.getType())) {
            totalBees.decrementAndGet();
            decrementBeeTypeCounters(dto.getSubtype());
        }
    }
    private void processFoodEvent(LivingThingDTO dto) {
        if ("Bacteria".equals(dto.getType())) {
            BacteriaEatenFood.incrementAndGet();
        } else if ("Bee".equals(dto.getType())) {
            BeeEatenFood.incrementAndGet();
            if (dto.getFoodEaten() != null && dto.getFoodEaten() == 10) {
                totalFoodInHive.incrementAndGet();
            } else {
                totalFoodInHive.decrementAndGet();
            }
        }
    }
    private void processMatingQueueEvent(LivingThingDTO dto) {
        if (dto.getMatingQueueStatus() != null) {
            if (dto.getMatingQueueStatus()) {
                totalDronesInMatingQueue.incrementAndGet();
            } else {
                totalDronesInMatingQueue.decrementAndGet();
            }
        }
    }
    private void incrementBeeTypeCounters(String subtype) {
        switch (subtype) {
            case "Worker":
                totalWorkerBees.incrementAndGet();
                break;
            case "Male":
                totalMaleBees.incrementAndGet();
                break;
            case "Queen":
                totalQueenBees.incrementAndGet();
                break;
        }
    }
    private void decrementBeeTypeCounters(String subtype) {
        switch (subtype) {
            case "Worker":
                totalWorkerBees.decrementAndGet();
                break;
            case "Male":
                totalMaleBees.decrementAndGet();
                break;
            case "Queen":
                totalQueenBees.decrementAndGet();
                break;
        }
    }

}
