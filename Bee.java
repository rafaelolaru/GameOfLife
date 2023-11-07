import java.util.concurrent.atomic.AtomicInteger;

abstract class Bee implements Runnable {
    protected AtomicInteger age = new AtomicInteger(0);
    protected final int lifespan;
    protected HiveEnvironment environment;
    protected BeeLifecycleListener lifecycleListener;
    protected volatile boolean isAlive;
    protected String type;

    public Bee(int lifespan, HiveEnvironment environment, BeeLifecycleListener listener, String type) {
        this.lifespan = lifespan;
        this.environment = environment;
        this.lifecycleListener = listener;
        this.isAlive = true;
        this.type = type;
    }

    public void liveDay() {
        if (age.incrementAndGet() > lifespan) {
            die();
        }
    }

    public void die() {
        isAlive = false;
        lifecycleListener.onBeeDeath(this);
    }

    public boolean isAlive() {
        return isAlive;
    }

    public abstract void performDailyTask();

    @Override
    public void run() {
        while (isAlive) {
            performDailyTask();
            liveDay();

            try {
                Thread.sleep(1000); // Simulate a day with 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}