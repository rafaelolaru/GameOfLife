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
    public Bacteria(int lifespan, HiveEnvironment environment, LifeCycleListener listener, String type) {
        this.lifespan = lifespan;
        this.environment = environment;
        this.lifecycleListener = listener;
        this.isAlive = true;
        this.type = type;
        this.id = Bacteria.ID;
        Bacteria.ID = Bacteria.ID + 1;

        this.lifecycleListener.onBirth(this);
    }
    public Bacteria(HiveEnvironment environment, LifeCycleListener listener) {
        this(4,environment,listener,"Bacteria");
    }
    public void liveDay() {
        if (age.incrementAndGet() > lifespan) {
            // die();
            lifecycleListener.onDeath(this);
        }
        environment.eatFood();
    }
    // public void die() {
    //     lifecycleListener.onDeath(this);
    // }
    public void performDailyTask() {
        int totalBacteria = environment.getTotalNumberOfBacterias();
        int foodAvailable = environment.getFoodCollected();
        double baseRate = totalBacteria * 0.01;//the bacteria population can get roughly 1% bigger each day.
        double threshold = totalBacteria * 0.1 * 2;//if there is not enough food, no bees will be born
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
