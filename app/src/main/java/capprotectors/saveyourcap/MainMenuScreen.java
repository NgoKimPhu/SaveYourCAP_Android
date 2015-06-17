package capprotectors.saveyourcap;

import android.app.AlertDialog;
import android.content.DialogInterface;

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
        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        for (TouchEvent event: touchEvents) {
            if (event.type == TouchEvent.TOUCH_UP) {

                if (inBounds(event, 16, g.getHeight()-149, 319-16, 149-15)) {
                    //START GAME
                    Assets.click.play(1f);
                    game.setScreen(new GameScreen(game));
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

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        //Display "Exit Game?" Box
        AlertDialog.Builder builder = new AlertDialog.Builder((AndroidGame) this.game);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                ((AndroidGame) MainMenuScreen.this.game).finish();
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }
}
