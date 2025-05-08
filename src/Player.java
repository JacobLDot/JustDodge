import javax.swing.*;
import java.awt.*;

public class Player {
    // Map size
    public static final int MAP_WIDTH = 2000;
    public static final int MAP_HEIGHT = 2000;

    // Player location
    private int worldX, worldY;

    // Player stats
    private final int size = 80;
    private int speed = 7;
    private int hp;
    private int maxHp;
    private String difficulty;

    // Sprite settings
    private int frame = 0;
    private int frameTick = 0;
    private boolean isMoving = false;
    private String direction = "right"; // Sets default direction
    private Image[][] walkFrames;
    private Image[] idleFrames;
    private Image speedy;

    // Player constructor
    public Player(int x, int y) {
        this.worldX = x;
        this.worldY = y;
        loadSprites();
    }

    public void setWorldX(double x) {
        this.worldX = (int)x;
    }

    public void setWorldY(double y) {
        this.worldY = (int)y;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    // Loads the player sprites
    public void loadSprites() {
        // 4 directions, 2 frames each
        walkFrames = new Image[4][2];

        // 4 directions
        idleFrames = new Image[4];

        speedy = new ImageIcon("Resources/speedy.png").getImage();

        walkFrames[0][0] = new ImageIcon("Resources/Sprites/down_1.png").getImage();
        walkFrames[0][1] = new ImageIcon("Resources/Sprites/down_2.png").getImage();
        walkFrames[1][0] = new ImageIcon("Resources/Sprites/up_1.png").getImage();
        walkFrames[1][1] = new ImageIcon("Resources/Sprites/up_2.png").getImage();
        walkFrames[2][0] = new ImageIcon("Resources/Sprites/left_1.png").getImage();
        walkFrames[2][1] = new ImageIcon("Resources/Sprites/left_2.png").getImage();
        walkFrames[3][0] = new ImageIcon("Resources/Sprites/right_1.png").getImage();
        walkFrames[3][1] = new ImageIcon("Resources/Sprites/right_2.png").getImage();

        idleFrames[0] = new ImageIcon("Resources/Sprites/idle_down.png").getImage();
        idleFrames[1] = new ImageIcon("Resources/Sprites/idle_up.png").getImage();
        idleFrames[2] = new ImageIcon("Resources/Sprites/idle_left.png").getImage();
        idleFrames[3] = new ImageIcon("Resources/Sprites/idle_right.png").getImage();
    }

    // Moves player up
    public void moveUp() {
        worldY += speed;
        direction = "up";
        isMoving = true;
    }

    // Moves player down
    public void moveDown() {
        worldY -= speed;
        direction = "down";
        isMoving = true;
    }

    // Moves player left
    public void moveLeft() {
        worldX -= speed;
        direction = "left";
        isMoving = true;
    }

    // Moves player right
    public void moveRight() {
        worldX += speed;
        direction = "right";
        isMoving = true;
    }

    // Stops moving player
    public void stopMoving() {
        isMoving = false;
    }

    // Updates player sprites when moving
    public void updateAnimation() {
        if (isMoving) {
            frameTick++;
            if (frameTick > 10) { // Change frame every ~160 ms
                frame = (frame + 1) % 2; // Swap to the next animation
                frameTick = 0;
            }
        }
        else {
            frame = 0;
            frameTick = 0;
        }
    }

    // Returns player X location
    public int getWorldX() {
        return worldX;
    }

    // Returns player Y location
    public int getWorldY() {
        return worldY;
    }

    // Returns player hp
    public int getHp() {
        return hp;
    }

    // Returns player maxHp
    public int getMaxHp() {
        return maxHp;
    }

    // Removes hp when player is damaged
    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    // Regenerate player hp
    public void regenerateHp(int amount) {
        if (hp < maxHp) {
            hp = Math.min(maxHp, hp + amount);
        }
    }

    // Resets to start when game is reset
    public void reset() {
        this.hp = 100;
        this.maxHp = 100;
        this.worldX = MAP_WIDTH / 2;
        this.worldY = (int)(MAP_HEIGHT * 0.7805);
        this.isMoving = false;
        this.frame = 0;
        this.frameTick = 0;
    }

    // Creates rectangle player hitbox
    public Rectangle getHitbox() {
        return new Rectangle(worldX - size / 2, worldY - size / 2, size, size);
    }

    // Draws player frames
    public void draw(Graphics g) {
        int directionIndex = switch (direction) { // Check direction player is moving
            case "up" -> 0;
            case "down" -> 1;
            case "left" -> 2;
            case "right" -> 3;
            default -> 1;
        };

        Image sprite;
        if (isMoving) { // If player is moving, use corresponding image
            sprite = walkFrames[directionIndex][frame];
        } else { // Else, use the corresponding idle frame
            sprite = idleFrames[directionIndex];
        }

        // Creates HP bar and sets location
        int barWidth = 80;
        int barHeight = 8;
        int barX = worldX - barWidth / 2;

        // Following right below the character
        int barY = worldY + size / 2 + 10;

        // Sets outline of hp bar to dark grey
        g.setColor(Color.white);
        g.fillRect(barX, barY, barWidth, barHeight);

        float hpPercent = (float) hp / maxHp;
        // Red to green; dynamic hp colors
        Color healthColor = new Color(1.0f - hpPercent, hpPercent, 0.0f);
        g.setColor(healthColor);
        g.fillRect(barX, barY, (int)(barWidth * hpPercent), barHeight);

        if (speed == 15) {
            g.drawImage(speedy, worldX + 16, worldY - 80, 64, 64, null);
        }

        // Draws the image centered at (x, y)
        int drawX = worldX - size / 2;
        int drawY = worldY - size / 2;

        g.drawImage(sprite, drawX, drawY, size, size, null);
    }
}
