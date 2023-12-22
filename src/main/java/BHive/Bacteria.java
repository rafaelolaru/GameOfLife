package BHive;
import com.rabbitmq.client.ConnectionFactory;

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
    EventPublisher eventPublisher;

    public Bacteria(int lifespan, HiveEnvironment environment, LifeCycleListener listener, String type) {
        super(); // Call to super() is implicit, but added for clarity
        this.lifespan = lifespan;
        this.environment = environment;
        this.lifecycleListener = listener;
        this.type = type;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.eventPublisher = new EventPublisher(factory, "rafael");

        this.lifecycleListener.onBirth(this);
        this.consecutiveDaysEaten = 0;
        this.consecutiveDaysStarved = 0;
    }
    public Bacteria(HiveEnvironment environment, LifeCycleListener listener) {
        this(10 ,environment,listener,"Bacteria");
    }
    @Override
    //New liveDay and functions for checking starvation
    public void liveDay() {
        if (age.incrementAndGet() > lifespan) {
            this.isAlive = false;
            lifecycleListener.onDeath(this);
            return;
        }
        if (environment.eatFood()){
            consecutiveDaysStarved = 0 ;
            consecutiveDaysEaten ++ ;
            eventPublisher.publishEvent("food-ate", this);
        }else{
            consecutiveDaysEaten = 0;
            consecutiveDaysStarved ++ ;
        }
        performDailyTask();

    }

    private void resetStarvationAndMaybeReproduce() {
        consecutiveDaysStarved = 0;
        if (consecutiveDaysEaten >= 4) {
            Bacteria newBacteria = new Bacteria(environment, lifecycleListener);
            lifecycleListener.onBirth(newBacteria); // Delegate the reproduction to the lifecycleListener
            //onReproduction(this);
            consecutiveDaysEaten = 0;
        }
    }
    private void incrementStarvationAndCheckDeath() {
        consecutiveDaysEaten = 0;
        if (consecutiveDaysStarved >= 6) {
            lifecycleListener.onDeath(this);
        }
    }
    //End of new liveDay and functions for checking starvation
    public void performDailyTask() {
        if (consecutiveDaysEaten > 0) {
            resetStarvationAndMaybeReproduce();
//            environment.eatFood();
        } else {
            incrementStarvationAndCheckDeath();
        }

    }
    @Override
    public void run() {
        while (isAlive) {
            if (((HiveSimulation)lifecycleListener).isNewDay().get()) {
//                performDailyTask();
                liveDay();
            }

            try {
                Thread.sleep(1000); // this will reduce the cpu usage.
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}
