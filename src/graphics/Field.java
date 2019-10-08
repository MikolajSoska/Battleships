package graphics;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Field extends Rectangle {
    private int posX;
    private int posY;
    private boolean locked;
    private boolean permanentLocked;
    private boolean isShip;

    public Field(int posX, int posY){
        super(50, 50, Color.WHITE);
        super.setStroke(Color.BLACK);
        super.setOnMouseClicked(event -> {
            if(!locked)
                select();
        });

        this.posX = posX;
        this.posY = posY;
        locked = true;
        permanentLocked = false;
        isShip = false;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void select(){
        super.setFill(Color.GRAY);
        super.setOnMouseClicked(event -> {
            if(!locked)
                deselect();
        });
    }

    public void deselect(){
        super.setFill(Color.WHITE);
        super.setOnMouseClicked(event -> {
            if(!locked)
                select();
        });
    }

    public void lockAsShip(){
        super.setFill(Color.BLACK);
        locked = true;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public void reset() {
        super.setFill(Color.WHITE);
        locked = true;
    }

    public void lockAsHit() {
        super.setFill(new ImagePattern(new Image("graphics/images/hit.jpg")));
        permanentLocked = true;
        isShip = true;
    }

    public void lockAsMiss() {
        super.setFill(new ImagePattern(new Image("graphics/images/miss.jpg")));
        permanentLocked = true;
    }

    public void lockAsEnemyHit() {
        super.setFill(new ImagePattern(new Image("graphics/images/enemy_hit.jpg")));
        permanentLocked = true;
        isShip = true;
    }

    public void lockAsEnemyMiss() {
        super.setFill(new ImagePattern(new Image("graphics/images/enemy_miss.jpg")));
        permanentLocked = true;
    }

    public boolean isPermanentLocked() {
        return permanentLocked;
    }

    public boolean isShip() {
        return isShip;
    }
}
