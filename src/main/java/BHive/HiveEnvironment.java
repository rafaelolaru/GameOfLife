package BHive;
import java.util.concurrent.atomic.AtomicInteger;
class HiveEnvironment {
    private AtomicInteger foodCollected = new AtomicInteger(0);// amount of food in the hive
    private AtomicInteger currentDay = new AtomicInteger(0);// does this really need an explanation?
    public AtomicInteger totalBees = new AtomicInteger(0); //total number of bees
    public AtomicInteger totalBacterias = new AtomicInteger(0); //total number of bacterias
    private AtomicInteger numberOfDrones = new AtomicInteger(0);
    private AtomicInteger numberOfWorkerBees = new AtomicInteger(0);
    private AtomicInteger wildFood = new AtomicInteger(0);// amount of food available for grabs in the wild

    public AtomicInteger getWildFood() {
        return wildFood;
    }

    public AtomicInteger getNumberOfDrones() {
        return numberOfDrones;
    }

    public AtomicInteger getNumberOfWorkerBees() {
        return numberOfWorkerBees;
    }

    public void addWildFood(int amount) {
        wildFood.addAndGet(amount);
    }
    public void incrementBacterias() {
        totalBacterias.incrementAndGet();
    }
    public void decrementBacterias() {
        totalBacterias.decrementAndGet();
    }
    public void incrementDrones() {
        numberOfDrones.incrementAndGet();
    }
    public void decrementDrones() {
        numberOfDrones.decrementAndGet();
    }
    public void incrementWorkerBees() {
        numberOfWorkerBees.incrementAndGet();
    }
    public void decrementWorkerBees() {
        numberOfWorkerBees.decrementAndGet();
    }
    public boolean hasSufficientWildFood() {
        return wildFood.get() > 10;
    }
    public void setWildFood(int amount) {
        wildFood.set(amount);
    }
    public int getCurrentDay() {
        return currentDay.get();
    }
    public void nextDay(HiveSimulation listener) {
        listener.tickNewDay();
        currentDay.incrementAndGet();
    }
    public int getTotalNumberOfBees() {
        return totalBees.get();
    }
    public int getTotalNumberOfBacterias() {
        return totalBacterias.get();
    }
    public int getFoodCollected() {
        return foodCollected.get();
    }
    public void addFood() {
        //Upon successful "harvesting", the bee brings home 10 units of food
        foodCollected.addAndGet(10);
        wildFood.addAndGet(-10);
    }
    public boolean eatFood() {
        /* one bee can only eat a unit of food per day. */
        if (foodCollected.get() > 0) {
            foodCollected.decrementAndGet(); // Consume one unit of food
            return true ;
        }
        return false ;
    }
    public void resetDailyFood(){ //handles endOfDay foodCounter reset
        ;
    }
}
