import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

class QueenBee extends Bee {
    private Random random = new Random();

    private boolean pregnant;
    private int pregnancyDays;
    private Queue<MaleBee> matingQueue;

    

    public QueenBee(HiveEnvironment environment, LifeCycleListener listener) {
        super(200, environment, listener, "QueenBee"); // increased lifespan to 200
        this.pregnant = false;
        this.pregnancyDays = 0;
        this.matingQueue = new LinkedList<>();
        for(int i = 0 ; i <3 ; i++){
            MaleBee drone = new MaleBee(environment, lifecycleListener);
            matingQueue.add(drone);
            lifecycleListener.onBirth(drone);
        }
        
    }

    private void initiateMatingFlight() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++") ;
        // Mate with a random male bee
        mateWithMaleBee();

        // Start the pregnancy
        pregnant = true;
    }

    private void mateWithMaleBee() {
        // Check if there is a male bee in the queue
        if (!matingQueue.isEmpty()) {
            MaleBee maleBee = matingQueue.poll(); // Get the next male bee from the front of the queue

            // Queen mates with the selected male bee
            System.out.println("Queen Bee mated");

            // Perform any other actions after mating
            // ...

            // Optionally, you might want to remove the male bee from the simulation
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
        // int totalBees = environment.getTotalNumberOfBees();
        // int foodAvailable = environment.getFoodCollected();
        // double baseRate = totalBees * 0.1;//the hive can get roughly 10% bigger each day.
        // double threshold = totalBees * 0.1 * 2;//if there is not enough food, no bees will be born
        // double foodPerBee = 1;//this may need to be changed to 1
        // double randomVariabilityFactor = 0.8 + (1.2 - 0.8) * random.nextDouble();//some kind of randomness
        // //this randomness multiplies the final amount of bees to a number between 0.8 and 1.2
        // double dailyBirthRate = baseRate * ((foodAvailable - threshold) / foodPerBee) * randomVariabilityFactor;
        // // ensure birth rate is not negative
        // int newBees = Math.max(0, (int) Math.round(dailyBirthRate));

        // for (int i = 0; i < newBees; i++) {
        //     Bee newBee;
        //     if (Math.random() < 0.85) {
        //         newBee = new WorkerBee(environment, lifecycleListener);
        //         lifecycleListener.onBirth(newBee);
        //     } else {
        //         MaleBee drone = new MaleBee(environment, lifecycleListener);
        //         matingQueue.add(drone);
        //         lifecycleListener.onBirth(drone);
        //     }
        // }
        if (5 < this.age.get()) {
            System.out.println("===============================");
        }
        
        // if (!pregnant) {
        //     System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        //     initiateMatingFlight();
        // }

        initiateMatingFlight();
        // Check if the queen is pregnant
        // if (pregnant) {
        //     handlePregnancy();
        // }

        // else{
        //     System.out.println("_________________");
        //     onPregnancyComplete() ;}
        onPregnancyComplete() ;

        

    }
}
