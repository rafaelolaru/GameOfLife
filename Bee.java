import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

abstract class Bee extends LivingThing{
    protected AtomicInteger age = new AtomicInteger(0);
    protected final int lifespan;
    protected HiveEnvironment environment;
    protected LifeCycleListener lifecycleListener;
    protected String type;
    protected int id;
    public Bee(int lifespan, HiveEnvironment environment, LifeCycleListener listener, String type) {
        this.lifespan = lifespan;
        this.environment = environment;
        this.lifecycleListener = listener;
        this.isAlive = true;
        this.type = type;
        this.id = Bee.ID;
        Bee.ID = Bee.ID + 1;

        this.lifecycleListener.onBirth(this);
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
    public abstract void performDailyTask();
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