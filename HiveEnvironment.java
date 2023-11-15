import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
class HiveEnvironment {
    private BlockingQueue<String> foodQueue = new LinkedBlockingQueue<>();
    private ConcurrentLinkedQueue<MaleBee> matingQueue = new ConcurrentLinkedQueue<>();
    private AtomicInteger foodCollected = new AtomicInteger(0);
    private Random random = new Random();

    private AtomicInteger foodAvailable = new AtomicInteger(0);

    private final int dailyFoodLimit = 500; // For example, 20 units of food per day
    private AtomicInteger foodAddedToday = new AtomicInteger(0);

    public boolean tryAddFood() {
        if (foodAddedToday.get() < dailyFoodLimit) {
            addFood();
            foodAddedToday.incrementAndGet();
            return true;
        }
        return false;
    }

    public void resetDailyFood() {
        foodAddedToday.set(0);
    }
    public void addFood() {
        foodQueue.offer("Food");
        foodCollected.incrementAndGet();
    }

    public boolean consumeFood() {
        int currentFood;
        do {
            currentFood = foodAvailable.get();
            if (currentFood == 0) {
                return false; // No food available to consume
            }
        } while (!foodAvailable.compareAndSet(currentFood, currentFood - 1));
        return true;
    }

    public int getFoodCollected() {
        return foodCollected.get();
    }

    public String getFood() {
        return foodQueue.poll();
    }

    public void addDrone(MaleBee drone) {
        matingQueue.offer(drone);
    }

    public MaleBee getDrone() {
        return matingQueue.poll();
    }

    public boolean tryMating() {
        return random.nextDouble() < 0.95; // 5% chance of successful mating
    }
}
