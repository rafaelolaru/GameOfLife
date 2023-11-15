class QueenBee extends Bee {
    private int matingCooldown;

    public QueenBee(HiveEnvironment environment, BeeLifecycleListener listener) {
        super(200, environment, listener, "QueenBee"); // increased lifespan to 200
        this.matingCooldown = 0;
    }

    @Override
    public void performDailyTask() {
        if (matingCooldown <= 0) {
            MaleBee drone = environment.getDrone();
            if (drone != null && drone.isAlive() && environment.tryMating()) {
                System.out.println("Queen bee has mated with a drone.");
                matingCooldown = 2; // reset mating cooldown to 2 days
                createBabyBee(); // Use the intended method to create a baby bee
            }
        } else {
            matingCooldown--;
        }
    }

    private void createBabyBee() {
        // Randomly decide the type of the baby bee
        Bee babyBee = Math.random() < 0.10 ?
                new QueenBee(environment, lifecycleListener) :
                (Math.random() < 0.8 ? new WorkerBee(environment, lifecycleListener) : new MaleBee(environment, lifecycleListener));
        lifecycleListener.onBeeBirth(babyBee);
    }
}
