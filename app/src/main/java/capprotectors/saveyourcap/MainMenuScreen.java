package capprotectors.saveyourcap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.swarmconnect.Swarm;

import java.util.List;

import capprotectors.framework.Game;
import capprotectors.framework.Graphics;
import capprotectors.framework.Input.TouchEvent;
import capprotectors.framework.Screen;
import capprotectors.implementation.AndroidGame;

public class MainMenuScreen extends Screen {
    public MainMenuScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        //Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        for (TouchEvent event : touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {

                if (inBounds(event, 80, 301, 629-80, 656-301)) {
                    //START GAME
                    Assets.click.play(1f);
                    game.setScreen(new GameScreen(game));
                }

                if (inBounds(event, 80, 679, 629 - 80, 751 - 679)) {
                    Assets.click.play(1f);
                    Swarm.showDashboard();
                }

                if (inBounds(event, 653, 299, 1202 - 653, 371 - 299)) {
                    Assets.click.play(1f);
                    toggleMusic();
                }

                if (inBounds(event, 653, 391, 1202 - 653, 463 - 391)) {
                    Assets.click.play(1f);
                    Swarm.showAchievements();
                }

                if (inBounds(event, 653, 493, 1202 - 653, 565 - 493)) {
                    Assets.click.play(1f);
                    Swarm.showLeaderboards();
                }

                if (inBounds(event, 653, 584, 1202 - 653, 656 - 584)) {
                    Assets.click.play(1f);
                    Swarm.showStore();
                }

                if (inBounds(event, 653, 679, 1202 - 653, 751 - 679)) {
                    confirmExit();
                }

            }
        }
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width,
                             int height) {
        return event.x > x && event.x < x + width - 1 && event.y > y
                && event.y < y + height - 1;
    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawScaledImage(Assets.menu, 0, 0, g.getWidth(), g.getHeight(), 0, 0, Assets.menu.getWidth(), Assets.menu.getHeight());
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        Assets.theme.play();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        //Display "Exit Game?" Box
        confirmExit();
    }

    public void toggleMusic() {
        ((AndroidGame) game).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder((AndroidGame) game);
                builder.setCancelable(true);
                builder.setMessage("The only setting: Toggle Music :D");
                builder.setPositiveButton("Music on!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Assets.theme.setVolume(0.85f);
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton("No music plz", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Assets.theme.setVolume(0);
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void confirmExit() {
        ((AndroidGame) game).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder((AndroidGame) game);
                builder.setCancelable(false);
                builder.setMessage("Do you want to Exit?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user pressed "yes", then he is allowed to exit from application
                        ((AndroidGame) MainMenuScreen.this.game).finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if user select "No", just cancel this dialog and continue with app
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

}