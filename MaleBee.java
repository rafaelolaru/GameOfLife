class MaleBee extends Bee {
    public MaleBee(HiveEnvironment environment, BeeLifecycleListener listener) {
        super(40, environment, listener, "MaleBee");
    }

    @Override
    public void performDailyTask() {
        environment.addDrone(this);
        System.out.println("Male bee joined the mating queue.");
    }
}



