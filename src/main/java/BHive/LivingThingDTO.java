package BHive;

public class LivingThingDTO {
    private String type; // "Bee" or "Bacteria"
    private String subtype; // "Worker", "Male", "Queen" for bees; null for bacteria
    private int id;
    private boolean foodEaten; // For the 'food' event
    private boolean isDone;

    public LivingThingDTO(LivingThing livingThing) {
        this.type = livingThing.getClass().getSimpleName();
        this.id = livingThing.getId();
        if (livingThing instanceof Bee) {
            this.subtype = ((Bee) livingThing).getBeeType();
        } else {
            this.subtype = null;
        }
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean getDone() {
        return isDone;
    }

    public void setFoodEaten(boolean foodEaten) {
        this.foodEaten = foodEaten;
    }

    public boolean getFoodEaten() {
        return foodEaten;
    }

    public String getType() {
        return type;
    }

    public String getSubtype() {
        return subtype;
    }

}
