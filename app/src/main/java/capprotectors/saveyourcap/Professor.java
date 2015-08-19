package capprotectors.saveyourcap;

import android.graphics.Rect;

import java.util.ArrayList;

public class Professor {
    public static ArrayList<String> grades = new ArrayList<>();
    public static ArrayList<Integer> marks = new ArrayList<>();

    private int professorWidth;
    private int professorHeight;
    private int professorX; //center
    private int professorY;

    private float professorSpeed;

    private boolean dead = false;
    private int gradeId;
    private GameScreen game;

    public int type; //0=regular and 1=bad

    public Rect r = new Rect(0, 0, 0, 0);
    public Professor(GameScreen game, int professorWidth, int professorHeight, int professorX, int professorY, float professorSpeed, int type, int gradeId) {
        this.gradeId = gradeId;
        this.type = (getScore()<0 && Math.random()<.24)?1:type;

        this.game = game;
        this.professorWidth = professorWidth-((type<1)?0:3);
        this.professorHeight = professorHeight;
        this.professorX = professorX;
        this.professorY = professorY;
        this.professorSpeed = professorSpeed;
    }

    public void update(float d) {
        professorX += professorSpeed*(d/1.6);
        r.set(professorX - professorWidth / 2, professorY, professorX + professorWidth / 2, professorY + professorHeight / 2);
        if (r.intersect(Student.boundingBox)){
            eaten();
            die();
        } else if (r.intersect(-this.professorWidth, 200, -this.professorWidth, GameScreen.screenHeight)) {
            if (type == 1)
                eaten();
            die();
        }
    }

    public void eaten() {
        if (this.getGrade().equals(grades.get(grades.size()-1)))
            game.getStudent().lostALife();
        if (this.getScore()<0 && GameScreen.getSus()>0) {
            Assets.suedS.play(1);
            GameScreen.addSus(-1);
        }
        else {
            (this.getScore()>=0?Assets.eatS:Assets.badS).play(1);
            game.addScore(this.getScore());
        }
        game.addStat(gradeId);
    }

    public void die(){
        dead = true;
    }

    public int getX() {
        return professorX;
    }

    public int getY() {
        return professorY;
    }

    public int getProfessorWidth() {
        return professorWidth;
    }

    public int getProfessorHeight() {
        return professorHeight;
    }

    public boolean isDead() {
        return dead;
    }

    public String getGrade() {return grades.get(gradeId);}

    public int getScore() {
        return marks.get(gradeId);
    }

    public void setProfessorSpeed(float professorSpeed) {
        this.professorSpeed = professorSpeed;
    }
}
