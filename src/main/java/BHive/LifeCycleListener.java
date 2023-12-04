package BHive;
public interface LifeCycleListener {
    void onDeath(LivingThing livingThing);
    void onBirth(LivingThing livingThing);
}
