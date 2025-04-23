import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GameView extends JPanel implements KeyListener {
    private Player player;
    private Set<Integer> keysPressed = new HashSet<>();
    private Timer timer;
    private Image mapImage;
    private Image menuImage;
    private Image deathImage;
    private static final double ZOOM = 0.60;
    private ArrayList<Projectile> flames = new ArrayList<>();
    private ArrayList<Projectile> stars = new ArrayList<>();
    private ArrayList<Projectile> lanterns = new ArrayList<>();
    private ArrayList<Projectile> fireworks = new ArrayList<>();
    private ArrayList<Projectile> shards = new ArrayList<>();
    private ArrayList<Projectile> floatingLanterns = new ArrayList<>();
    private Image[] starSprites = new Image[4];
    private Image[] flameSprites = new Image[4];
    private Image[] lanternSprites = new Image[4];
    private Image[] fireworkSprites = new Image[4];
    private Image[] shardSprites = new Image[4];
    private long startTime;
    private double spiralAngle = 0;
    private int flameSpawnCooldown = 0;
    private int lanternSpawnCooldown = 0;
    private int fireworkSpawnCooldown = 0;
    private int floatingLanternSpawnCooldown = 0;
    private int numTimesLooped = 0;
    private int regenCounter = 0;
    private int perimeter = 8000;
    private boolean isGameOver = false;
    private boolean isPlayingGame = false;
    private boolean isInMenu = false;

    // Phase repetitions in cycles ~16ms per tick

    public GameView(Player player) {
        this.player = player;
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        initializeImages();
        isInMenu = true;
        if (isInMenu) {

        }

        if (isPlayingGame) {
            int spacing = perimeter / 80;

            for (int i = 0; i < 80; i++) { // Create 80 bordering projectiles
                int start = i * spacing;
                stars.add(new Projectile(start, 2, 2000, 2000, starSprites[i % 4]));
            }

            timer = new Timer(16, e -> {
                regenCounter++;
                if (regenCounter >= 100) {
                    player.regenerateHp(5);
                    regenCounter = 0;
                }
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
                        player.takeDamage(5);
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
                        player.takeDamage(20);
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

                // Detects collision with floating lanterns
                for (Projectile floatingLantern : floatingLanterns) {
                    if (!floatingLantern.isHasHitPlayer() && floatingLantern.getHitbox().intersects(playerHitbox)) {
                        player.takeDamage(20);
                        floatingLantern.setHasHitPlayer(true);
                    }
                }

                numTimesLooped++;

                // Flame Spiral Phase 1 ~10 seconds
                if (numTimesLooped < 625) {
                    // Increase cooldown
                    flameSpawnCooldown++;
                    if (flameSpawnCooldown % 5 == 0) {
                        double radius = 1250;
                        // Parametric equation of a circle to place points on the circle
                        double x = 1000 + radius * Math.cos(spiralAngle);
                        double y = 1000 + radius * Math.sin(spiralAngle);
                        flames.add(new Projectile(x, y, 40, 60, 50, 2000, 2000, flameSprites));
                        spiralAngle += 0.5;
                    }
                }

                // Flame Spiral Phase 2 ~10 seconds
                if (numTimesLooped >= 625 && numTimesLooped < 1250) {
                    // Increase cooldown
                    flameSpawnCooldown++;
                    if (flameSpawnCooldown % 5 == 0) {
                        double radius = 1250;
                        // Parametric equation of a circle to place points on the circle
                        double x = 1000 + radius * Math.cos(spiralAngle);
                        double y = 1000 + radius * Math.sin(spiralAngle);
                        flames.add(new Projectile(x, y, 40, 60, 50, 2000, 2000, flameSprites));
                        spiralAngle -= 0.5;
                    }
                }

                // Lantern Fall Phase 1 ~20 seconds
                if (numTimesLooped >= 1250 && numTimesLooped < 2500) {
                    // Increase cooldown
                    lanternSpawnCooldown++;
                    if (lanternSpawnCooldown % 5 == 0) {
                        int randomRow = (int) (Math.random() * 20);
                        double fallingX = randomRow * 100;
                        lanterns.add(new Projectile(fallingX, 0, 40.0, 60.0, 5, 2000, 2000, lanternSprites));
                    }
                }

                // Lantern Rise Phase 2 ~20 seconds
                if (numTimesLooped >= 2500 && numTimesLooped < 3750) {
                    // Increase cooldown
                    floatingLanternSpawnCooldown++;
                    if (floatingLanternSpawnCooldown % 25 == 0) {
                        double randomRow = (int) (Math.random() * 20);
                        double spawnX = randomRow * 100;
                        double spawnY = 2000 + 10;
                        int speed = 1 + (int) (Math.random() * 5);
                        floatingLanterns.add(new Projectile(spawnX, spawnY, 40.0, 60, speed, 2000, 2000, lanternSprites));
                    }
                }


                // Checks if player is dead
                if (player.getHp() <= 0) {
                    isGameOver = true;
                    timer.stop();
                }

                repaint(); // Redraws screen
            });
            timer.start();
            startTime = System.currentTimeMillis();
        }
    }

    public void initializeImages() {
        mapImage = new ImageIcon("Resources/map.png").getImage();
        menuImage = new ImageIcon("Resources/menu.png").getImage();
        deathImage = new ImageIcon("Resources/death.png").getImage();
        starSprites[0] = new ImageIcon("Resources/Projectiles/blue_star.png").getImage();
        starSprites[1] = new ImageIcon("Resources/Projectiles/green_star.png").getImage();
        starSprites[2] = new ImageIcon("Resources/Projectiles/red_star.png").getImage();
        starSprites[3] = new ImageIcon("Resources/Projectiles/yellow_star.png").getImage();
        flameSprites[0] = new ImageIcon("Resources/Projectiles/blue_flame.png").getImage();
        flameSprites[1] = new ImageIcon("Resources/Projectiles/green_flame.png").getImage();
        flameSprites[2] = new ImageIcon("Resources/Projectiles/red_flame.png").getImage();
        flameSprites[3] = new ImageIcon("Resources/Projectiles/yellow_flame.png").getImage();
        lanternSprites[0] = new ImageIcon("Resources/Projectiles/lantern1.png").getImage();
        lanternSprites[1] = new ImageIcon("Resources/Projectiles/lantern2.png").getImage();
        lanternSprites[2] = new ImageIcon("Resources/Projectiles/lantern3.png").getImage();
        lanternSprites[3] = new ImageIcon("Resources/Projectiles/lantern4.png").getImage();
        fireworkSprites[0] = new ImageIcon("Resources/Projectiles/fireball_1.png").getImage();
        fireworkSprites[1] = new ImageIcon("Resources/Projectiles/fireball_2.png").getImage();
        fireworkSprites[2] = new ImageIcon("Resources/Projectiles/fireball_3.png").getImage();
        fireworkSprites[3] = new ImageIcon("Resources/Projectiles/fireball_4.png").getImage();
        shardSprites[0] = new ImageIcon("Resources/Projectiles/shard_1.png").getImage();
        shardSprites[1] = new ImageIcon("Resources/Projectiles/shard_2.png").getImage();
        shardSprites[2] = new ImageIcon("Resources/Projectiles/shard_3.png").getImage();
        shardSprites[3] = new ImageIcon("Resources/Projectiles/shard_4.png").getImage();
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

        if (isInMenu) {
            g2d.drawImage(menuImage, 0, 0, this);
        }

        // Save original transform
        var oldTransform = g2d.getTransform();

        // Apply zoom and translation
        g2d.scale(ZOOM, ZOOM);
        g2d.translate(-offsetX, -offsetY);

        if (isPlayingGame) {
            // Fill background black
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Draw map and player
            g.drawImage(mapImage, 0, 0, this);

            player.draw(g2d);

            for (Projectile star : stars) {
                star.draw(g2d); // Draws the star
                star.update(); // Updates movement/location
            }

            // Remove the flame once it reaches the center of the map
            Iterator<Projectile> flameIterator = flames.iterator();
            while (flameIterator.hasNext()) { // Repeats until no more lanterns
                Projectile flame = flameIterator.next();
                flame.draw(g2d);
                flame.update();

                // Removes lantern if in the middle
                int tolerance = 5;
                if (Math.abs(flame.getX() - 1000) < tolerance && Math.abs(flame.getY() - 1000) < tolerance) {
                    flameIterator.remove();
                }
            }

            // Remove the lanterns once they hit the end of the map.
            Iterator<Projectile> lanternIterator = lanterns.iterator();
            while (lanternIterator.hasNext()) { // Repeats until no more lanterns
                Projectile lantern = lanternIterator.next();
                lantern.draw(g2d);
                lantern.update();

                // Removes lantern if past the bottom border
                if (lantern.getY() >= 2000) {
                    lanternIterator.remove();
                }
            }

            // Remove the floating lanterns once they hit the top of the map.
            Iterator<Projectile> floatingLanternIterator = floatingLanterns.iterator();
            while (floatingLanternIterator.hasNext()) { // Repeats until no more lanterns
                Projectile floatingLantern = floatingLanternIterator.next();
                floatingLantern.draw(g2d);
                floatingLantern.update();

                // Removes lantern if past the top border
                if (floatingLantern.getY() < 0) {
                    floatingLanternIterator.remove();
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
            g2d.setTransform(oldTransform);
        }
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
        floatingLanterns.clear();
        spiralAngle = 0;
        flameSpawnCooldown = 0;
        lanternSpawnCooldown = 0;
        fireworkSpawnCooldown = 0;
        floatingLanternSpawnCooldown = 0;
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
