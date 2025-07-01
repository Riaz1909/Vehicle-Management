import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class VehicleApp extends Frame implements ActionListener {

    private Button checkCarsBtn, exitBtn;
    private Button btnMinimize, btnMaximize, btnClose;
    private BufferedImage backgroundImage, carImage;
    private boolean isMaximized = true;

    private Panel titleBar;
    private Label titleLabel;
    private Point initialClick;

    private int carX;
    private int carY;
    private Timer animationTimer;

    private Clip engineClip;

    private boolean isMoving = false;

    public VehicleApp() {
        setTitle("Vehicle Application");
        setUndecorated(true);
        setLayout(null);
        setBackground(Color.WHITE);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setLocation(0, 0);

        loadImages(screenSize.width, screenSize.height);
        carY = screenSize.height - 300;

        // Title Bar
        titleBar = new Panel(null);
        titleBar.setBackground(new Color(30, 30, 30));
        titleBar.setBounds(0, 0, screenSize.width, 40);
        add(titleBar);

        titleLabel = new Label("  Vehicle Application", Label.LEFT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBounds(10, 8, 300, 25);
        titleBar.add(titleLabel);

        btnMinimize = new Button("_");
        btnMinimize.setBounds(screenSize.width - 150, 8, 40, 24);
        btnMinimize.addActionListener(e -> setState(Frame.ICONIFIED));
        titleBar.add(btnMinimize);

        btnMaximize = new Button("⬜");
        btnMaximize.setBounds(screenSize.width - 100, 8, 40, 24);
        btnMaximize.addActionListener(e -> toggleMaximize());
        titleBar.add(btnMaximize);

        btnClose = new Button("X");
        btnClose.setBounds(screenSize.width - 50, 8, 40, 24);
        btnClose.setBackground(Color.RED);
        btnClose.setForeground(Color.WHITE);
        btnClose.addActionListener(e -> System.exit(0));
        titleBar.add(btnClose);

        titleBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (!isMaximized) {
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;
                    setLocation(getX() + xMoved, getY() + yMoved);
                }
            }
        });

        // Buttons
        checkCarsBtn = new Button("Check Cars");
        checkCarsBtn.setBounds(screenSize.width / 2 - 120, screenSize.height - 150, 100, 40);
        checkCarsBtn.addActionListener(this);
        checkCarsBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isMoving = true;
                playEngineSound();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isMoving = false;
                stopSound();
            }
        });
        add(checkCarsBtn);

        exitBtn = new Button("Exit");
        exitBtn.setBounds(screenSize.width / 2 + 30, screenSize.height - 150, 80, 40);
        exitBtn.addActionListener(this);
        add(exitBtn);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                stopSound();
            }
        });

        carX = getWidth();

        animationTimer = new Timer(16, e -> {
            if (isMoving) {
                carX -= 5;
                if (carX < -200) {
                    carX = getWidth();
                }
                repaint();
            }
        });
        animationTimer.start();

        setVisible(true);
    }

    private void loadImages(int width, int height) {
        try {
            BufferedImage bg = ImageIO.read(new File("Background.jpg"));
            backgroundImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = backgroundImage.createGraphics();
            g2d.drawImage(bg, 0, 0, width, height, null);
            g2d.dispose();
        } catch (Exception e) {
            System.out.println("Background image not found.");
        }

        try {
            carImage = ImageIO.read(new File("car.png"));
        } catch (Exception e) {
            System.out.println("Car image not found.");
        }
    }

    private void playEngineSound() {
        try {
            if (engineClip == null || !engineClip.isOpen()) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("engine.wav"));
                engineClip = AudioSystem.getClip();
                engineClip.open(audioStream);
                engineClip.loop(Clip.LOOP_CONTINUOUSLY);
            } else if (!engineClip.isRunning()) {
                engineClip.setFramePosition(0);
                engineClip.start();
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }

    private void stopSound() {
        if (engineClip != null && engineClip.isRunning()) {
            engineClip.stop();
            engineClip.close();
            engineClip = null;
        }
    }

    private void toggleMaximize() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle maxBounds = ge.getMaximumWindowBounds();

        if (isMaximized) {
            setSize(1000, 700);
            setLocationRelativeTo(null);
            isMaximized = false;
        } else {
            setBounds(maxBounds);
            isMaximized = true;
        }

        loadImages(getWidth(), getHeight());
        repositionUI();
        repaint();
    }

    private void repositionUI() {
        int width = getWidth();
        int height = getHeight();

        titleBar.setBounds(0, 0, width, 40);
        btnMinimize.setBounds(width - 150, 8, 40, 24);
        btnMaximize.setBounds(width - 100, 8, 40, 24);
        btnClose.setBounds(width - 50, 8, 40, 24);

        checkCarsBtn.setBounds(width / 2 - 120, height - 150, 100, 40);
        exitBtn.setBounds(width / 2 + 30, height - 150, 80, 40);

        carY = height - 300;
    }

    public void update(Graphics g) {
        paint(g);
    }

    public void paint(Graphics g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this);
        }

        if (carImage != null) {
            g.drawImage(carImage, carX, carY, carImage.getWidth(), carImage.getHeight(), this);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkCarsBtn) {
            EventQueue.invokeLater(() -> new VehicleManager());
            dispose();
        } else if (e.getSource() == exitBtn) {
            stopSound();
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new VehicleApp();
    }
}
