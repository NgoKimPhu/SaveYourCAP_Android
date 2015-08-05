package capprotectors.saveyourcap;

import android.graphics.Color;
import android.graphics.Paint;

import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActiveUser.GotCloudDataCB;

import java.util.List;

import capprotectors.framework.Game;
import capprotectors.framework.Graphics;
import capprotectors.framework.Input.TouchEvent;
import capprotectors.framework.Screen;

public class ShopScreen extends Screen {

    private static int coins, sus, brains;

    Paint paint, p2;

    public ShopScreen(final Game game) {
        super(game);
        coins = GameScreen.getCoins();
        sus = GameScreen.getSus();
        brains = GameScreen.getBrains();
        final int t1=coins,t2=sus,t3=brains;
        if (Swarm.isLoggedIn()) {
            Swarm.user.getCloudData("coin", new GotCloudDataCB() {
                public void gotData(String data) {
                    // Did our request fail (network offline, and not cached)? Or has this key never been set?
                    if (data == null || data.length() == 0)
                        data = "0";
                    // Parse the level data for later use
                    ShopScreen.coins += Integer.parseInt(data)-t1;
                }
            });
            Swarm.user.getCloudData("su", new GotCloudDataCB() {
                public void gotData(String data) {
                    if (data == null || data.length() == 0)
                        data = "0";
                    ShopScreen.sus += Integer.parseInt(data)-t2;
                }
            });
            Swarm.user.getCloudData("brain", new GotCloudDataCB() {
                public void gotData(String data) {
                    if (data == null || data.length() == 0)
                        data = "0";
                    ShopScreen.brains += Integer.parseInt(data)-t3;
                }
            });
        }
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(80);
        p2 = new Paint(paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        p2.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        for (TouchEvent event : touchEvents)
            if (event.type == TouchEvent.TOUCH_UP) {
                if (inBounds(event, 1280 - 118, 18, 100, 100)) {
                    Assets.clickS.play(1f);
                    game.setScreen(new MainMenuScreen(game));
                }
                if (inBounds(event, 748, 179, 326, 138)) {
                    Assets.clickS.play(1f);
                    buy("SU");
                }
                if (inBounds(event, 471, 378, 300, 127)) {
                    Assets.clickS.play(1f);
                    buy("brain");
                }
            }
    }

    private void buy(String item) {
        if (item.equals("SU")) {
            if (coins>=30) {
                Assets.buyS.play(1);
                coins -= 30;
                GameScreen.addCoin(-30);
                sus += 1;
                GameScreen.addSus(1);
                if (Swarm.isLoggedIn()) {
                    Swarm.user.saveCloudData("coin", coins + "");
                    Swarm.user.saveCloudData("su", sus + "");
                }
            }
        }
        else if (item.equals("brain")) {
            if (coins>=80) {
                Assets.buyS.play(1);
                coins -= 80;
                GameScreen.addCoin(-80);
                brains +=1;
                GameScreen.addBrains(1);
                if (Swarm.isLoggedIn()) {
                    Swarm.user.saveCloudData("coin", coins + "");
                    Swarm.user.saveCloudData("brain", brains + "");
                }
            }
        }
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        return event.x > x && event.x < x + width - 1 && event.y > y && event.y < y + height - 1;
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawImage(Assets.shop, 0, 0);
        g.drawString(sus + "", 740, 288, paint);
        g.drawString(brains+"", 460, 468, paint);
        g.drawString(coins+"", 300, 666, p2);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        Assets.clickS.play(1f);
        game.setScreen(new MainMenuScreen(game));
    }
}
