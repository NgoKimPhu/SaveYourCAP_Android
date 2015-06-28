package capprotectors.saveyourcap;

import capprotectors.framework.Image;
import capprotectors.framework.Music;
import capprotectors.framework.Sound;

public class Assets {
    public static Image mainmenu, menu, splash, background, button;

    public static Image professor;
    public static Image[] student = new Image[14];
    public static Image[] su = new Image[9];

    public static Sound click;
    public static Music theme;

    public static void load(MainGame mainGame) {
        theme = mainGame.getAudio().createMusic("menutheme.mp3");
        theme.setLooping(true);
        theme.setVolume(0.65f);
        theme.play();
    }
}
