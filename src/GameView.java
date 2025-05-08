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
    private Image startImage;
    private Image selectDifficultyImage;
    private Image easyImage;
    private Image defaultImage;
    private Image hardImage;
    private Image nightmareImage;
    private Image titleImage;
    private Image borderImage;
    private static final double ZOOM = 0.60;
    public static final int MAP_WIDTH = 2000;
    public static final int MAP_HEIGHT = 2000;
    private ArrayList<Projectile> flames = new ArrayList<>();
    private ArrayList<Projectile> stars = new ArrayList<>();
    private ArrayList<Projectile> lanterns = new ArrayList<>();
    private ArrayList<Projectile> fireworks = new ArrayList<>();
    private ArrayList<Projectile> shards = new ArrayList<>();
    private ArrayList<Projectile> floatingLanterns = new ArrayList<>();
    private ArrayList<Projectile> flowers = new ArrayList<>();
    private Image[] starSprites = new Image[4];
    private Image[] flameSprites = new Image[8];
    private Image[] lanternSprites = new Image[4];
    private Image[] fireworkSprites = new Image[4];
    private Image[] shardSprites = new Image[4];
    private Image[] flowerSprites = new Image[4];
    private Image[] fireballSprites = new Image[4];
    private Image[] flowerShurikenSprites = new Image[2];
    private Image crownImage;
    private long startTime;
    private double survivalTime;
    private double spiralAngle = 0;
    private double spiralAngle2 = 0;
    private int flameSpawnCooldown = 0;
    private int lanternSpawnCooldown = 0;
    private int fireworkSpawnCooldown = 0;
    private int floatingLanternSpawnCooldown = 0;
    private int flowerSpawnCooldown = 0;
    private int numTimesLooped = 0;
    private int regenCounter = 0;
    private int perimeter = 8000;
    private boolean isGameOver = false;
    private boolean isPlayingGame = false;
    private boolean isInMenu = false;
    private boolean isInDifficultyMenu = false;
    private String difficulty = "default";

    private JButton startButton;
    private JButton selectButton;
    private JButton easyButton;
    private JButton defaultButton;
    private JButton hardButton;
    private JButton nightmareButton;

    private Icon startIcon;
    private Icon selectDifficultyIcon;
    private Icon easyIcon;
    private Icon defaultIcon;
    private Icon hardIcon;
    private Icon nightmareIcon;


    public void setIsPlayingGame(Boolean status) {
        isPlayingGame = status;
        isInMenu = !status;
        isInDifficultyMenu = !status;

        if (isPlayingGame) {
            remove(startButton);
            remove(selectButton);
            revalidate();
            repaint();
            requestFocusInWindow();
            startGame();
        }
    }

    public void setIsInMenu(Boolean status) {
        // Creates buttons behind the background
        removeAll();
        isInDifficultyMenu = status;
        isPlayingGame = !status;
        isInMenu = !status;

        if (isInDifficultyMenu) {
            remove(startButton);
            remove(selectButton);
            easyButton = new JButton(easyIcon);
//            easyButton.setBounds(279, 400, 442, 50);
            easyButton.setBounds(190, 350, 620, 70);
            easyButton.addActionListener(e -> {
                difficulty = "easy";
                player.setHp(250);
                player.setMaxHp(250);
                returnToMenu();
            });
            defaultButton = new JButton(defaultIcon);
//            defaultButton.setBounds(279, 500, 442, 50);
            defaultButton.setBounds(190, 450, 620, 70);
            defaultButton.addActionListener(e -> {
                difficulty = "default";
                player.setHp(100);
                player.setMaxHp(100);
                returnToMenu();
            });
            hardButton = new JButton(hardIcon);
//            hardButton.setBounds(279, 600, 442, 50);
            hardButton.setBounds(190, 550, 620, 70);
            hardButton.addActionListener(e -> {
                difficulty = "hard";
                player.setHp(75);
                player.setMaxHp(75);
                returnToMenu();
            });
            nightmareButton = new JButton(nightmareIcon);
//            nightmareButton.setBounds(279, 700, 442, 50);
            nightmareButton.setBounds(190, 650, 620, 70);
            nightmareButton.addActionListener(e -> {
                difficulty = "nightmare";
                player.setHp(50);
                player.setMaxHp(50);
                returnToMenu();
            });
            repaint();
            revalidate();
            add(easyButton);
            add(defaultButton);
            add(hardButton);
            add(nightmareButton);
        }
    }

    private void returnToMenu() {
        removeAll();
        add(startButton);
        add(selectButton);
        setIsPlayingGame(false);
        revalidate();
        repaint();
    }

    public GameView() {
        int playerStartX = MAP_WIDTH / 2;
        int playerStartY = (int)(MAP_HEIGHT * 0.7805);
        this.player =  new Player(playerStartX, playerStartY);
        player.setHp(100);
        player.setMaxHp(100);
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        initializeImages();

        setLayout(null);
        startButton = new JButton(startIcon);
//        startButton.setBounds(279, 400, 442, 50);
        startButton.setBounds(190, 350, 620, 70);
        startButton.addActionListener(e -> setIsPlayingGame(true));
        add(startButton);

        selectButton = new JButton(selectDifficultyIcon);
        selectButton.setBounds(190, 450, 620, 70);
        selectButton.addActionListener(e -> setIsInMenu(true));
        add(selectButton);

        setIsPlayingGame(false);
    }

    public void startGame() {
        int spacing = perimeter / 80;

        for (int i = 0; i < 80; i++) { // Create 80 bordering projectiles
            int start = i * spacing;
            stars.add(new Projectile(start, 2, 2000, 2000, starSprites[i % 4]));
        }

        // Phase repetitions in cycles ~16ms per tick
        timer = new Timer(16, e -> {
            // Regenerates hp based on level
            regenCounter++;
            if (difficulty.equals("easy")) {
                if (regenCounter >= 100) {
                    player.regenerateHp(5);
                    regenCounter = 0;
                }
            } else if (difficulty.equals("default")) {
                if (regenCounter >= 200) {
                    player.regenerateHp(5);
                    regenCounter = 0;
                }
            } else if (difficulty.equals("hard")){
                if (regenCounter >= 400) {
                    player.regenerateHp(5);
                    regenCounter = 0;
                }
            } else {
                regenCounter = 0;
            }
            boolean moving = false;
            if (keysPressed.contains(KeyEvent.VK_W) || keysPressed.contains(KeyEvent.VK_UP)) { // Up
                player.moveDown();
                moving = true;
            }
            if (keysPressed.contains(KeyEvent.VK_S) || keysPressed.contains(KeyEvent.VK_DOWN)) { // Down
                player.moveUp();
                moving = true;
            }
            if (keysPressed.contains(KeyEvent.VK_A) || keysPressed.contains(KeyEvent.VK_LEFT)) { // Left
                player.moveLeft();
                moving = true;
            }
            if (keysPressed.contains(KeyEvent.VK_D) || keysPressed.contains(KeyEvent.VK_RIGHT)) { // Right
                player.moveRight();
                moving = true;
            }

            if (!moving) player.stopMoving(); // If not moving stop the animation

            player.updateAnimation(); // Updates the player frame

            // Detect collision with the star border
            Rectangle playerHitbox = player.getHitbox();
            for (Projectile star : stars) {
                if (star.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(15);
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
                    player.takeDamage(15);
                    lantern.setHasHitPlayer(true);
                }
            }

            // Detects collision with fireworks
            for (Projectile firework : fireworks) {
                if (!firework.isHasHitPlayer() && firework.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(10);
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
                    player.takeDamage(15);
                    floatingLantern.setHasHitPlayer(true);
                }
            }

            // Detects collision with flowers
            for (Projectile flower : flowers) {
                if (flower.getHitbox().intersects(playerHitbox)) {
                    player.takeDamage(1);
                }
            }

            numTimesLooped++;

            // Flame Spiral Phase 1 ~10 seconds
            if (numTimesLooped < 640) {
                // Increase cooldown
                flameSpawnCooldown++;
                if (flameSpawnCooldown % 5 == 0) {
                    double radius = 1250;
                    // Parametric equation of a circle to place points on the circle
                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    flames.add(new Projectile(x, y, 40, 60, 1000, 1000,50, 2000, 2000, flameSprites));
                    spiralAngle += 0.5;
                }
            }

            // Flame Spiral Phase 2 ~10 seconds
            if (numTimesLooped >= 640 && numTimesLooped < 1280) {
                // Increase cooldown
                flameSpawnCooldown++;
                if (flameSpawnCooldown % 4 == 0) {
                    double radius = 1250;
                    double randomAngle = Math.random() * 2 * Math.PI;
                    double x1 = 1000 + radius * Math.cos(randomAngle);
                    double y1 = 1000 + radius * Math.sin(randomAngle);

                    double oppositeAngle = Math.random() * 2 * Math.PI;
                    double x2 = 1000 + radius * Math.cos(oppositeAngle);
                    double y2 = 1000 + radius * Math.sin(oppositeAngle);
                    flames.add(new Projectile(x1, y1, 40, 40, 1000, 1000,50, 2000, 2000, fireballSprites));
                    flames.add(new Projectile(x2, y2, 40, 40, 1000,1000,50, 2000, 2000, fireballSprites));
                }
            }

            // Flame Spiral Phase 3
            if (difficulty.equals("nightmare")) {
                flowerSpawnCooldown++;
                if (flowerSpawnCooldown % 10 == 0) {
                    double radius = 1250;
                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    int speed = 5 + (int)(Math.random() * 5);
                    flowers.add(new Projectile(1000, 1000, x, y, 80.0, 80, speed, 2000, 2000, flameSprites));
                    spiralAngle += 0.5;
                }
            }

            // Rebound Phase ~ 20 seconds
            if (numTimesLooped >= 1280 && numTimesLooped < 2560) {
                // Increase cooldown
                fireworkSpawnCooldown++;
                if (fireworkSpawnCooldown % 25 == 0) {
                    int numFireworks = 1;
                    for (int i = 0; i < numFireworks; i++) {
                        double angle = i * (360 / numFireworks);
                        fireworks.add(new Projectile(1000, 1000, 80, 80, 5.5, 2000, 2000, flowerShurikenSprites));
                    }
                }
                // Increase cooldown
                flameSpawnCooldown++;
                if (flameSpawnCooldown % 5 == 0) {
                    double radius = 1250;
                    // Parametric equation of a circle to place points on the circle
                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    flames.add(new Projectile(x, y, 80, 80, 1000, 1000,50, 2000, 2000, flameSprites));
                    spiralAngle += 0.5;
                }
            }

            // Lantern Fall ~20 seconds
            if (numTimesLooped >= 2560 && numTimesLooped < 3840) {
                // Increase cooldown
                lanternSpawnCooldown++;
                if (lanternSpawnCooldown % 5 == 0) {
                    int randomRow = (int) (Math.random() * 20);
                    double fallingX = randomRow * 100;
                    lanterns.add(new Projectile(fallingX, 0, 40.0, 64.0, 10, 2000, 2000, lanternSprites));
                }
            }

            // Lantern Rise ~20 seconds
            if (numTimesLooped >= 3840 && numTimesLooped < 5120) {
                // Increase cooldown
                floatingLanternSpawnCooldown++;
                if (floatingLanternSpawnCooldown % 7 == 0) {
                    double randomRow = (int) (Math.random() * 20);
                    double spawnX = randomRow * 100;
                    double spawnY = 2000 + 10;
                    int speed = 3 + (int) (Math.random() * 7);
                    floatingLanterns.add(new Projectile(spawnX, spawnY, 40.0, 64, speed, 2000, 2000, lanternSprites));
                }
            }

            // Flowers Phase 1 ~20 seconds
            if (numTimesLooped >= 5120 && numTimesLooped < 6400) {
                flowerSpawnCooldown++;
                if (flowerSpawnCooldown % 5 == 0) {
                    double radius = 1250;
                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    int speed = 3 + (int)(Math.random() * 5);
                    flowers.add(new Projectile(x, y, 1000, 1000, 80.0, 80, speed, 2000, 2000, flowerSprites));
                    spiralAngle += 0.5;
                }
            }

            // Flower Phase 2 ~20 seconds
            if (numTimesLooped >= 6400 && numTimesLooped < 7680) {
                flowerSpawnCooldown++;
                if (flowerSpawnCooldown % 5 == 0) {
                    double radius = 1250;
                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    int speed = 5 + (int)(Math.random() * 5);
                    flowers.add(new Projectile(1000, 1000, x, y, 80.0, 80, speed, 2000, 2000, flowerSprites));
                    spiralAngle += 0.5;
                }
            }

            // Flower Phase 3 ~20 seconds
            if (numTimesLooped >= 7680 && numTimesLooped < 8960) {
                flowerSpawnCooldown++;
                if (flowerSpawnCooldown % 3 == 0) {
                    double radius = 1250;
                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    double x2 = 1000 + radius * Math.cos(spiralAngle2);
                    double y2 = 1000 + radius * Math.sin(spiralAngle2);
                    int speed = 5 + (int)(Math.random() * 5);
                    flowers.add(new Projectile(1000, 1000, x, y, 80.0, 80, speed, 2000, 2000, flowerSprites));
                    flowers.add(new Projectile(1000, 1000, x2, y2, 80.0, 80, speed, 2000, 2000, flowerSprites));
                    spiralAngle += 0.5;
                    spiralAngle2 -= 0.5;
                }
            }

            // Lantern Fall ~20 seconds
            if (numTimesLooped >= 8960 && numTimesLooped < 10240) {
                // Increase cooldown
                lanternSpawnCooldown++;
                if (lanternSpawnCooldown % 1 == 0) {
                    int randomRow = (int) (Math.random() * 5);
                    int randomRow2 = (int) (Math.random() * 5) + 15;
                    double fallingX = randomRow * 100;
                    double fallingX2 = randomRow2 * 100;
                    lanterns.add(new Projectile(fallingX, 0, 40.0, 60.0, 10, 2000, 2000, flameSprites));
                    lanterns.add(new Projectile(fallingX2, 0, 40.0, 60.0, 10, 2000, 2000, flameSprites));
                }
                if (lanternSpawnCooldown % 10 == 0) {
                    int randomRow3 = (int) (Math.random() * 10) + 5;
                    double fallingX3 = randomRow3 * 100;
                    lanterns.add(new Projectile(fallingX3, 0, 40.0, 64.0, 5, 2000, 2000, lanternSprites));
                }
            }

            // Lantern Fall Phase 2 ~20 seconds
            if (numTimesLooped >= 10240 && numTimesLooped < 11520) {
                // Increase cooldown
                lanternSpawnCooldown++;
                if (lanternSpawnCooldown % 1 == 0) {
                    int randomRow = (int) (Math.random() * 5);
                    int randomRow2 = (int) (Math.random() * 5) + 15;
                    double fallingX = randomRow * 100;
                    double fallingX2 = randomRow2 * 100;
                    lanterns.add(new Projectile(fallingX, 0, 40.0, 60.0, 10, 2000, 2000, flameSprites));
                    lanterns.add(new Projectile(fallingX2, 0, 40.0, 60.0, 10, 2000, 2000, flameSprites));
                }
                if (lanternSpawnCooldown % 7 == 0) {
                    int randomRow3 = (int) (Math.random() * 10) + 5;
                    double fallingX3 = randomRow3 * 100;
                    lanterns.add(new Projectile(fallingX3, 0, 40.0, 64.0, 5, 2000, 2000, lanternSprites));
                }
            }

            // Finale
            if (numTimesLooped >= 11520 && numTimesLooped < 12800) {
                flowerSpawnCooldown++;
                if (flowerSpawnCooldown % 1 == 0) {
                    double radius = 1250;
                    double x = 1000 + radius * Math.cos(spiralAngle);
                    double y = 1000 + radius * Math.sin(spiralAngle);
                    int speed = 5 + (int)(Math.random() * 5);
                    flowers.add(new Projectile(1000, 1000, x, y, 40.0, 40, speed, 2000, 2000, flameSprites));
                    spiralAngle += 0.5;
                }
            }
            if (numTimesLooped >= 13000) {
                player.takeDamage(1000);
            }
//            // Death
//            if (numTimesLooped >= 10240 && numTimesLooped < 11520) {
//                double radius = 1250;
//                double x = 1000 + radius * Math.cos(spiralAngle);
//                double y = 1000 + radius * Math.sin(spiralAngle);
//                double x2 = 1000 + radius * Math.cos(spiralAngle2);
//                double y2 = 1000 + radius * Math.sin(spiralAngle2);
//                double x3 = 1000 + radius * Math.cos(spiralAngle);
//                double y3 = 1000 + radius * Math.sin(spiralAngle);
//                double x4 = 1000 + radius * Math.cos(spiralAngle2);
//                double y4 = 1000 + radius * Math.sin(spiralAngle2);
//                int speed = 7 + (int)(Math.random() * 5);
//                flowers.add(new Projectile(x, y, 1000, 1000, 80.0, 80, speed, 2000, 2000, flowerSprites));
//                flowers.add(new Projectile(x2, y2, 1000, 1000, 80.0, 80, speed, 2000, 2000, flowerSprites));
//                flowers.add(new Projectile(1000, 1000, x3, y3, 80.0, 80, speed, 2000, 2000, flowerSprites));
//                flowers.add(new Projectile(1000, 1000, x4, y4, 80.0, 80, speed, 2000, 2000, flowerSprites));
//                spiralAngle += 0.5;
//                spiralAngle2 -= 0.5;
//            }

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

    public void initializeImages() {
        mapImage = new ImageIcon("Resources/map.png").getImage();
        titleImage = new ImageIcon("Resources/title2.png").getImage();
        menuImage = new ImageIcon("Resources/menu3.png").getImage();
        deathImage = new ImageIcon("Resources/death.png").getImage();
        startImage = new ImageIcon("Resources/start2.png").getImage();
        selectDifficultyImage = new ImageIcon("Resources/selectdifficulty2.png").getImage();
        easyImage = new ImageIcon("Resources/easy2.png").getImage();
        defaultImage = new ImageIcon("Resources/default2.png").getImage();
        hardImage = new ImageIcon("Resources/hard2.png").getImage();
        nightmareImage = new ImageIcon("Resources/nightmare.png").getImage();

        starSprites[0] = new ImageIcon("Resources/Projectiles/blue_star.png").getImage();
        starSprites[1] = new ImageIcon("Resources/Projectiles/green_star.png").getImage();
        starSprites[2] = new ImageIcon("Resources/Projectiles/red_star.png").getImage();
        starSprites[3] = new ImageIcon("Resources/Projectiles/yellow_star.png").getImage();
        flameSprites[0] = new ImageIcon("Resources/Projectiles/blue_flame.png").getImage();
        flameSprites[1] = new ImageIcon("Resources/Projectiles/BF2.png").getImage();
        flameSprites[2] = new ImageIcon("Resources/Projectiles/green_flame.png").getImage();
        flameSprites[3] = new ImageIcon("Resources/Projectiles/GF2.png").getImage();
        flameSprites[4] = new ImageIcon("Resources/Projectiles/red_flame.png").getImage();
        flameSprites[5] = new ImageIcon("Resources/Projectiles/RF2.png").getImage();
        flameSprites[6] = new ImageIcon("Resources/Projectiles/yellow_flame.png").getImage();
        flameSprites[7] = new ImageIcon("Resources/Projectiles/YF2.png").getImage();
        lanternSprites[0] = new ImageIcon("Resources/Projectiles/lantern1.png").getImage();
        lanternSprites[1] = new ImageIcon("Resources/Projectiles/lantern2.png").getImage();
        lanternSprites[2] = new ImageIcon("Resources/Projectiles/lantern3.png").getImage();
        lanternSprites[3] = new ImageIcon("Resources/Projectiles/lantern4.png").getImage();
        flowerSprites[0] = new ImageIcon("Resources/Projectiles/flower_1.png").getImage();
        flowerSprites[1] = new ImageIcon("Resources/Projectiles/flower_2.png").getImage();
        flowerSprites[2] = new ImageIcon("Resources/Projectiles/flower_3.png").getImage();
        flowerSprites[3] = new ImageIcon("Resources/Projectiles/flower_4.png").getImage();
        fireworkSprites[0] = new ImageIcon("Resources/Projectiles/fireball_1.png").getImage();
        fireworkSprites[1] = new ImageIcon("Resources/Projectiles/fireball_2.png").getImage();
        fireworkSprites[2] = new ImageIcon("Resources/Projectiles/fireball_3.png").getImage();
        fireworkSprites[3] = new ImageIcon("Resources/Projectiles/fireball_4.png").getImage();
        shardSprites[0] = new ImageIcon("Resources/Projectiles/shard_1.png").getImage();
        shardSprites[1] = new ImageIcon("Resources/Projectiles/shard_2.png").getImage();
        shardSprites[2] = new ImageIcon("Resources/Projectiles/shard_3.png").getImage();
        shardSprites[3] = new ImageIcon("Resources/Projectiles/shard_4.png").getImage();
        fireballSprites[0] = new ImageIcon("Resources/Projectiles/fireball_1.png").getImage();
        fireballSprites[1] = new ImageIcon("Resources/Projectiles/fireball_2.png").getImage();
        fireballSprites[2] = new ImageIcon("Resources/Projectiles/fireball_3.png").getImage();
        fireballSprites[3] = new ImageIcon("Resources/Projectiles/fireball_4.png").getImage();
        flowerShurikenSprites[0] = new ImageIcon("Resources/Projectiles/flowershuriken_1.png").getImage();
        flowerShurikenSprites[1] = new ImageIcon("Resources/Projectiles/flowershuriken_2.png").getImage();
        startIcon = new ImageIcon("Resources/start2.png");
        selectDifficultyIcon = new ImageIcon("Resources/selectdifficulty2.png");
        easyIcon = new ImageIcon("Resources/easy2.png");
        defaultIcon = new ImageIcon("Resources/default2.png");
        hardIcon = new ImageIcon("Resources/hard2.png");
        nightmareIcon = new ImageIcon("Resources/nightmare.png");
        crownImage = new ImageIcon("Resources/king.png").getImage();
        borderImage = new ImageIcon("Resources/border.png").getImage();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Follow player
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int offsetX = (int)(player.getWorldX() - centerX / ZOOM);
        int offsetY = (int)(player.getWorldY() - centerY / ZOOM);

        // Cast to Graphics2D for scaling
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (isInMenu || isInDifficultyMenu) {
            g2d.drawImage(menuImage, 0, 0, this);
            g2d.drawImage(borderImage, 0, 0, 1000, 777, this);
            g2d.drawImage(titleImage, 67, 120, 865, 162, this);
            return;
        }

        if (isGameOver) {
            survivalTime = (System.currentTimeMillis() - startTime) / 1000;
            g2d.drawImage(deathImage, 0, 0, getWidth(), getHeight(), null);

            if (numTimesLooped >= 13000) {
                g2d.setColor(Color.GREEN);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
                g2d.drawString("You won!", 450, 550);
                g2d.drawImage(crownImage, 225, -25, 550, 550, this);
            } else {
                g2d.setColor(Color.RED);
                g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
                g2d.drawString("You survived for " + survivalTime + " seconds!", 275, 550);
            }
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("DialogInput", Font.BOLD, 24));
            g2d.drawString("Press R", 445, 720);
            return;
        }

        // Save original transform
        var oldTransform = g2d.getTransform();

        // Apply zoom and translation
        g2d.scale(ZOOM, ZOOM);
        g2d.translate(-offsetX, -offsetY);

        if (isPlayingGame) {
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

            // Remove the flame once it reaches the center of the map
            Iterator<Projectile> flowerIterator = flowers.iterator();
            while (flowerIterator.hasNext()) { // Repeats until no more lanterns
                Projectile flower = flowerIterator.next();
                flower.draw(g2d);
                flower.update();

                // Removes lantern if in the middle
                int tolerance = 5;
                if (Math.abs(flower.getX() - 1000) < tolerance && Math.abs(flower.getY() - 1000) < tolerance) {
                    flowerIterator.remove();
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
                    int numShards = 16;
                    double shardSpeed = 5;
                    int shardLifetime = 100;

                    for (int i = 0; i < numShards; i++) {
                        double angle = Math.toRadians(i * (360.0 / numShards));
                        double dx = shardSpeed * Math.cos(angle);
                        double dy = shardSpeed * Math.sin(angle);
                        shards.add(new Projectile(firework.getX(), firework.getY(), dx, dy, 3, 2000, 2000, flowerSprites, shardLifetime));
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
        difficulty = "default";
        flames.clear();
        stars.clear();
        lanterns.clear();
        fireworks.clear();
        shards.clear();
        floatingLanterns.clear();
        flowers.clear();
        spiralAngle = 0;
        spiralAngle2 = 0;
        flameSpawnCooldown = 0;
        lanternSpawnCooldown = 0;
        fireworkSpawnCooldown = 0;
        floatingLanternSpawnCooldown = 0;
        flowerSpawnCooldown = 0;
        numTimesLooped = 0;
        isGameOver = false;
        isInMenu = true;
        setIsPlayingGame(false);
        returnToMenu();

        int perimeter = 8000;
        int spacing = perimeter / 80;

        for (int i = 0; i < 80; i++) { // Create 80 bordering projectiles
            int start = i * spacing;
            stars.add(new Projectile(start, 2, 2000, 2000, starSprites[i % 4]));
        }

        timer.stop();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysPressed.remove(e.getKeyCode());
    }
}
