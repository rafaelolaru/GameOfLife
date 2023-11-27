public interface LivingThing {
    /// This is a bigger class that includes both Bacteria and Bee
    /// At this point in time all the logic revolved around the bee class, so a lot of the things between Bacteria and
    /// Bee are repeated. In the future, this will be changed so that it's going to be cleaner and will make us seem
    /// as better programmers than we actually are.
    void liveDay();//die run isalive performdailytask
    void die();
    void performDailyTask();
}
