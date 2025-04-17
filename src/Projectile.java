import java.awt.*;

public class Projectile {
    private double distance;
    private int speed;
    private int mapWidth, mapHeight;
    private Image sprite;
    private double x, y;
    private double dx, dy;
    private int width, height;
    private Image[] flameSprite;
    private Image[] lanternSprite;
    private Image[] fireworkSprite;
    private Image[] shardSprite;
    private Image[] flowerSprite;
    private double targetX, targetY;
    private int frameFlame = 0;
    private int frameFlameCounter = 0;
    private int frameLantern = 0;
    private int frameLanternCounter = 0;
    private int frameFirework = 0;
    private int frameFireworkCounter = 0;
    private int frameShard = 0;
    private int frameShardCounter = 0;
    private int frameFlower = 0;
    private int frameFlowerCounter = 0;
    private int bounceCount = 0;
    private boolean isRotatingFlame = false;
    private boolean isFallingLantern = false;
    private boolean isReboundingFirework = false;
    private boolean isExpandingShard = false;
    private boolean isFloatingFlower = false;
    public boolean hasHitPlayer = false;
    private boolean explode = false;
    private int shardAge = 0;
    private int shardLifetime = 30;


    public boolean isHasHitPlayer() {
        return hasHitPlayer;
    }

    public void setHasHitPlayer(boolean hasHitPlayer) {
        this.hasHitPlayer = hasHitPlayer;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getBounceCount() {
        return bounceCount;
    }

    // For stars
    public Projectile(int startDistance, int speed, int mapWidth, int mapHeight, Image sprite) {
        this.distance = startDistance;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.speed = speed;
        this.sprite = sprite;
    }

    // For rotating inward flames
    public Projectile(double x, double y, int width, int height, int speed, int mapWidth, int mapHeight, Image[] flameSprite) {
        this.isRotatingFlame = true;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.flameSprite = flameSprite;
        this.targetX = mapWidth / 2;
        this.targetY = mapHeight / 2;
    }

    // Falling lanterns
    public Projectile(double x, double y, double width, double height, int speed, int mapWidth, int mapHeight, Image[] lanternSprite) {
        this.isFallingLantern = true;
        this.x = x;
        this.y = y;
        this.width = (int)width;
        this.height = (int)height;
        this.speed = speed;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.lanternSprite = lanternSprite;
        this.targetY = mapHeight;
    }

    // Rebounding firework
    public Projectile(double x, double y, int width, int height, double speed, int mapWidth, int mapHeight, Image[] fireworkSprite) {
        this.isReboundingFirework = true;
        this.bounceCount = 0;
        this.explode = false;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = (int)speed;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.fireworkSprite = fireworkSprite;
        double angle = Math.random() * 360;
        this.dx = Math.cos(Math.toRadians(angle)) * speed; // Gives horizontal x direction
        this.dy = Math.sin(Math.toRadians(angle)) * speed; // Gives vertical y direction
    }

    public Projectile()

    public boolean isExpiredShard() {
        return shardAge > shardLifetime;
    }

    // For exploding fireworks into shards
    public Projectile(double x, double y, double dx, double dy, int speed, int mapWidth, int mapHeight, Image[] shardSprites, int shardLifetime) {
        this.isExpandingShard = true;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.speed = speed;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.shardSprite = shardSprites;
        this.width = 10;
        this.height = 10;
        this.shardLifetime = shardLifetime;
    }

    public void draw(Graphics g) {
        if (isRotatingFlame) { // Draw flame
            Image currentSprite = flameSprite[frameFlame];
            g.drawImage(currentSprite, (int)x - width / 2, (int)y - width / 2, null);
        } else if (isFallingLantern) {
            Image currentSprite = lanternSprite[frameLantern];
            g.drawImage(currentSprite, (int)x, (int)y - height / 2, null);
        } else if (isReboundingFirework) {
            Image currentSprite = fireworkSprite[frameFirework];
            g.drawImage(currentSprite, (int)x - width / 2, (int)y - height / 2, null);
        } else if (isExpandingShard) {
            Image currentSprite = shardSprite[frameShard];
            g.drawImage(currentSprite, (int)x - width / 2, (int)y - height / 2, null);
        } else if (is)
        else { // Draw border of stars
                int drawX = 0;
                int drawY = 0;
                double d = distance % (mapWidth * 2 + mapHeight * 2);

                if (d < mapWidth) {
                    drawX = (int) d;
                    drawY = 0;
                } else if (d < mapWidth + mapHeight) {
                    drawX = mapWidth;
                    drawY = (int) (d - mapWidth);
                } else if (d < mapWidth * 2 + mapHeight) {
                    drawX = (int) (mapWidth * 2 + mapHeight - d);
                    drawY = mapHeight;
                } else {
                    drawX = 0;
                    drawY = (int) (mapWidth * 2 + mapHeight * 2 - d);
                }

                g.drawImage(sprite, drawX - sprite.getWidth(null) / 2, drawY - sprite.getHeight(null) / 2, null);
        }
    }

    public void update() {
        if(isRotatingFlame) {
            // Move slowly to center
            double dx = targetX - x;
            double dy = targetY - y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > 1) {
                x += dx/dist * speed * 0.1;
                y += dy/dist * speed * 0.1;
            }

            frameFlameCounter++;
            if (frameFlameCounter % 10 == 0) {
                frameFlame = (frameFlame + 1) % flameSprite.length;
            }
        } else if (isFallingLantern) {
            y += speed;
            frameLanternCounter++;
            if (frameLanternCounter % 10 == 0) {
                frameLantern = (frameLantern + 1) % lanternSprite.length;
            }
        } else if (isReboundingFirework) {
            x += dx;
            y += dy;
            if (x <= 0 || x >= mapWidth) {
                dx = -dx;
                bounceCount++;
            }
            if (y <= 0 || y > mapHeight) {
                dy = -dy;
                bounceCount++;
            }

            if (bounceCount >= 3) {
                explode = true;
            }
            frameFireworkCounter++;
            if (frameFireworkCounter % 10 == 0) {
                frameFirework = (frameFirework + 1) % fireworkSprite.length;
            }
        } else if (isExpandingShard) {
            x += dx;
            y += dy;
            shardAge++;
            frameShardCounter++;
            if (frameShardCounter % 10 == 0) {
                frameShard = (frameShard + 1) % shardSprite.length;
            }
        } else {
            distance += speed;
            double perimeter = 2 * mapWidth + 2 * mapHeight;
            if (distance >= perimeter) {
                distance -= perimeter;
            }
        }
    }

    public boolean shouldExplode() {
        return explode;
    }

    public Rectangle getHitbox() {
        if (isRotatingFlame) {
            return new Rectangle((int)x - width / 2, (int)y - height / 2, width, height);
        } else if (isFallingLantern) {
            return new Rectangle((int)x - width / 2, (int)y - height / 2, (int)width, (int)height);
        } else if (isReboundingFirework) {
            return new Rectangle((int)x - width / 2, (int)y - height / 2, width, height);
        } else if (isExpandingShard) {
            return new Rectangle((int)x - width / 2, (int)y - height / 2, width, height);
        } else {
            int x = 0;
            int y = 0;
            double d = distance % (mapWidth * 2 + mapHeight * 2);

            if (d < mapWidth) {
                x = (int) d;
                y = 0;
            } else if (d < mapWidth + mapHeight) {
                x = mapWidth;
                y = (int) (d - mapWidth);
            } else if (d < mapWidth * 2 + mapHeight) {
                x = (int) (mapWidth * 2 + mapHeight - d);
                y = mapHeight;
            } else {
                x = 0;
                y = (int) (mapWidth * 2 + mapHeight * 2 - d);
            }

            int spriteWidth = sprite.getWidth(null);
            int spriteHeight = sprite.getHeight(null);

            // Center hitbox on the projectile's position
            return new Rectangle(x - spriteWidth / 2, y - spriteHeight / 2, spriteWidth, spriteHeight);
        }
    }
}
