package BHive;

import com.google.gson.Gson;

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

    public void resetCounters() {
        totalLivingThings = new AtomicInteger(0);
        totalBees = new AtomicInteger(0);
        totalBacterias = new AtomicInteger(0);
        totalQueenBees = new AtomicInteger(0);
        totalMaleBees = new AtomicInteger(0);
        totalWorkerBees = new AtomicInteger(0);
        BacteriaEatenFood = new AtomicInteger(0);
        BeeEatenFood = new AtomicInteger(0);
        totalFoodInHive = new AtomicInteger(0);
        totalDronesInMatingQueue = new AtomicInteger(0);
    }
    public void printCounters() {
        System.out.println("Total Living Things: " + totalLivingThings.get());
        System.out.println("Total Bees: " + totalBees.get());
        System.out.println("Total Bacterias: " + totalBacterias.get());
        System.out.println("Total Queen Bees: " + totalQueenBees.get());
        System.out.println("Total Male Bees: " + totalMaleBees.get());
        System.out.println("Total Worker Bees: " + totalWorkerBees.get());
        System.out.println("Total Food in Hive: " + totalFoodInHive.get());
        System.out.println("BacteriaEatenFood: " + BacteriaEatenFood.get());
        System.out.println("BeeEatenFood: " + BeeEatenFood.get());
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
            case "mating":
                processMatingQueueEvent(dto);
                break;
        }
    }
    private void incrementCounters(LivingThingDTO dto) {
        totalLivingThings.incrementAndGet();
        if ("Bacteria".equals(dto.getType())) {
            totalBacterias.incrementAndGet();
        } else if ("QueenBee".equals(dto.getType())) {
            totalBees.incrementAndGet();
            incrementBeeTypeCounters(dto.getSubtype());
        } else if ("MaleBee".equals(dto.getType())) {
            totalBees.incrementAndGet();
            incrementBeeTypeCounters(dto.getSubtype());
        } else if ("WorkerBee".equals(dto.getType())) {
            totalBees.incrementAndGet();
            incrementBeeTypeCounters(dto.getSubtype());
        }
    }
    private void decrementCounters(LivingThingDTO dto) {
        totalLivingThings.decrementAndGet();
        if ("Bacteria".equals(dto.getType())) {
            totalBacterias.decrementAndGet();
        } else if ("QueenBee".equals(dto.getType())) {
            totalBees.decrementAndGet();
            decrementBeeTypeCounters(dto.getSubtype());
        } else if ("MaleBee".equals(dto.getType())) {
            totalBees.decrementAndGet();
            decrementBeeTypeCounters(dto.getSubtype());
        } else if ("WorkerBee".equals(dto.getType())) {
            totalBees.decrementAndGet();
            decrementBeeTypeCounters(dto.getSubtype());
        }
    }
    private void processFoodEvent(LivingThingDTO dto) {
        if ("Bacteria".equals(dto.getType())) {
            BacteriaEatenFood.incrementAndGet();
        } else if ("Bee".equals(dto.getType())) {
            BeeEatenFood.incrementAndGet();
            if (dto.getFoodEaten()) {
                totalFoodInHive.decrementAndGet();
            } else {
                totalFoodInHive.addAndGet(10);
            }
        }
    }
    private void processMatingQueueEvent(LivingThingDTO dto) {
        if (dto.getDone()){
            totalDronesInMatingQueue.decrementAndGet();
            System.out.println("decrementing matingqueue");
        }
        else{
            totalDronesInMatingQueue.incrementAndGet();
            System.out.println("incrementing matingqueue");
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
                if (totalMaleBees.get() > 1) {
                    totalMaleBees.decrementAndGet();
                    System.out.println("Male Bee died");
                }
                break;
            case "Queen":
                totalQueenBees.decrementAndGet();
                break;
        }
    }

}
