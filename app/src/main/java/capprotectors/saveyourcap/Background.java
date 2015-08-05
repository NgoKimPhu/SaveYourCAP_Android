package capprotectors.saveyourcap;

public class Background {

    private int bgX, bgY, speedX;

    public Background(int x, int y){
        bgX = x;
        bgY = y;
        speedX = 0;
    }

    public void update(float d) {
        bgX += (int) (speedX*d/1.6);

        if (bgX <= -Assets.background.getWidth()){
            bgX += 2*Assets.background.getWidth();
        }
    }

    public int getBgX() {
        return bgX;
    }

    public int getBgY() {
        return bgY;
    }

    public int getSpeedX() {
        return speedX;
    }

    public void setBgX(int bgX) {
        this.bgX = bgX;
    }

    public void setBgY(int bgY) {
        this.bgY = bgY;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

}