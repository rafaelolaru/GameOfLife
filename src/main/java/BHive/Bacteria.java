package BHive;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class Bacteria extends LivingThing{
    protected AtomicInteger age = new AtomicInteger(0);
    protected final int lifespan;
    protected HiveEnvironment environment;
    protected LifeCycleListener lifecycleListener;
    protected String type;
    private int consecutiveDaysEaten;
    private int consecutiveDaysStarved;

    public Bacteria(int lifespan, HiveEnvironment environment, LifeCycleListener listener, String type) {
        super(); // Call to super() is implicit, but added for clarity
        this.lifespan = lifespan;
        this.environment = environment;
        this.lifecycleListener = listener;
        this.type = type;

        this.lifecycleListener.onBirth(this);
        this.consecutiveDaysEaten = 0;
        this.consecutiveDaysStarved = 0;
    }
    public Bacteria(HiveEnvironment environment, LifeCycleListener listener) {
        this(6,environment,listener,"Bacteria");
    }
    @Override
    public void liveDay() {
        // Check if the bacteria has reached its lifespan
        if (age.incrementAndGet() > lifespan) {
            lifecycleListener.onDeath(this);
        }else{
            // Check if the bacteria eats food
            if (environment.getFoodCollected() > 0) {
                // Reset consecutive days of starvation if food is consumed
                consecutiveDaysStarved = 0;

                // Increment consecutive days of eating
                consecutiveDaysEaten++;

                // Check if the bacteria has eaten for three consecutive days
                if (consecutiveDaysEaten >= 3) {
                    // Double the bacteria
                    new Bacteria(environment, lifecycleListener);
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
    }
    public void performDailyTask() {
        ;//reproduction happens @LiveDay for now
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
                Thread.sleep(400 + random.nextInt(201)); // this will reduce the cpu usage. Sleep interval: 0.4-0.6s
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}
