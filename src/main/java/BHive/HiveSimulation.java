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
    private final int simulationDays = 30;
    //private AtomicInteger totalBees = new AtomicInteger(0);
    private AtomicBoolean newDay = new AtomicBoolean(false);
    List<Future<?>> futureList = new CopyOnWriteArrayList<>();
    EventReader eventReader;
    EventPublisher eventPublisher;
    Random random = new Random();

    public static void main(String[] args) {
        HiveSimulation simulation = new HiveSimulation();
        startInputThread(simulation.eventReader);  // Start the input thread
        try{
            simulation.startSimulation();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            System.exit(1); //Shuts down EVERY thread if main fails. Helps PC to not go kaboom.
        }
    }
    private static void startInputThread(EventReader eventReader) {
        Thread inputThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Press 'p' to print counters or 'q' to quit:");
                String input = scanner.nextLine();

                if ("p".equals(input)) {
                    eventReader.printCounters();
                } else if ("q".equals(input)) {
                    break; // Exit the loop, but don't interrupt the thread
                }
            }
            scanner.close();
        });

        inputThread.start();
    }
    public AtomicBoolean isNewDay() {
        return newDay;
    }
    public HiveSimulation() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.eventPublisher = new EventPublisher(factory, "rafael");
        this.eventReader = new EventReader(factory, "rafael");
        this.eventReader.startListening("birth");
        this.eventReader.startListening("death");
    }

    private void startSimulation() {
        // Starting the simulation with 1 queen, 100 worker bees, 10 drones, 20 bacterias and 20 units of food in the hive.
        new QueenBee(environment, this); // create queenBee as a task of another thread and trigger onBirth for the queenBee

        for (int i = 0; i < 100; i++) {
            new WorkerBee(environment, this); // create workerBee as a task of another thread and trigger onBirth for the workerBee
        }
        for (int i = 0; i < 10; i++) {
            new MaleBee(environment, this); // create maleBee as a task of another thread and trigger onBirth for the maleBee
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
            //recommendations:
            // don't use flags like isAlive

            //

            // Simulate the end of a day
            try {
                TimeUnit.SECONDS.sleep(1); // Each 'day' is one second long
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            System.out.println("---------------------------------");
            System.out.println("End of Day " + (day + 1) + ":");
            System.out.println("Total number of bacterias: " + environment.getTotalNumberOfBacterias());
            System.out.println("Total number of bees: " + environment.getTotalNumberOfBees());
            System.out.println("Number of worker bees: " + environment.getNumberOfWorkerBees());
            System.out.println("Number of drones: " + environment.getNumberOfDrones());
            System.out.println("Food in the hive: " + environment.getFoodCollected());
            System.out.println("Wild food available: " + environment.getWildFood());
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
            environment.totalBacterias.decrementAndGet();
            environment.addWildFood(5); // Add 5 units to the wild food
        }
        else{

            if (livingThing instanceof WorkerBee) {
                environment.decrementWorkerBees();
            } else if (livingThing instanceof MaleBee) {
                environment.decrementDrones();
            }
            environment.totalBees.decrementAndGet();
            environment.addWildFood(20);  // Add 20 units to the wild food
        }
        eventPublisher.publishEvent("death", livingThing);
    }

    @Override
    public void onBirth(LivingThing livingThing) {
        Future<?> future;
        if(livingThing instanceof Bacteria){
            Bacteria bacteria = (Bacteria)livingThing;
            environment.incrementBacterias();
            try {
                future = executorService.submit(bacteria);
                futureList.add(future);

            } catch (RejectedExecutionException e) {
                System.out.println("Could not start the life of a new " + bacteria.type + " because the executor service is shutting down.");
            }
        }
        else{
            Bee bee = (Bee)livingThing;
            if (livingThing instanceof WorkerBee) {
                environment.incrementWorkerBees();
            } else if (livingThing instanceof MaleBee) {
                environment.incrementDrones();
            }
            environment.totalBees.incrementAndGet();
            try {
                future = executorService.submit(bee);
                futureList.add(future);
            } catch (RejectedExecutionException e) {
                System.out.println("Could not start the life of a new " + bee.type + " because the executor service is shutting down.");
            }
            eventPublisher.publishEvent("birth", livingThing);
        }
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
