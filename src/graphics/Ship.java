package graphics;

import java.util.ArrayList;
import java.util.Arrays;

public class Ship {
    private ArrayList<Field> fields;
    private int hitCounter;

    Ship(Field[] fields) {
        this.fields = new ArrayList<>(Arrays.asList(fields));
        hitCounter = 0;
    }

    public boolean containsField(Field field) {
        return fields.contains(field);
    }

    public void hit() {
        hitCounter++;
    }

    public boolean isDestroyed() {
        return hitCounter == fields.size();
    }

    public Field[] getFields() {
        return fields.toArray(new Field[fields.size()]);
    }
}
