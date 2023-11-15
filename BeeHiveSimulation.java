import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class BeeHiveSimulation implements BeeLifecycleListener {
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private HiveEnvironment environment = new HiveEnvironment();
    private final int simulationDays = 1000; // Total simulation days
    private AtomicInteger noOfBees = new AtomicInteger(0);

    public static void main(String[] args) {
        BeeHiveSimulation simulation = new BeeHiveSimulation();
        simulation.startSimulation();
    }

    private void startSimulation() {
        // Starting the simulation with initial bees
        executorService.execute(new QueenBee(environment, this));
        for (int i = 0; i < 10; i++) {
            executorService.execute(new WorkerBee(environment, this));
        }
        for (int i = 0; i < 2; i++) {
            executorService.execute(new MaleBee(environment, this));
        }

        // Run the simulation for the specified number of days
        for (int day = 0; day < simulationDays; day++) {

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
    public void onBeeDeath(Bee bee) {
        noOfBees.decrementAndGet();
        System.out.println("Number of bees :" + noOfBees);
        System.out.println(bee.type + " died.");
    }

    @Override
    public void onBeeBirth(Bee bee) {
        System.out.println("A new " + bee.type + " was born.");
        noOfBees.incrementAndGet();
        System.out.println("Number of bees :" + noOfBees);
        // Submit new bee task to the executor service to start the bee's life
        try {
            executorService.submit(bee);
        } catch (RejectedExecutionException e) {
            System.out.println("Could not start the life of a new " + bee.type + " because the executor service is shutting down.");
        }
    }
}
