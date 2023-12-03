import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

public class HiveSimulation implements LifeCycleListener {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private HiveEnvironment environment = new HiveEnvironment();
    private final int simulationDays = 1000;
    //private AtomicInteger totalBees = new AtomicInteger(0);

    public static void main(String[] args) {
        HiveSimulation simulation = new HiveSimulation();
        simulation.startSimulation();
    }

    private void startSimulation() {
        // Starting the simulation with 1 queen, 100 worker bees, 10 drones, 4 bacterias and 20 units of food in the hive.
        Bee queenBee = new QueenBee(environment, this);
        this.onBirth(queenBee); // Trigger onBirth for the queen bee
        for (int i = 0; i < 100; i++) {
            Bee workerBee = new WorkerBee(environment, this);
            this.onBirth(workerBee); // Trigger onBirth for each worker bee
        }
        for (int i = 0; i < 10; i++) {
            Bee maleBee = new MaleBee(environment, this);
            this.onBirth(maleBee); // Trigger onBirth for each male bee
        }
        for (int i = 0; i < 4; i++) {
            Bacteria bacteria = new Bacteria(environment, this);
            this.onBirth(bacteria); // Trigger onBirth for each bacteria
        }

        for (int i = 0; i < 20; i++) {
            environment.addFood();
        }

        // Run the simulation for the specified number of days
        for (int day = 0; day < simulationDays; day++) {
            // First we set wild food to 10000 at the start of each day
            environment.setWildFood(1000000);
            // Then we need to let the bees know that there is a new day.
            environment.nextDay();

            // Run the liveDay method for each bacteria
            for (int i = 0; i < environment.getTotalNumberOfBacterias(); i++) {
                Bacteria bacteria = new Bacteria(environment, this);
                bacteria.liveDay();
            }
            // Simulate the end of a day
            try {
                TimeUnit.SECONDS.sleep(1); // Each 'day' is one second long
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
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
        Bee bee = (Bee) livingThing;
        Bacteria bacteria = (Bacteria) livingThing;
        if(bacteria.type.equals("Bacteria")){
            environment.totalBacterias.decrementAndGet();
            environment.addWildFood(5); // Add 5 units to the wild food
        }
        else{
            if (bee instanceof WorkerBee) {
                environment.decrementWorkerBees();
            } else if (bee instanceof MaleBee) {
                environment.decrementDrones();
            }
            environment.totalBees.decrementAndGet();
            environment.addWildFood(20);  // Add 20 units to the wild food
        }
    }

    @Override
    public void onBirth(LivingThing livingThing) {
        Bee bee = new WorkerBee(environment, this);
        bee.isAlive = false;
        Bacteria bacteria = new Bacteria(environment, this);
        bacteria.isAlive = false;
        try{
        bee = (Bee) livingThing;
        }catch(Exception e){
        bacteria = (Bacteria) livingThing;
        }
        if(bacteria.isAlive){
            environment.incrementBacterias();
            try {
                executorService.submit(bacteria);
            } catch (RejectedExecutionException e) {
                System.out.println("Could not start the life of a new " + bacteria.type + " because the executor service is shutting down.");
            }
        }
        else{
            if (bee instanceof WorkerBee) {
                environment.incrementWorkerBees();
            } else if (bee instanceof MaleBee) {
                environment.incrementDrones();
            }
            environment.totalBees.incrementAndGet();
            try {
                executorService.submit(bee);
            } catch (RejectedExecutionException e) {
                System.out.println("Could not start the life of a new " + bee.type + " because the executor service is shutting down.");
            }
        }
    }
}
