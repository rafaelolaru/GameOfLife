package BHive;
import java.util.Random;
import java.util.Queue;

class QueenBee extends Bee {
    private boolean pregnant;
    private int pregnancyDays;
    private Queue<MaleBee> matingQueue;

    private Random random = new Random();
    @Override
    public String getBeeType() {
        return "Queen";
    }
    public QueenBee(HiveEnvironment environment, LifeCycleListener listener) {
        super(200, environment, listener, "QueenBee"); // increased lifespan to 200
    }

    private void initiateMatingFlight() {
        mateWithMaleBee();
        pregnant = true ;
        pregnancyDays = 0 ;
    }

    private void mateWithMaleBee() {
        // Check if there is a male bee in the queue
        if (!matingQueue.isEmpty()) {
            MaleBee maleBee = matingQueue.poll(); // Get the next male bee from the front of the queue
            lifecycleListener.onDeath(maleBee);
        }
    }

    private void onPregnancyComplete() {
        int totalBees = environment.getTotalNumberOfBees();
        int foodAvailable = environment.getFoodCollected();
        double baseRate = totalBees * 0.1;//the hive can get roughly 10% bigger each day.
        double threshold = totalBees * 0.1 * 2;//if there is not enough food, no bees will be born
        double foodPerBee = 1;//this may need to be changed to 1
        double randomVariabilityFactor = 0.8 + (1.2 - 0.8) * random.nextDouble();//some kind of randomness
        double dailyBirthRate = baseRate * ((foodAvailable - threshold) / foodPerBee) * randomVariabilityFactor;
        int newBees = Math.max(0, (int) Math.round(dailyBirthRate));
        pregnant = false;

    
        for (int i = 0; i < newBees; i++) {
            Bee newBee;
            if (Math.random() < 0.85) {
                newBee = new WorkerBee(environment, lifecycleListener);
                lifecycleListener.onBirth(newBee);
            } else {
                MaleBee drone = new MaleBee(environment, lifecycleListener);
                matingQueue.add(drone);
                lifecycleListener.onBirth(drone);
            }
            
        }
    }

    private void handlePregnancy() {
        pregnancyDays++;

        // Check if the pregnancy duration is over
        if (pregnancyDays > 3) {
            pregnant = false;
            pregnancyDays = 0;

            // Perform actions after pregnancy, e.g., laying fertilized eggs
            onPregnancyComplete();
        }
    }

    @Override
    public void performDailyTask() {
        if (!pregnant) {
            initiateMatingFlight();
        }else{
            handlePregnancy();
        }
    }
}
