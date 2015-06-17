package capprotectors.saveyourcap;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import capprotectors.framework.Game;
import capprotectors.framework.Graphics;
import capprotectors.framework.Input.TouchEvent;
import capprotectors.framework.Screen;

public class GameScreen extends Screen {
    enum GameState {
        Ready, Running, Paused, GameOver
    }

    GameState state = GameState.Ready;

    // Variable Setup
    // You would create game objects here.

    private static Background bg1, bg2;
    private static Student student;
    public static ArrayList<Professor> professors = new ArrayList<>();

    int lives = 3;

    private static int score = 0;

    private float spawnChance = 0.02f;
    private int scrollSpeed = -9;
    Graphics g = game.getGraphics();

    public int screenWidth = g.getWidth();
    public int screenHeight = g.getHeight();
    Paint paint, paint2;
    Random random = new Random();
    public GameScreen(Game game) {
        super(game);

        // Initialize game objects here
        bg1 = new Background(0, 0);
        bg2 = new Background(2160, 0);
        bg1.setSpeedX(scrollSpeed);
        bg2.setSpeedX(scrollSpeed);

        student = new Student(lives, Assets.student.getWidth(), Assets.student.getHeight(), 100, screenHeight/2);

        loadRaw();

        // Defining a paint object
        paint = new Paint();
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        paint2 = new Paint();
        paint2.setTextSize(100);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.WHITE);
    }

    private void loadRaw() {
        Scanner sc = new Scanner(MainGame.grades);
        while (sc.hasNext()) {
            Professor.grades.add(sc.next());
            Professor.marks.add(sc.nextInt());
        }
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        // We have four separate update methods in this example.
        // Depending on the state of the game, we call different update methods.
        // Refer to Unit 3's code. We did a similar thing without separating the
        // update methods.

        if (state == GameState.Ready)
            updateReady(touchEvents);
        if (state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        if (state == GameState.Paused)
            updatePaused(touchEvents);
        if (state == GameState.GameOver)
            updateGameOver(touchEvents);
    }

    private void updateReady(List<TouchEvent> touchEvents) {

        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins.
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!

        if (touchEvents.size() > 0) {
            Assets.click.play(1f);
            state = GameState.Running;
        }
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {

        //This is identical to the update() method from our Unit 2/3 game.


        // 1. All touch input is handled here:
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);

            if (event.type == TouchEvent.TOUCH_UP) {

                if (event.x > screenWidth-100 && event.y < 100) {
                    pause();
                }
                if (event.y > screenHeight*3/4) {
                    student.moveTo(screenHeight * 3 / 4);
                }

                else if (event.y > screenHeight/2) {
                    student.moveTo(screenHeight / 2);
                }

                else if (event.y > screenHeight/4){
                    student.moveTo(screenHeight/4);
                }
            }


        }

        // 2. Check miscellaneous events like death:

        if (student.getLives() < 1) {
            state = GameState.GameOver;
        }


        // 3. Call individual update() methods here.
        // This is where all the game updates happen.
        student.update();

        if (Math.random()<spawnChance)
            professors.add(new Professor(Assets.professor.getWidth(), Assets.professor.getHeight(),
                    screenWidth+Assets.professor.getWidth()/2, (int) Math.floor((Math.random()*3+1))*screenHeight/4,
                    scrollSpeed, nextGrade()));

        for (int i = professors.size()-1; i>=0; i--) {
            Professor professor = professors.get(i);
            if (professor.isDead()) {
                professors.remove(i);
            }
            else {
                professor.update();
            }
        }

        bg1.update();
        bg2.update();
    }

    private int nextGrade() {
        // a method to generate next grade, dependent on different levels
        return random.nextInt(Professor.grades.size()); //temporarily a uniform distribution
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (inBounds(event, screenWidth/4, screenHeight/4, screenWidth/2, screenHeight/4)) {
                    Assets.click.play(1f);
                    resume();
                }

                if (inBounds(event, screenWidth/4, screenHeight/2, screenWidth/2, screenHeight/4)) {
                    Assets.click.play(1f);
                    nullify();
                    goToMenu();
                }
            }
        }
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (inBounds(event, 0, 0, 800, 480)) {
                    Assets.click.play(.85f);
                    nullify();
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }

    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();

        // First draw the game elements.

        // Example:
        // g.drawImage(Assets.background, 0, 0);
        // g.drawImage(Assets.character, characterX, characterY);
        g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
        g.drawImage(Assets.background, bg2.getBgX(), bg2.getBgY());

        g.drawImage(Assets.student, student.getX()-student.getWidth()/2, student.getY()-student.getHeight()/2);
        for (Professor professor : professors) {
            g.drawImage(Assets.professor, professor.getX(), professor.getY());
            g.drawString(professor.getGrade(), professor.getX(), professor.getY(), paint);
        }

        String hearts = "";
        for (int i=0; i<student.getLives(); i++) hearts+="?";
        g.drawString(hearts, 100, 100, paint);
        g.drawString(score+"", screenWidth - 200, 100, paint);

        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI();

    }

    private void nullify() {

        // Set all variables to null. You will be recreating them in the
        // constructor.
        paint = null;
        bg1 = null;
        bg2 = null;
        student = null;
        professors = null;
        // Call garbage collector to clean up memory.
        System.gc();
    }

    private void drawReadyUI() {
        Graphics g = game.getGraphics();

        g.drawARGB(155, 255, 255, 255);
        g.drawString("Tap to move to another lane.",
                640, 300, paint);

    }

    private void drawRunningUI() {
        Graphics g = game.getGraphics();

    }

    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Fill transparently the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);
        g.drawString("Resume", screenWidth/2, screenHeight*3/8, paint2);
        g.drawString("Menu",   screenWidth/2, screenHeight*5/8, paint2);
    }

    private void drawGameOverUI() {
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, 1281, 801, Color.BLACK);
        g.drawString("GAME OVER.", 400, 240, paint2);
        g.drawString("Tap to return.", 400, 290, paint);
    }

    @Override
    public void pause() {
        if (state == GameState.Running)
            state = GameState.Paused;
    }

    @Override
    public void resume() {
        if (state == GameState.Paused)
            state = GameState.Running;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        Assets.click.play(.85f);
        pause();
    }

    private void goToMenu() {
        game.setScreen(new MainMenuScreen(game));
    }

    public static Background getBg1() {
        return bg1;
    }

    public static Background getBg2() {
        return bg2;
    }

    public static Student getStudent() {
        return student;
    }

    public static void addScore(int dScore) {
        score += dScore;
    }
}