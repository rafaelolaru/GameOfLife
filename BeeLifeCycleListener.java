// Bee lifecycle listener
interface BeeLifecycleListener {
    void onBeeDeath(Bee bee);
    void onBeeBirth(Bee bee);
}