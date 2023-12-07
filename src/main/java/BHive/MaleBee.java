package BHive;
class MaleBee extends Bee {
    public MaleBee(HiveEnvironment environment, LifeCycleListener listener) {
        super(40, environment, listener, "MaleBee");
    }
    @Override
    public String getBeeType() {
        return "Male";
    }
    @Override
    public void performDailyTask() {
        //it literally does nothing all day except eat food, useless [censored]
    }
}

