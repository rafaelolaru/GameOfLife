package BHive;

public class LivingThingDTO {
    private String type; // "Bee" or "Bacteria"
    private String subtype; // "Worker", "Male", "Queen" for bees; null for bacteria
    private int id;
    private Boolean matingQueueStatus; // For the 'matingqueue' event
    private Integer foodEaten; // For the 'food' event

    public LivingThingDTO(LivingThing livingThing) {
        this.type = livingThing.getClass().getSimpleName();
        this.id = livingThing.getId();
        if (livingThing instanceof Bee) {
            this.subtype = ((Bee) livingThing).getBeeType();
        } else {
            this.subtype = null;
        }
    }

    public Integer getFoodEaten() {
        return foodEaten;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

    public Boolean getMatingQueueStatus() {
        return matingQueueStatus;
    }
}
