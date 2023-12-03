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
        this.id = LivingThing.ID;
        LivingThing.ID = LivingThing.ID + 1;

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
                Thread.sleep(400 + random.nextInt(201)); // this will reduce the cpu usage. Sleep interval: 0.4-0.6s
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}