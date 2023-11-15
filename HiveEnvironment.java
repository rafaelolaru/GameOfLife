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

    public void addFood() {
        foodQueue.offer("Food");
        foodCollected.incrementAndGet();
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
