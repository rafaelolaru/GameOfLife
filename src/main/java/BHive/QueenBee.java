package BHive;
import java.util.Random;

class QueenBee extends Bee {
    private Random random = new Random();
    @Override
    public String getBeeType() {
        return "Queen";
    }
    public QueenBee(HiveEnvironment environment, LifeCycleListener listener) {
        super(200, environment, listener, "QueenBee"); // increased lifespan to 200
    }

    @Override
    public void performDailyTask() {
        int totalBees = environment.getTotalNumberOfBees();
        int foodAvailable = environment.getFoodCollected();
        double baseRate = totalBees * 0.1;//the hive can get roughly 10% bigger each day.
        double threshold = totalBees * 0.1 * 2;//if there is not enough food, no bees will be born
        double foodPerBee = 1;//this may need to be changed to 1
        double randomVariabilityFactor = 0.8 + (1.2 - 0.8) * random.nextDouble();//some kind of randomness
        //this randomness multiplies the final amount of bees to a number between 0.8 and 1.2
        double dailyBirthRate = baseRate * ((foodAvailable - threshold) / foodPerBee) * randomVariabilityFactor;
        // ensure birth rate is not negative
        int newBees = Math.max(0, (int) Math.round(dailyBirthRate));

        for (int i = 0; i < 1; i++) {
            Bee newBee;
            if (Math.random() < 0.85) {
                newBee = new WorkerBee(environment, lifecycleListener);
            } else {
                newBee = new MaleBee(environment, lifecycleListener);
            }
            lifecycleListener.onBirth(newBee);
        }
    }
}
