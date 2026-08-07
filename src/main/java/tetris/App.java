package tetris;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import tetris.Board.RotationResult;

import java.util.*;

class App extends JPanel implements KeyListener {

    private JLabel gameOverLabel;
    private JButton restart;

    static final int COLUMNS = 10;
    static final int ROWS = 20;
    static final int TILE_SIZE = 30;
    static final int HEIGHT = ROWS * TILE_SIZE;
    static final int WIDTH = COLUMNS * TILE_SIZE;

    Board board = new Board();
    Piece current = new Piece(4, 0, 0,board.puzzleQueue[board.puzzleCount]);

    int fallSpeed = 600;

    boolean down = false;
    boolean gameOver = false;

    javax.swing.Timer gameTimer;

    public void startGame() {
        gameTimer = new javax.swing.Timer(fallSpeed, e -> {
            update();
            repaint();
        });
        gameTimer.start();
    }

    public void update() {

        if (!board.is_landed(current)) {

            board.clear_puzzle(current);
            current.y++;
        } else {
            if (gameOver()) {
                gameOver = true;
                gameOverLabel.setVisible(true);
                restart.setVisible(true);
                gameTimer.stop();
                return;
            } else {

                board.removeLine();
            }
            if (board.puzzleCount == 6) {
                board.puzzleCount = 0;
                board.puzzleQueue = Board.Random7bag();
            } else {
                board.puzzleCount++;
            }
            current = new Piece(4, 0, 0,board.puzzleQueue[board.puzzleCount]);
        }


        board.clear_puzzle(current);
        board.spawn_puzzle(current);
    }

    private void moveLeft() {
        if (gameOver) return;
        if (board.canMoveLeft(current)) {
            board.clear_puzzle(current);
            current.x--;
            board.spawn_puzzle(current);
            repaint();
        }
    }

    private void moveRight() {
        if (gameOver) return;
        if (board.canMoveRight(current)) {
            board.clear_puzzle(current);
            current.x++;
            board.spawn_puzzle(current);
            repaint();
        }
    }

    private void rotate() {
        if (gameOver) return;
        RotationResult result = board.canRotate(current);
        if (result.success()) {
            board.clear_puzzle(current);
            current.cells = result.new_shape();
            current.x = result.x();
            current.y = result.y();
            current.rotateState++;
            board.spawn_puzzle(current);
            repaint();
        }
    }

    App() {
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        requestFocusInWindow();

        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.magenta);
        int labelWidth = 300, labelHeight = 60;
        int x = (COLUMNS * TILE_SIZE - labelWidth) / 2;
        int y = (HEIGHT - labelHeight) / 2;
        gameOverLabel.setBounds(x, y, labelWidth, labelHeight);
        gameOverLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gameOverLabel.setVisible(false);  // na razie niewidoczny
        add(gameOverLabel);

        restart = new JButton("RESTART");

        restart.addActionListener(e -> {
            board.clear_grid();
            board.puzzleCount = 0;
            board.puzzleQueue = Board.Random7bag();
            current = new Piece(4, 0, 0, board.puzzleQueue[board.puzzleCount]);
            gameOver = false;
            gameOverLabel.setVisible(false);
            restart.setVisible(false);
            requestFocusInWindow();
            gameTimer.start();
            repaint();
        });

        restart.setFont(new Font("Arial", Font.BOLD, 30));
        restart.setBounds(x, 2 * y, labelWidth, labelHeight);
        restart.setBackground(Color.magenta);
        restart.setForeground(Color.WHITE);
        restart.setVisible(false);
        add(restart);

        startGame();
    }

    public boolean gameOver() {
        for (int i = 0; i < Puzzle.shape[board.puzzleQueue[board.puzzleCount]].length; i++) {
            for (int j = 0; j < Puzzle.shape[board.puzzleQueue[board.puzzleCount]][0].length; j++) {
                if (board.grid[0 + i][4 + j] == 1) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //grid drawing
        if (!gameOver) {
            for (int i = 0; i < HEIGHT; i += TILE_SIZE) {
                for (int j = 0; j < WIDTH; j += TILE_SIZE) {
                    g.drawRect(j, i, TILE_SIZE, TILE_SIZE);
                }
            }
        }


        //tetermino drawing
        if (!gameOver) {
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    if (board.grid[i][j] == 1) {
                        g.setColor(board.tile_color[i][j]);
                        g.fillRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }


    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_RIGHT) {
            moveRight();
        }
        if (keyCode == KeyEvent.VK_LEFT) {
            moveLeft();
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            fallSpeed = 100;
            if (gameTimer != null) {
                gameTimer.setDelay(fallSpeed);
            }
        }
        if (keyCode == KeyEvent.VK_UP) {
            rotate();
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_DOWN) {
            fallSpeed = 600;
            if (gameTimer != null) {
                gameTimer.setDelay(fallSpeed);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Tetris");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);

            App panel = new App();
            window.add(panel);
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
            panel.requestFocusInWindow();
        });
    }
}
