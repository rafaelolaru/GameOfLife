// WorkerBee class with increased lifespan
package BHive;
class WorkerBee extends Bee {
    private static final double CHANCE_TO_GET_FOOD = 0.75;
    @Override
    public String getBeeType() {
        return "Worker";
    }
    public WorkerBee(HiveEnvironment environment, LifeCycleListener listener) {
        super(30, environment, listener, "WorkerBee"); // increased lifespan to 100
    }
    @Override
    public void performDailyTask() {
        //until it matures (older than 5 days), the workerbee cannot collect food
        if (5 < this.age.get()) {
            if (environment.hasSufficientWildFood()) {
                if (Math.random() < CHANCE_TO_GET_FOOD) {
                    environment.addFood();
                    eventPublisher.publishEvent("food-harvest", this);
                }
            }
        }
    }
}
