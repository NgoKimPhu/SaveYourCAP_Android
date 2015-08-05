package capprotectors.saveyourcap;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmAchievement;
import com.swarmconnect.SwarmActiveUser.GotCloudDataCB;
import com.swarmconnect.SwarmLeaderboard;

import java.util.ArrayList;
import java.util.List;
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

    private static int runs = 0;
    private static int coins;
    private static int sus = 0;
    private static int brains = 0;

    public static void addSus(int d) {
        GameScreen.sus += d;
    }

    public static void addBrains(int d) {
        GameScreen.brains += d;
    }

    private Background bg1, bg2;
    private Animation stuAnim, heartAnim;
    private Student student;

    private Bonus heart, su, brain;

    public ArrayList<Professor> professors = new ArrayList<>();
    private float[] laneCooldown = new float[3]; // = [0, 0, 0]
    int lives = 3;

    // Variable Setup
    private int[] stat;
    private int coin;
    private float currTime;
    private float brainTime = 0;

    private int score = 0;

    // You would create game objects here.

    private float spawnChance = 0.02f;
    private float spawnChanceIncRate = 0.0021f;
    private float[] biggerGradeChance;
    private float gradeChanceChange = 0.975f;
    private int difficulty = 0;
    private int scrollSpeed = -9;
    Graphics g = game.getGraphics();

    public static int screenWidth;
    public static int screenHeight;
    Paint smallBlackPaint, medBlackPaint, bigBlackPaint, smallRedPaint, smallBluePaint, medRedPaint, medRedPaint2, medRedPaint3, bigRedPaint, medWhiteBPaint, medWhiteBPaint2, medWhiteBPaint3, bigWhiteBPaint;

    public GameScreen(Game game) {

        super(game);

        if (Swarm.isLoggedIn()) {
            Swarm.user.getCloudData("runs", new GotCloudDataCB() {
                public void gotData(String data) {
                    // Did our request fail (network offline, and not cached)? Or has this key never been set?
                    if (data == null || data.length() == 0)
                        data = "0";
                    // Parse the level data for later use
                    GameScreen.runs = Integer.parseInt(data);
                }
            });
            Swarm.user.getCloudData("coin", new GotCloudDataCB() {
                public void gotData(String data) {
                    // Did our request fail (network offline, and not cached)? Or has this key never been set?
                    if (data == null || data.length() == 0)
                        data = "0";
                    // Parse the level data for later use
                    GameScreen.coins = Integer.parseInt(data);
                }
            });
            Swarm.user.getCloudData("su", new GotCloudDataCB() {
                public void gotData(String data) {
                    if (data == null || data.length() == 0)
                        data = "0";
                    GameScreen.sus = Integer.parseInt(data);
                }
            });
            Swarm.user.getCloudData("brain", new GotCloudDataCB() {
                public void gotData(String data) {
                    if (data == null || data.length() == 0)
                        data = "0";
                    GameScreen.brains = Integer.parseInt(data);
                }
            });
        }

        screenWidth = g.getWidth();
        screenHeight = g.getHeight();

        // Initialize game objects here
        bg1 = new Background(0, 0);
        bg2 = new Background(Assets.background.getWidth(), 0);
        bg1.setSpeedX(scrollSpeed);
        bg2.setSpeedX(scrollSpeed);

        student = new Student(lives, Assets.student[0].getWidth(), Assets.student[0].getHeight(), 100, screenHeight/2);
        stuAnim = new Animation();
        for (int i=0; i<=8; i++)
            stuAnim.addFrame(Assets.student[i], 50);
        for (int i=6; i>=0; i--)
            stuAnim.addFrame(Assets.student[i], 50);

        heartAnim = new Animation();
        for (int i=0; i<7; i++)
            heartAnim.addFrame(Assets.heart[i], 40);

        if (Professor.grades.size()<1) {
            loadRaw();
        }
        stat = new int[Professor.grades.size()];

        if (biggerGradeChance == null) {
            biggerGradeChance = new float[Professor.grades.size()];
            for (int i = 0; i < biggerGradeChance.length - 1; i++)
                biggerGradeChance[i] = (float) Math.sqrt(1 - 1f / (biggerGradeChance.length - i)); //higher chance for bigger grades at first;
            biggerGradeChance[biggerGradeChance.length - 1] = 0;
        }
        /*for (int i = 0; i<biggerGradeChance.length; i++)
            Log.i("Grades up chance from ", i+": "+biggerGradeChance[i]);
        */
        // Defining a paint object
        smallBlackPaint = new Paint();
        smallBlackPaint.setAntiAlias(true);
        medRedPaint = new Paint(smallBlackPaint);

        smallBlackPaint.setTextAlign(Paint.Align.CENTER);
        smallRedPaint = new Paint(smallBlackPaint);

        smallBlackPaint.setColor(Color.BLACK);
        medBlackPaint = new Paint(smallBlackPaint);
        bigBlackPaint = new Paint(smallBlackPaint);

        smallBlackPaint.setTextSize(30);
        medBlackPaint.setTextSize(55);

        smallRedPaint.setTextSize(69);
        smallBluePaint = new Paint(smallRedPaint);
        smallRedPaint.setColor(Color.RED);
        smallBluePaint.setColor(Color.BLUE);

        medRedPaint.setTextSize(80);
        medRedPaint.setColor(Color.RED);
        medRedPaint2 = new Paint(medRedPaint);
        medRedPaint3 = new Paint(medRedPaint);
        medRedPaint.setTextAlign(Paint.Align.LEFT);
        medRedPaint.setTextScaleX(.9f);

        medRedPaint2.setTextAlign(Paint.Align.CENTER);
        medRedPaint3.setTextAlign(Paint.Align.RIGHT);

        bigBlackPaint.setTextSize(100);
        bigRedPaint = new Paint(bigBlackPaint);
        bigWhiteBPaint = new Paint(bigBlackPaint);
        bigBlackPaint.setTextAlign(Paint.Align.RIGHT);

        bigRedPaint.setColor(Color.RED);

        bigWhiteBPaint.setColor(Color.WHITE);
        bigWhiteBPaint.setStyle(Paint.Style.STROKE);
        bigWhiteBPaint.setStrokeWidth(9f);

        medWhiteBPaint2 = new Paint(bigWhiteBPaint);
        medWhiteBPaint2.setTextSize(80);
        medWhiteBPaint = new Paint(medWhiteBPaint2);
        medWhiteBPaint.setTextAlign(Paint.Align.LEFT);
        medWhiteBPaint3 = new Paint(medWhiteBPaint);
        medWhiteBPaint.setStrokeWidth(4f);
        medWhiteBPaint3.setTextAlign(Paint.Align.RIGHT);
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

        if (deltaTime > 3.15){ // safe cap to prevent major glitch during frame drops
            deltaTime = (float) 3.15;
        }

        if (state == GameState.Ready)
            updateReady(touchEvents);
        else if (state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        else if (state == GameState.Paused)
            updatePaused(touchEvents);
        else if (state == GameState.GameOver)
            updateGameOver(touchEvents);
    }

    private void updateReady(List<TouchEvent> touchEvents) {

        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins.
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!

        if (touchEvents.size() > 0) {
            Assets.clickS.play(1f);
            state = GameState.Running;
        }
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {

        //This is identical to the update() method from our Unit 2/3 game.


        // 1. All touch input is handled here:
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);

            if (event.type == TouchEvent.SWIPE_UP) {
                if (student.getY() > screenHeight/2 + screenHeight/7)
                    student.moveTo(2);
                else if (student.getY() > screenHeight/4)
                    student.moveTo(1);
                break;
            }
            else if (event.type == TouchEvent.SWIPE_DOWN) {
                if (student.getY() < screenHeight/2 - screenHeight/7)
                    student.moveTo(2);
                else if (student.getY() < screenHeight*3/4)
                    student.moveTo(3);
                break;
            }
            else if (event.type == TouchEvent.TOUCH_UP) {
                if (inBounds(event, screenWidth-118, 18, 100, 100))
                    pause();
//                if (inBounds(event, 460, 0, 245, 145))
//                    pause();
                if (inBounds(event, 705, 13, 270, 138))
                    actBrain();

                if (event.x < 20 && event.y < 20) //debug
                    student.lostALife();
                g.drawImage(Assets.su, 465, 0);
                g.drawString("x" + sus, 580, 100, medWhiteBPaint);
                g.drawString("x" + sus, 578, 100, medRedPaint);
                g.drawImage(Assets.brain, 710, 18);
                g.drawString("x" + brains, 820, 100, medWhiteBPaint);
                g.drawString("x" + brains, 818, 100, medRedPaint);
                for (Professor x:professors)
                    if (x.type == 1 && inBounds(event, x.getX() - x.getProfessorWidth() / 2 - 25, x.getY() - x.getProfessorHeight() - 25, x.getProfessorWidth() + 50, x.getProfessorHeight() + 50)) {
                        Assets.punchS.play(1);
                        x.die();
                    }
                /*if (event.y > screenHeight*3/4) //tap to move
                    student.moveTo(3);
                else if (event.y > screenHeight/2)
                    student.moveTo(2);
                else if (event.y > screenHeight/4)
                    student.moveTo(1);*/
            }
        }

        // 2. Check miscellaneous events like death:

        if (student.getLives() == 0) {
            Assets.gmovS.play(1);
            runs+=1;
            if (Swarm.isLoggedIn())
                Swarm.user.saveCloudData("runs", runs + "");
            Log.d("GameScreen", score+" "+student.getLives());
            SwarmLeaderboard.submitScore(SwarmConsts.Leaderboard.CAP_SAVIORS_ID, score);
            if (runs == 1)
                SwarmAchievement.unlock(SwarmConsts.Achievement.FIRST_RUN_ID);
            if (runs == 20)
                SwarmAchievement.unlock(SwarmConsts.Achievement.HARDWORKING_GUY_ID);
            if (score<0)
                SwarmAchievement.unlock(SwarmConsts.Achievement.SUPER_SLACK_ID);
            if (score>100)
                SwarmAchievement.unlock(SwarmConsts.Achievement.NICE_PROGRESS_ID);
            if (score>200)
                SwarmAchievement.unlock(SwarmConsts.Achievement.DEANS_LISTERS_ID);
            currTime = 0;
            student.lostALife();
            state = GameState.GameOver;
        }

        // 3. Call individual update() methods here.
        // This is where all the game updates happen.
        student.update(deltaTime);

        while (score - difficulty > 50) {
            increaseDifficulty();
        }

        if (Math.random()<spawnChance)
            spawn("Prof", Assets.prof[0].getWidth(), Assets.prof[0].getHeight(),
                    screenWidth+Assets.prof[0].getWidth()/2, (int) (Math.random() * 3),
                    scrollSpeed, 0, nextGrade(), deltaTime);

        if (Math.random()<Math.pow(spawnChance, 2.3))
            spawn("Bonus", Assets.heart[0].getWidth(), Assets.heart[0].getHeight(),
                    screenWidth+Assets.heart[0].getWidth()/2, (int) (Math.random() * 3), scrollSpeed, 0, 1, deltaTime);

        if (Math.random()<Math.pow(spawnChance, 2.1))
            spawn("Bonus", Assets.su.getWidth(), Assets.su.getHeight(),
                    screenWidth + Assets.su.getWidth() / 2, (int) (Math.random() * 3), scrollSpeed, 1, 1, deltaTime);

        if (Math.random()<Math.pow(spawnChance, 2.2))
            spawn("Bonus", Assets.brain.getWidth(), Assets.brain.getHeight(),
                    screenWidth + Assets.brain.getWidth()/2, (int) (Math.random() * 3), scrollSpeed, 2, 1, deltaTime);

        for (int i = 0; i < laneCooldown.length; i++)
            if (laneCooldown[i]>0)
                laneCooldown[i] -= deltaTime;

        for (int i = professors.size()-1; i>=0; i--) {
            Professor professor = professors.get(i);
            if (professor.isDead()) {
                professors.remove(i);
            }
            else {
                professor.update(deltaTime);
            }
        }

        if (heart !=null)
            if (heart.isDead())
                heart = null;
            else heart.update(deltaTime);
        if (su !=null)
            if (su.isDead())
                su = null;
            else su.update(deltaTime);
        if (brain !=null)
            if (brain.isDead())
                brain = null;
            else brain.update(deltaTime);

        bg1.update(deltaTime);
        bg2.update(deltaTime);
        animate(deltaTime);
    }

    private void actBrain() {
        if (brains>0){
            Assets.brainS.play(1);
            brains-=1;
            brainTime+=5;
        }
    }

    private void spawn(String type, int w, int h, int x, int y, int speed, int id, int val, float d) {
        if (laneCooldown[y] <= 0) {
            laneCooldown[y] = -2f*w/speed/d;
            y+=1;
            if (type.equals("Prof")) {
                if (brainTime>0) {
                    brainTime-=1;
                    id = 2;
                    val = Math.random()<.5?0:1;
                }
                professors.add(new Professor(this, w, h, x, y*screenHeight/4, speed, id, val));
            }
            else if (type.equals("Bonus")) {
                if (id == 0) // Heart
                    if (heart == null)
                        heart = new Bonus(this, x, y * screenHeight / 4, speed, id, val);
                if (id == 1) // SU
                    if (su == null)
                        su = new Bonus(this, x, y * screenHeight / 4, speed, id, val);
                if (id == 2) // Brain
                    if (brain == null)
                        brain = new Bonus(this, x, y * screenHeight / 4, speed, id, val);
            }
        }
    }

    private void animate(float d) {
        stuAnim.update((long) (13 * d / 1.6));
        heartAnim.update((long) (13 * d / 1.6));
    }

    private void increaseDifficulty() {
        difficulty+=50;

        spawnChance += spawnChanceIncRate;
        spawnChanceIncRate -= spawnChance/200;
        spawnChanceIncRate = spawnChanceIncRate>0?spawnChanceIncRate:0;

        gradeChanceChange *= 0.975f;

        scrollSpeed = -9-score/50;
        bg1.setSpeedX(scrollSpeed);
        bg2.setSpeedX(scrollSpeed);
    }

    private int nextGrade() {
        // a method to generate next grade
        int next = 0;
        while (Math.random() < biggerGradeChance[next]*gradeChanceChange)
            next++;
        return Professor.grades.size() - 1 - next;
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (inBounds(event, 500, 350, 100, 100)) {
                    Assets.clickS.play(1f);
                    resume();
                }
                else if (inBounds(event, 680, 350, 100, 100)) {
                    Assets.clickS.play(1f);
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
                if (inBounds(event, 500, 590, 100, 100)) {
                    Assets.clickS.play(1f);
                    nullify();
                    game.setScreen(new GameScreen(game));
                }
                else if (inBounds(event, 680, 590, 100, 100)) {
                    Assets.clickS.play(1f);
                    nullify();
                    goToMenu();
                }
                else currTime = 99999;
            }
        }

    }

    @Override
    public void paint(float deltaTime) {

        g.drawImage(Assets.background, bg1.getBgX(), bg1.getBgY());
        g.drawImage(Assets.background, bg2.getBgX(), bg2.getBgY());

        for (int i=1; i<4; i++) {
            if ((i-1)*screenHeight / 4 < student.getY() && student.getY() <= i*screenHeight / 4)
                g.drawImage(stuAnim.getImage(), student.getX() - student.getWidth() / 2, student.getY() - student.getHeight() / 2);

            for (Professor prof : professors)
                if (prof.getY() == i*screenHeight / 4) {
                    g.drawImage(Assets.prof[prof.type==1?1:0], prof.getX() - prof.getProfessorWidth() / 2, prof.getY() - prof.getProfessorHeight() / 2);
                    g.drawString(prof.getGrade(), prof.getX() - 80, prof.getY() - 5, prof.type<2?smallRedPaint: smallBluePaint);
                }
            if (heart != null && heart.getY() == i*screenHeight / 4) {
                g.drawImage(heartAnim.getImage(), heart.getX() - heart.getWidth() / 2, heart.getY() - heart.getHeight() / 2);
            }
            if (su != null && su.getY() == i*screenHeight / 4) {
                g.drawImage(Assets.su, su.getX() - su.getWidth() / 2, su.getY() - su.getHeight() / 2);
            }
            if (brain != null && brain.getY() == i*screenHeight / 4) {
                g.drawImage(Assets.brain, brain.getX() - brain.getWidth() / 2, brain.getY() - brain.getHeight() / 2);
            }
        }

        String hearts = "";
        for (int i = 0; i < student.getLives(); i++) hearts += "♥";
        g.drawString(hearts, 35, 100, medRedPaint);
        g.drawString(score + "", screenWidth - 140, 105, bigBlackPaint);

        g.drawString("FPS:"+((int) (100/deltaTime)), 60, screenHeight-25, smallBlackPaint); //TODO option to show/hide in setting

        // Secondly, draw the UI above the game elements.
        if (deltaTime > 3.15){ // safe cap to prevent major glitch during frame drops
            deltaTime = 3.15f;
        }
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI(deltaTime);
    }

    private void nullify() {
        if (Swarm.isLoggedIn()) {
            Swarm.user.saveCloudData("su", sus + "");
            Swarm.user.saveCloudData("brain", brains + "");
        }
        // Set all variables to null. You will be recreating them in the
        // constructor.
        bg1 = null;
        bg2 = null;
        stuAnim = null;
        heartAnim = null;
        student = null;
        heart = null;
        su = null;
        brain = null;
        professors = null;
        laneCooldown = null;
        stat = null;
        smallBlackPaint = null;
        bigBlackPaint = null;
        smallRedPaint = null;
        smallBluePaint = null;
        medRedPaint = null;
        medRedPaint2 = null;
        medRedPaint3 = null;
        bigRedPaint = null;
        medWhiteBPaint = null;
        medWhiteBPaint2 = null;
        medWhiteBPaint3 = null;
        bigWhiteBPaint = null;
        // Call garbage collector to clean up memory.
        System.gc();
    }

    private void drawReadyUI() {
        g.drawARGB(155, 255, 255, 255);
        g.drawString("Swipe to move to another lane.",
                640, 100, medBlackPaint);
        g.drawString("Regular profs                  give good grades.", 640, 265, medBlackPaint);
        g.drawImage(Assets.prof[0], 485, 260 - Assets.prof[0].getHeight() / 2);
        g.drawString("Tap to get rid of bad profs               ", 660, 420, medBlackPaint);
        g.drawImage(Assets.prof[1], 910, 425 - Assets.prof[1].getHeight() / 2);
        g.drawString("Each            neutralizes one bad grade.", 615, 550, medBlackPaint);
        g.drawImage(Assets.su, 275, 530 - Assets.su.getHeight() / 2);
        g.drawString("Use        to boost brain power & get 5 straight A's.", 640, 680, medBlackPaint);
        g.drawImage(Assets.brain, 240, 610);
    }

    private void drawRunningUI() {
        g.drawImage(Assets.pause, screenWidth - 118, 18);
        g.drawImage(Assets.su, 465, 0);
        g.drawString("x" + sus, 578, 100, medWhiteBPaint);
        g.drawString("x" + sus, 580, 100, medRedPaint);
        g.drawImage(Assets.brain, 710, 18);
        g.drawString("x" + brains, 818, 100, medWhiteBPaint);
        g.drawString("x" + brains, 820, 100, medRedPaint);
    }

    private void drawPausedUI() {
        drawRunningUI();
        g.drawARGB(155, 0, 0, 0);
        g.drawImage(Assets.resume, 500, 350);
        g.drawImage(Assets.back, 680, 350);
    }

    private void drawGameOverUI(float d) {
        currTime+=d;

        g.drawARGB(155, 0, 0, 0);
        if (currTime<10) return;

        g.drawString("Game Over", 640, 200, bigWhiteBPaint);
        g.drawString("Game Over", 640, 200, bigRedPaint);
        if (currTime<20) return;

        g.drawString("Your Score: " + score, 640, 370, medWhiteBPaint2);
        g.drawString("Your Score: " + score, 640, 370, medRedPaint2);

        g.drawImage(Assets.replay, 500, 590);
        g.drawImage(Assets.back, 680, 590);
        if (currTime<30) return;

        g.drawString("+", 730, 510, medWhiteBPaint2);
        g.drawString("+", 730, 510, medRedPaint2);
        g.drawImage(Assets.coin, 975, 440);

        int i = stat.length/2;
        int j=30;
        for (;i>=0;i--,j+=60)
            if (j <= currTime && currTime < j + 60) {
                if (i < stat.length / 2 && stat[i + 1] >= 0) {
                    Assets.coinS.play(1);
                    coin += stat[i + 1] * (Professor.marks.get(i + 1)+2);
                    stat[i + 1] = -1;
                }
                if (j <= currTime && currTime < j + 30) {
                    g.drawString(stat[i] + "", 485, 510 - 30 - j + (int) currTime, medWhiteBPaint3);
                    g.drawString(stat[i] + "", 485, 510 - 30 - j + (int) currTime, medRedPaint3);

                } else {
                    g.drawString(stat[i] + "", 485, 510, medWhiteBPaint3);
                    g.drawString(stat[i] + "", 485, 510, medRedPaint3);

                    g.drawString(stat[i] * (Professor.marks.get(i) + 2) + "", 960 - (int) (j + 60 - currTime) * 4, 510, medWhiteBPaint3);
                    g.drawString(stat[i] * (Professor.marks.get(i) + 2) + "", 960 - (int) (j + 60 - currTime) * 4, 510, medRedPaint3);
                }
                g.drawImage(Assets.prof[0], 550, 433, 0, 68, 66, 95);
                g.drawString(Professor.grades.get(i), 585, 504, smallRedPaint);

                g.drawString(coin + "", 960, 510, medWhiteBPaint3);
                g.drawString(coin + "", 960, 510, medRedPaint3);
            }
        if (currTime<j) return;

        for (i=stat.length/2;i>=0;i--)
            if (stat[i] >= 0) {
                Assets.coinS.play(1);
                coin += stat[i] * (Professor.marks.get(i)+2);
                stat[i ] = -1;
            }

        g.drawString(coin + "", 960, 510, medWhiteBPaint3);
        g.drawString(coin + "", 960, 510, medRedPaint3);

        if (stat[stat.length-1]>=0) {
            stat[stat.length-1] = -1;
            addCoin(coin);
            if (Swarm.isLoggedIn())
                Swarm.user.saveCloudData("coin", coins + "");
        }
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
        Assets.clickS.play(1f);
        if (state == GameState.Paused)
            resume();
        else if (state == GameState.GameOver) {
            nullify();
            goToMenu();
        }
        else
            pause();
    }

    private void goToMenu() {
        game.setScreen(new MainMenuScreen(game));
    }

    public Student getStudent() {
        return student;
    }

    public static int getCoins() {
        return coins;
    }

    public static int getSus() {
        return sus;
    }

    public static int getBrains() {
        return brains;
    }

    public static void addCoin(int d) {
        coins += d;
    }
    public void addScore(int dScore) {
        score += dScore;
    }

    public void addStat(int gradeId) {
        stat[gradeId]++;
    }
}