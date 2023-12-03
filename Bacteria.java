import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class Bacteria extends LivingThing{
    private Random random = new Random();

    protected AtomicInteger age = new AtomicInteger(0);
    protected final int lifespan;
    protected HiveEnvironment environment;
    protected LifeCycleListener lifecycleListener;
    protected String type;
    protected int id;

    private int consecutiveDaysEaten;

    private int consecutiveDaysStarved;

    public Bacteria(int lifespan, HiveEnvironment environment, LifeCycleListener listener, String type) {
        this.lifespan = lifespan;
        this.environment = environment;
        this.lifecycleListener = listener;
        this.isAlive = true;
        this.type = type;
        this.id = Bacteria.ID;
        Bacteria.ID = Bacteria.ID + 1;

        this.lifecycleListener.onBirth(this);
        this.consecutiveDaysEaten = 0;
        this.consecutiveDaysStarved = 0;
    }
    public Bacteria(HiveEnvironment environment, LifeCycleListener listener) {
        this(4,environment,listener,"Bacteria");
    }
    @Override
    public void liveDay() {
        // Check if the bacteria is alive
        if (!isAlive) {
            return;
        }

        // Check if the bacteria has reached its lifespan
        if (age.incrementAndGet() > lifespan) {
            // die();
            lifecycleListener.onDeath(this);
        }

        // Check if the bacteria eats food
        if (environment.getFoodCollected() > 0) {
            // Reset consecutive days of starvation if food is consumed
            consecutiveDaysStarved = 0;

            // Increment consecutive days of eating
            consecutiveDaysEaten++;

            // Check if the bacteria has eaten for three consecutive days
            if (consecutiveDaysEaten >= 3) {
                // Double the bacteria
                Bacteria newBacteria = new Bacteria(environment, lifecycleListener);
                lifecycleListener.onBirth(newBacteria);
                consecutiveDaysEaten = 0; // Reset consecutive days of eating
            }

            environment.eatFood(); // Consume food
        } else {
            // Reset consecutive days of eating
            consecutiveDaysEaten = 0;

            // Increment consecutive days of starvation
            consecutiveDaysStarved++;

            // Check if the bacteria has starved for two consecutive days
            if (consecutiveDaysStarved >= 2) {
                lifecycleListener.onDeath(this); // Bacteria dies due to starvation
                return;
            }
        }
        // Increment age as a day passes
        age.incrementAndGet();
    }
    public void performDailyTask() {
        int totalBacteria = environment.getTotalNumberOfBacterias();
        int foodAvailable = environment.getFoodCollected();
        double baseRate = totalBacteria * 0.01;//the bacteria population can get roughly 1% bigger each day.
        double threshold = totalBacteria * 0.1 * 2;//if there is not enough food, no bacteria will be born
        double foodPerBacteria = 5;//this may need to be changed to 1
        double randomVariabilityFactor = 0.2 + (0.6 - 0.2) * random.nextDouble();//some kind of randomness
        //this randomness multiplies the final amount of bees to a number between 0.2 and 0.6
        double dailyBirthRate = baseRate * ((foodAvailable - threshold) / foodPerBacteria) * randomVariabilityFactor;
        // ensure birth rate is not negative
        int newBacterias = Math.max(0, (int) Math.round(dailyBirthRate));

        for (int i = 0; i < newBacterias; i++) {
            Bacteria newBacteria;
            newBacteria = new Bacteria(environment, lifecycleListener);
            lifecycleListener.onBirth(newBacteria);
        }
    }
    @Override
    public void run() {
        Random random = new Random();
        while (isAlive) {
            if (((HiveSimulation)lifecycleListener).isNewDay().get()) {
                performDailyTask();
                liveDay();
            }

            try {
                Thread.sleep(5 + random.nextInt(26)); // this will reduce the cpu usage. Sleep interval: 5-30ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}
