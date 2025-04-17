import javax.swing.*;
import java.awt.*;

public class Player {
    private int worldX, worldY;
    private final int size = 80;
    private final int speed = 7;
    private boolean isMoving = false;
    private String direction = "right"; // Sets default direction
    private int frame = 0;
    private int frameTick = 0;
    private int hp = 1000;
    private final int maxHp = 1000;

    private Image[][] walkFrames;
    private Image[] idleFrames;


    public Player(int x, int y) {
        this.worldX = x;
        this.worldY = y;
        loadSprites();
    }

    public void loadSprites() {
        walkFrames = new Image[4][2]; // 4 directions, 2 frames each
        idleFrames = new Image[4]; // 4 directions

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

    public void moveUp() {
        worldY += speed;
        direction = "up";
        isMoving = true;
    }
    public void moveDown() {
        worldY -= speed;
        direction = "down";
        isMoving = true;
    }
    public void moveLeft() {
        worldX -= speed;
        direction = "left";
        isMoving = true;
    }
    public void moveRight() {
        worldX += speed;
        direction = "right";
        isMoving = true;
    }

    public void stopMoving() {
        isMoving = false;
    }

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

    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }

    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }

    public void takeDamage(int amount) {
        hp = Math.max(0, hp - amount);
    }

    public void reset() {
        this.hp = 100;
        this.worldX = 1000;
        this.worldY = 1000;
        this.isMoving = false;
        this.frame = 0;
        this.frameTick = 0;
    }

    public Rectangle getHitbox() {
        return new Rectangle(worldX - size / 2, worldY - size / 2, size, size);
    }

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

        int barWidth = 80;
        int barHeight = 8;
        int barX = worldX - barWidth / 2;
        int barY = worldY + size / 2 + 10; // Following right below the character

        g.setColor(Color.white); // Sets outline of hp bar to dark grey
        g.fillRect(barX, barY, barWidth, barHeight);

        float hpPercent = (float) hp / maxHp;
        Color healthColor = new Color(1.0f - hpPercent, hpPercent, 0.0f); // Red to green; dynamic hp colors
        g.setColor(healthColor);
        g.fillRect(barX, barY, (int)(barWidth * hpPercent), barHeight);

        // Draws the image centered at (x, y)
        int drawX = worldX - size / 2;
        int drawY = worldY - size / 2;

        g.drawImage(sprite, drawX, drawY, size, size, null);
    }
}
