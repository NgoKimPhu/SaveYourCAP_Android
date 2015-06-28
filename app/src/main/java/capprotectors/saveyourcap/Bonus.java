package capprotectors.saveyourcap;

import android.graphics.Rect;

public class Bonus {

    private final GameScreen game;
    public Rect r = new Rect(0, 0, 0, 0);

    private int bonusX;
    private int bonusY;
    private float bonusSpeed;

    private boolean dead = false;

    private int bonusWidth = 80;
    private int bonusHeight = 80;
    private int id;

    private int value;

    public Bonus(GameScreen game, int bonusX, int bonusY, float bonusSpeed, int id, int value) {
        this.game = game;
        this.bonusX = bonusX;
        this.bonusY = bonusY;
        this.bonusSpeed = bonusSpeed;
        this.id = id;
        this.value = value;
    }

    public void update() {
        bonusX += bonusSpeed;
        r.set(bonusX-bonusWidth/2, bonusY-bonusHeight/2, bonusX+bonusWidth/2, bonusY+bonusHeight/2);
        if (r.intersect(Student.boundingBox)){
            if (id==0)
                game.lives+=value;
            die();
        } else if (r.intersect(0, 0, 0, 800)) { // TODO: replace 800 with screenHeight
            die();
        }
    }

    public void die(){
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public int getX() {
        return bonusX;
    }

    public int getY() {
        return bonusY;
    }

    public int getWidth() {
        return bonusWidth;
    }

    public int getHeight() {
        return bonusHeight;
    }
}
