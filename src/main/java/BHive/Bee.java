package BHive;
import com.rabbitmq.client.ConnectionFactory;
import java.util.concurrent.atomic.AtomicInteger;

abstract class Bee extends LivingThing{
    protected AtomicInteger age = new AtomicInteger(0);
    protected final int lifespan;
    protected HiveEnvironment environment;
    protected LifeCycleListener lifecycleListener;
    protected String type;
    protected int consecutiveDaysStarved;
    EventPublisher eventPublisher;

    public abstract String getBeeType();
    public Bee(int lifespan, HiveEnvironment environment, LifeCycleListener listener, String type) {
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
    }
    public void liveDay() {
        if (age.incrementAndGet() > lifespan) {
            lifecycleListener.onDeath(this);
        }
        if (environment.eatFood()){
            consecutiveDaysStarved = 0 ;
            eventPublisher.publishEvent("food-ate", this);
        }else{
            consecutiveDaysStarved ++ ;
        }
        if (consecutiveDaysStarved >= 5) {
            lifecycleListener.onDeath(this);
        }
    }
    public abstract void performDailyTask();
    @Override
    public void run() {
        while (isAlive) {
            if (((HiveSimulation)lifecycleListener).isNewDay().get()) {
                performDailyTask();
                liveDay();
            }
            try {
                Thread.sleep(1000); // this will reduce the cpu usage. Sleep interval: 0.4-0.6s
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

}