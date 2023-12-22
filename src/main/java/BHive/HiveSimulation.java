package BHive;
import com.rabbitmq.client.ConnectionFactory;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HiveSimulation implements LifeCycleListener {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private HiveEnvironment environment = new HiveEnvironment();
    private final int simulationDays = 50;
    private AtomicBoolean newDay = new AtomicBoolean(false);
    List<Future<?>> futureList = new CopyOnWriteArrayList<>();
    EventPublisher eventPublisher;
    Random random = new Random();

    public static void main(String[] args) {
        HiveSimulation simulation = new HiveSimulation();

        try{
            simulation.startSimulation();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            System.exit(1); //Shuts down EVERY thread if main fails. Helps PC to not go kaboom.
        }
    }
    public AtomicBoolean isNewDay() {
        return newDay;
    }
    public HiveSimulation() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.eventPublisher = new EventPublisher(factory, "rafael");//TODO: REPLACE WITH YOUR EXCHANGE NAME!
    }

    private void startSimulation() {
        // Starting the simulation with 1 queen, 100 worker bees, 10 drones, 20 bacterias and 20 units of food in the hive.
        new QueenBee(environment, this); // create queenBee as a task of another thread and trigger onBirth for the queenBee
        for (int i = 0; i < 100; i++) {
            new WorkerBee(environment, this); // create workerBee as a task of another thread and trigger onBirth for the workerBee
        }
        for (int i = 0; i < 20; i++) {
            new Bacteria(environment, this); // create bacteria as a task of another thread and trigger onBirth for the bacteria
        }
        for (int i = 0; i < 20; i++) {
            environment.addFood();
        }
        // Run the simulation for the specified number of days
        for (int day = 0; day < simulationDays; day++) {
            // First we set wild food to 5000/10000 at the start of each day
            environment.setWildFood(5000 + random.nextInt(5001));

            // Then we need to let the bees know that there is a new day.
            environment.nextDay((HiveSimulation)this);

            // Check futureList for completed futures(dead livingThings) and remove them from futureList
            for (Future<?> future : futureList) {
                if (future.isDone()) {
                    futureList.remove(future);
                }
            }

            // Simulate the end of a day
            try {
                TimeUnit.SECONDS.sleep(1); // Each 'day' is one second long
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        // After all simulation days are over, shutdown the executor
        endSimulation();
    }
    private void endSimulation() {
        executorService.shutdown();
        try {
            // Wait a while for existing tasks to terminate
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            executorService.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onDeath(LivingThing livingThing) {
        livingThing.isAlive = false;
        if(livingThing instanceof Bacteria){
            environment.addWildFood(5); // Add 5 units to the wild food
        }
        else{
            environment.addWildFood(20);  // Add 20 units to the wild food
        }
        eventPublisher.publishEvent("death", livingThing);
        eventPublisher.publishEvent("food-death", livingThing);
    }

    @Override
    public void onBirth(LivingThing livingThing) {
        Future<?> future;
        if(livingThing instanceof Bacteria){
            Bacteria bacteria = (Bacteria)livingThing;
            try {
                future = executorService.submit(bacteria);
                futureList.add(future);

            } catch (RejectedExecutionException e) {
                System.out.println("Could not start the life of a new " + bacteria.type + " because the executor service is shutting down.");
            }
        }
        else{
            Bee bee = (Bee)livingThing;
            try {
                future = executorService.submit(bee);
                futureList.add(future);
            } catch (RejectedExecutionException e) {
                System.out.println("Could not start the life of a new " + bee.type + " because the executor service is shutting down.");
            }
        }
        eventPublisher.publishEvent("birth", livingThing);
    }

    public void tickNewDay(){ //signals the start of a new day for a few ms
        this.newDay.set(true);
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        this.newDay.set(false);
    }
}
