// WorkerBee class with increased lifespan
class WorkerBee extends Bee {
    private static final double CHANCE_TO_GET_FOOD = 0.75;

    public WorkerBee(HiveEnvironment environment, BeeLifecycleListener listener) {
        super(30, environment, listener, "WorkerBee"); // increased lifespan to 100
    }

    @Override
    public void performDailyTask() {
        if (Math.random() < CHANCE_TO_GET_FOOD) {
            environment.addFood();
            //System.out.println("Worker bee collected food.");
        }
    }
}
