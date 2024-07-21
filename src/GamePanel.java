import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 700;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * (SCREEN_HEIGHT - 100)) / UNIT_SIZE;
    static final int DELAY = 120;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    static final int SCORE_AREA_HEIGHT = 25;
    int currentDelay = DELAY;
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    int lives = 3;
    char direction = 'R';
    boolean running;
    Timer timer;
    Random random;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = SCORE_AREA_HEIGHT + i * UNIT_SIZE;
        }
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void resetGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        lives = 3;
        currentDelay = DELAY;
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = SCORE_AREA_HEIGHT + i * UNIT_SIZE;
        }
        newApple();
        running = true;
        timer.setDelay(currentDelay);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.drawLine(0, UNIT_SIZE, SCREEN_WIDTH, UNIT_SIZE);

            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)), g.getFont().getSize());

            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            //FontMetrics metrics = getFontMetrics(g.getFont());
            int m = (SCREEN_WIDTH - metrics.stringWidth(""));
            g.drawString("Lives: " + lives, 0, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt(1, (int)((SCREEN_HEIGHT - SCORE_AREA_HEIGHT)/ UNIT_SIZE)) * SCORE_AREA_HEIGHT;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                if (y[0] < SCORE_AREA_HEIGHT) {
                    y[0] = SCORE_AREA_HEIGHT;
                }
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                if (y[0] >= SCREEN_HEIGHT) {
                    y[0] = SCREEN_HEIGHT - UNIT_SIZE;
                }
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                if (x[0] < 0) {
                    x[0] = 0;
                }
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                if (x[0] >= SCREEN_WIDTH) {
                    x[0] = SCREEN_WIDTH - UNIT_SIZE;
                }
                break;
        }

        // Area for the score
        if (y[0] < SCORE_AREA_HEIGHT) {
            y[0] = SCORE_AREA_HEIGHT;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            if (applesEaten % 5 == 0) {
                currentDelay = Math.max(10, currentDelay - 5);
                updateTimer();
            }
            newApple();
        }
    }

    private void updateTimer() {
        timer.setDelay(currentDelay);
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                lives--;
                resetSnake();
                if (lives == 0) {
                    running = false;
                    return;
                }
            }
        }

        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < SCORE_AREA_HEIGHT || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        if (!running) {
            timer.stop();
        }
    }

    public void resetSnake() {
        applesEaten = 0;
        bodyParts = 6;
        currentDelay = 120;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = SCORE_AREA_HEIGHT + i * UNIT_SIZE;
        }
        newApple();
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics gameOverMetrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - gameOverMetrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT / 2);

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics scoreMetrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - scoreMetrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics resetMetrics = getFontMetrics(g.getFont());
        g.drawString("Press Enter to Restart the Game", (SCREEN_WIDTH - resetMetrics.stringWidth("Press Enter to Restart the Game"))/2, (SCREEN_HEIGHT - 300));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_ENTER:
                    if (!running) {
                        resetGame();
                    }
                    break;
            }
        }
    }
}
