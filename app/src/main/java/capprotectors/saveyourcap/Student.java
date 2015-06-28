package capprotectors.saveyourcap;

import android.graphics.Rect;

public class Student {

    private int lives;
    private int studentWidth;
    private int studentHeight;
    private int studentX; //center
    private int studentY;
    private int studentDestY = studentY;
    private float studentSpeed;
    private boolean newMove = false;

    public static Rect boundingBox = new Rect(0, 0, 0, 0);

    public Student(int lives, int studentWidth, int studentHeight, int studentX, int studentY) {
        this.lives = lives;
        this.studentWidth = studentWidth;
        this.studentHeight = studentHeight;
        this.studentX = studentX;
        this.studentY = studentY;
    }

    public void update() {
        if (newMove && studentY != studentDestY) {
            studentSpeed = (studentDestY - studentY) / 18; //TODO
            newMove = false;
        }

        if (Math.abs(studentDestY-studentY) < Math.abs(studentSpeed)) {
            studentSpeed = 0;
            studentY = studentDestY;
        }
        else {
            studentY += studentSpeed;
        }
        boundingBox.set(studentX-studentWidth/2, studentY-studentHeight/2, studentX+studentWidth/2, studentY+studentHeight/2);
    }

    public void moveTo(int y) {
        int dest = GameScreen.screenHeight*y/4;
        if (dest != studentDestY) {
            studentDestY = dest;
            newMove = true;
        }
    }

    public void lostALife(){
        lives -= 1;
    }

    public void gotALife(){
        lives += 1;
    }

    public int getLives() {
        return lives;
    }

    public int getX() {
        return studentX;
    }

    public int getY() {
        return studentY;
    }

    public int getWidth() {
        return studentWidth;
    }

    public int getHeight() {
        return studentHeight;
    }
}
