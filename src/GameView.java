import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GameView extends JPanel implements KeyListener {
    private Player player;
    private Set<Integer> keysPressed = new HashSet<>();
    private Timer timer;
    private Image mapImage;
    private Image deathImage;
    private static final double ZOOM = 0.60;
    private ArrayList<Projectile> flames = new ArrayList<>();
    private ArrayList<Projectile> stars = new ArrayList<>();
    private ArrayList<Projectile> lanterns = new ArrayList<>();
    private ArrayList<Projectile> fireworks = new ArrayList<>();
    private ArrayList<Projectile> shards = new ArrayList<>();
    private ArrayList<Projectile> flowers = new ArrayList<>();
    private ArrayList<Projectile> flowerShurikens = new ArrayList<>();
    private Image[] starSprites = new Image[4];
    private Image[] flameSprites = new Image[4];
    private Image[] lanternSprites = new Image[4];
    private Image[] fireworkSprites = new Image[4];
    private Image[] shardSprites = new Image[2];
    private Image[] flowerSprites = new Image[4];
    private Image[] flowerShurikenSprites = new Image[2];
    private long startTime;
    private double spiralAngle = 0;
    private int flameSpawnCooldown = 0;
    private int lanternSpawnCooldown = 0;
    private int fireworkSpawnCooldown = 0;
    private int flowerSpawnCooldown = 0;
    private int flowerShurikenSpawnCooldown = 0;
    private int numTimesLooped = 0;
    private boolean isGameOver = false;

    // Phase repetitions in cycles ~16ms per tick
    private int wavePhaseStart = 1875;
    private int wavePhaseEnd = 3750;

    private int finaleStart = 4063;
    private int finaleEnd = 5863;

    private int flowerPhaseStart = 5864;
    private int flowerPhaseEnd = 7663;

    private int flowerShurikenStart = 7663;
    private int flowerShurikenEnd = 9000;

    public GameView(Player player) {
        this.player = player;
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        mapImage = new ImageIcon("Resources/map.png").getImage();
        deathImage = new ImageIcon("Resources/death2.png").getImage();
        starSprites[0] = new ImageIcon("Resources/Projectiles/blue_star.png").getImage();
        starSprites[1] = new ImageIcon("Resources/Projectiles/green_star.png").getImage();
        starSprites[2] = new ImageIcon("Resources/Projectiles/red_star.png").getImage();
        starSprites[3] = new ImageIcon("Resources/Projectiles/yellow_star.png").getImage();
        flameSprites[0] = new ImageIcon("Resources/Projectiles/blue_flame.png").getImage();
        flameSprites[1] = new ImageIcon("Resources/Projectiles/green_flame.png").getImage();
        flameSprites[2] = new ImageIcon("Resources/Projectiles/red_flame.png").getImage();
        flameSprites[3] = new ImageIcon("Resources/Projectiles/yellow_flame.png").getImage();
        lanternSprites[0] = new ImageIcon("Resources/Projectiles/blue_lantern.png").getImage();
        lanternSprites[1] = new ImageIcon("Resources/Projectiles/green_lantern.png").getImage();
        lanternSprites[2] = new ImageIcon("Resources/Projectiles/grey_lantern.png").getImage();
        lanternSprites[3] = new ImageIcon("Resources/Projectiles/black_lantern.png").getImage();
        fireworkSprites[0] = new ImageIcon("Resources/Projectiles/fireball_1.png").getImage();
        fireworkSprites[1] = new ImageIcon("Resources/Projectiles/fireball_2.png").getImage();
        fireworkSprites[2] = new ImageIcon("Resources/Projectiles/fireball_3.png").getImage();
        fireworkSprites[3] = new ImageIcon("Resources/Projectiles/fireball_4.png").getImage();
        shardSprites[0] = new ImageIcon("Resources/Projectiles/shard_1.png").getImage();
        shardSprites[1] = new ImageIcon("Resources/Projectiles/shard_2.png").getImage();
        flowerSprites[0] = new ImageIcon("Resources/Projectiles/flower_1.png").getImage();
        flowerSprites[1] = new ImageIcon("Resources/Projectiles/flower_2.png").getImage();
        flowerSprites[2] = new ImageIcon("Resources/Projectiles/flower_3.png").getImage();
        flowerSprites[3] = new ImageIcon("Resources/Projectiles/flower_4.png").getImage();
        flowerShurikenSprites[0] = new ImageIcon("Resources/Projectiles/flowershuriken_1").getImage();
        flowerShurikenSprites[1] = new ImageIcon("Resources/Projectiles/flowershuriken_2").getImage();

        int perimeter = 8000;
        int spacing = perimeter / 80;

        for (int i = 0; i < 80; i++) { // Create 80 bordering projectiles
            int start = i * spacing;
            stars.add(new Projectile(start, 2, 2000, 2000, starSprites[i % 4]));
        }

        timer = new Timer(16, e -> {
            boolean moving = false;
            if (keysPressed.contains(KeyEvent.VK_W)) { // Up
                player.moveDown();
                moving = true;
            }
            if (keysPressed.contains(KeyEvent.VK_S)) { // Down
                player.moveUp();
                moving = true;
            }
            if (keysPressed.contains(KeyEvent.VK_A)) { // Left
                player.moveLeft();
                moving = true;
            }
            if (keysPressed.contains(KeyEvent.VK_D)) { // Right
                player.moveRight();
                moving = true;
            }

            if (!moving) player.stopMoving(); // If not moving stop the animation

            player.updateAnimation(); // Updates the player frame

            // Detect collision with the stars
            Rectangle playerHitbox = player.getHitbox();
            for (Projectile star : stars) {
                if (star.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(10);
                    break;
                }
            }

            // Detects collision with flames
            for (Projectile flame : flames) {
                if (!flame.isHasHitPlayer() && flame.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(5);
                    flame.setHasHitPlayer(true);
                }
            }

            // Detects collision with lanterns
            for (Projectile lantern : lanterns) {
                if (!lantern.isHasHitPlayer() && lantern.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(10);
                    lantern.setHasHitPlayer(true);
                }
            }

            // Detects collision with fireworks
            for (Projectile firework : fireworks) {
                if (!firework.isHasHitPlayer() && firework.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(5);
                    firework.setHasHitPlayer(true);
                }
            }

            // Detects collision with shards
            for (Projectile shard : shards) {
                if (shard.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(1);
                    break;
                }
            }

            // Detects collision with flowers
            for (Projectile flower : flowers) {
                if (!flower.isHasHitPlayer() && flower.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(10);
                    flower.setHasHitPlayer(true);
                }
            }

            // Detects collision with flower shurikens
            for (Projectile flowerShuriken : flowerShurikens) {
                if (!flowerShuriken.isHasHitPlayer() && flowerShuriken.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(10);
                    flowerShuriken.setHasHitPlayer(true);
                }
            }

            numTimesLooped++;

            // Flame Spiral Phase 1 ~10 seconds
            if (numTimesLooped < wavePhaseStart) {
                flameSpawnCooldown++;
                if (flameSpawnCooldown % 10 == 0) {
                    double radius = 1250;
                    // Parametric equation of a circle to place points on the circle
                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    flames.add(new Projectile(x, y, 40, 60, 25, 2000, 2000, flameSprites));
                    spiralAngle += 180;
                }
            }

            // Lantern Fall + Flame Spiral Phase 2 ~20 seconds
            if (numTimesLooped > 625 && numTimesLooped < wavePhaseStart) {
                lanternSpawnCooldown++;
                if (lanternSpawnCooldown % 15 == 0) {
                    int randomRow = (int)(Math.random() * 20);
                    double fallingX = randomRow * 100;
                    lanterns.add(new Projectile(fallingX, 0, 40.0, 60.0, 5, 2000, 2000, lanternSprites));
                }
            }

            // Rocket Rebound Explode Phase 3 ~30 seconds, the 313 ticks ~5 seconds for other projectiles to clear off
            if (numTimesLooped >= (wavePhaseStart + 313) && numTimesLooped < (wavePhaseEnd + 313)) {
                fireworkSpawnCooldown++;
                if (fireworkSpawnCooldown % 20 == 0) {
                    int numFireworks = 1;
                    for (int i = 0; i < numFireworks; i++) {
                        double angle = i * (360 / numFireworks);
                        fireworks.add(new Projectile(1000, 1000, 40, 60, 7.5, 2000, 2000, fireworkSprites));
                    }
                }
            }

            // Finale Phase ~25 seconds
            if (numTimesLooped >= finaleStart && numTimesLooped < finaleEnd) {
                flameSpawnCooldown++;
                if (flameSpawnCooldown % 20 == 0) {
                    double radius = 1250;

                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    flames.add(new Projectile(x, y, 40, 60, 25, 2000, 2000, flameSprites));
                    spiralAngle += 180;
                }
                lanternSpawnCooldown++;
                if (lanternSpawnCooldown % 30 == 0) {
                    int randomRow = (int)(Math.random() * 20);
                    double fallingX = randomRow * 100;
                    lanterns.add(new Projectile(fallingX, 0, 40.0, 60.0, 5, 2000, 2000, lanternSprites));
                }
                fireworkSpawnCooldown++;
                if (fireworkSpawnCooldown % 40 == 0) {
                    int numFireworks = 1;
                    for (int i = 0; i < numFireworks; i++) {
                        double angle = i * (360 / numFireworks);
                        fireworks.add(new Projectile(1000, 1000, 40, 60, 7.5, 2000, 2000, fireworkSprites));
                    }
                }
            }

            // Flower Phase ~ 30 seconds + 5 second pause
            if (numTimesLooped >= (flowerPhaseStart + 313) && numTimesLooped < (flowerPhaseEnd + 313)) {
                flowerSpawnCooldown++;
                if (flowerSpawnCooldown % 15 == 0) {
                    double randomRow = (int)(Math.random() * 20);
                    double spawnX = randomRow * 100;
                    double spawnY = 2000 + 10;
                    int speed = 1 + (int)(Math.random() * 10);
                    flowers.add(new Projectile(spawnX, spawnY, 80.0, 80, speed, 2000, 2000, flowerSprites));
                }
            }

            // Flower Shuriken Phase ~ 30 seconds
            if (numTimesLooped >= flowerShurikenStart && numTimesLooped < flowerShurikenEnd) {
                flowerShurikenEnd++;
                if (flowerShurikenSpawnCooldown % 5 == 0) {
                    double randomRow = (int)(Math.random() * 20);
                    double spawnX = randomRow * 100;
                    double spawnY = 2000 + 10;
                    int speed = 1 + (int)(Math.random() * 10);
                    flowerShurikens.add(new Projectile(spawnX, spawnY, 80.0, 80, speed, 2000, 2000, flowerSprites));
                }
            }

            if (player.getHp() <= 0) {
                isGameOver = true;
                timer.stop();
            }

            repaint(); // Redraws screen
        });
        timer.start();
        startTime = System.currentTimeMillis();
    }

    public void paint(Graphics g) {
        super.paint(g);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int offsetX = (int)(player.getWorldX() - centerX / ZOOM);
        int offsetY = (int)(player.getWorldY() - centerY / ZOOM);

        // Cast to Graphics2D for scaling
        Graphics2D g2d = (Graphics2D) g;

        if (isGameOver) {
            g2d.drawImage(deathImage, 0, 0, getWidth(), getHeight(), null);
            return;
        }

        // Fill background black
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Save original transform
        var oldTransform = g2d.getTransform();

        // Apply zoom and translation
        g2d.scale(ZOOM, ZOOM);
        g2d.translate(-offsetX, -offsetY);

        // Draw map and player
        g.drawImage(mapImage, 0, 0, this);

        player.draw(g2d);

        for (Projectile star : stars) {
            star.draw(g2d); // Draws the star
            star.update(); // Updates movement/location
        }

        for (Projectile flame : flames) {
            flame.draw(g2d);
            flame.update();
        }

        // Remove the lanterns once they hit the end of the map.
        Iterator<Projectile> lanternIterator = lanterns.iterator();
        while (lanternIterator.hasNext()) { // Repeats until no more lanterns
            Projectile lantern = lanternIterator.next();
            lantern.draw(g2d);
            lantern.update();

            // Removes lantern if past the bottom border
            if (lantern.getY() > 2000) {
                lanternIterator.remove();
            }
        }

        Iterator<Projectile> fireworkIterator = fireworks.iterator();
        while (fireworkIterator.hasNext()) { // Repeats until no more fireworks/shards
            Projectile firework = fireworkIterator.next();
            firework.update();

            // If the firework bounced 3 times, explode it
            if (firework.shouldExplode()) {
                int numShards = 360;
                double shardSpeed = 5;
                int shardLifetime = 100;

                for (int i = 0; i < numShards; i++) {
                    double angle = Math.toRadians(i * (360.0 / numShards));
                    double dx = shardSpeed * Math.cos(angle);
                    double dy = shardSpeed * Math.sin(angle);
                    shards.add(new Projectile(firework.getX(), firework.getY(), dx, dy, 3, 2000, 2000, shardSprites, shardLifetime));
                }
            }
            firework.draw(g2d);

            if (firework.getBounceCount() >= 3) {
                fireworkIterator.remove();
            }
        }

        // Removes shards after a period of time
        Iterator<Projectile> shardIterator = shards.iterator();
        while (shardIterator.hasNext()) { // Repeats until no more shards
            Projectile shard = shardIterator.next();
            shard.draw(g2d);
            shard.update();

            if (shard.isExpiredShard()) {
                shardIterator.remove();
            }
        }

        for (Projectile flower : flowers) {
            flower.draw(g2d);
            flower.update();
        }

        g2d.setTransform(oldTransform);
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());

        if (isGameOver && e.getKeyCode() == KeyEvent.VK_R) {
            restartGame();
        }
    }

    public void restartGame() {
        player.reset();
        flames.clear();
        stars.clear();
        lanterns.clear();
        fireworks.clear();
        shards.clear();
        flowers.clear();
        spiralAngle = 0;
        flameSpawnCooldown = 0;
        lanternSpawnCooldown = 0;
        fireworkSpawnCooldown = 0;
        flowerSpawnCooldown = 0;
        numTimesLooped = 0;
        isGameOver = false;

        int perimeter = 8000;
        int spacing = perimeter / 80;

        for (int i = 0; i < 80; i++) { // Create 80 bordering projectiles
            int start = i * spacing;
            stars.add(new Projectile(start, 2, 2000, 2000, starSprites[i % 4]));
        }

        timer.start();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }
}
