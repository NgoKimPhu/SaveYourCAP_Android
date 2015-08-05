package capprotectors.saveyourcap;

import capprotectors.framework.Image;
import capprotectors.framework.Music;
import capprotectors.framework.Sound;

public class Assets {
    public static Image menu, shop, splash, background, pause, resume, replay, back, coin, su, brain;

    public static Image[] prof = new Image[2];
    public static Image[] student = new Image[9];
    public static Image[] heart = new Image[7];

    public static Sound badS, brainS, buyS, clickS, coinS, eatS, gmovS, punchS, suedS;
    public static Music theme;

    public static void load(MainGame mainGame) {
        theme = mainGame.getAudio().createMusic("theme.mp3");
        theme.setLooping(true);
        theme.setVolume(0.65f);
    }
}
