package BHive;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class LivingThing implements Runnable {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
    protected final int id;
    protected volatile boolean isAlive;

    public LivingThing() {
        this.id = ID_GENERATOR.getAndIncrement();
        this.isAlive = true;
    }
    public int getId() {
        return id;
    }
    public abstract void liveDay();
    public abstract void performDailyTask();
}
