import java.util.concurrent.atomic.AtomicInteger;

abstract class Bee implements Runnable, LivingThing {
    protected AtomicInteger age = new AtomicInteger(0);
    protected final int lifespan;
    protected HiveEnvironment environment;
    protected LifeCycleListener lifecycleListener;
    protected volatile boolean isAlive;
    protected String type;
    public Bee(int lifespan, HiveEnvironment environment, LifeCycleListener listener, String type) {
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
        environment.eatFood();
    }
    public void die() {
        isAlive = false;
        lifecycleListener.onDeath(this);
    }
    public abstract void performDailyTask();
    @Override
    public void run() {
        int lastDay = environment.getCurrentDay() - 1;
        while (isAlive) {
            if (environment.getCurrentDay() > lastDay) {
                performDailyTask();
                liveDay();
                lastDay = environment.getCurrentDay();
            }

            try {
                Thread.sleep(100); // this will reduce the cpu usage
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}