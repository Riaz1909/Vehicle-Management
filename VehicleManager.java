import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class VehicleManager extends JFrame implements ActionListener {

    private JTextField typeField, modelField, numberField;
    private JTextArea displayArea;
    private JButton submitButton;
    private JLabel vehicleImageLabel;
    private Image backgroundImage;

    public VehicleManager() {
        setTitle("Vehicle Management System");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        backgroundImage = new ImageIcon("images/background.jpg").getImage();

        // Custom JPanel with semi-transparent background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        Color labelColor = new Color(20, 20, 20); // dark black
        Color inputColor = Color.RED;

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createTitledBorder("Vehicle Details"));

        JLabel typeLabel = new JLabel("Types Of Vehicle:");
        typeLabel.setForeground(labelColor);
        formPanel.add(typeLabel);
        typeField = new JTextField();
        typeField.setForeground(inputColor);
        formPanel.add(typeField);

        JLabel modelLabel = new JLabel("Model Name:");
        modelLabel.setForeground(labelColor);
        formPanel.add(modelLabel);
        modelField = new JTextField();
        modelField.setForeground(inputColor);
        formPanel.add(modelField);

        JLabel numberLabel = new JLabel("Vehicle Number:");
        numberLabel.setForeground(labelColor);
        formPanel.add(numberLabel);
        numberField = new JTextField();
        numberField.setForeground(inputColor);
        formPanel.add(numberField);

        submitButton = new JButton("Verify");
        submitButton.addActionListener(this);
        formPanel.add(submitButton);
        formPanel.add(new JLabel()); // for alignment

        // Display area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setOpaque(false);
        displayArea.setForeground(labelColor);
        displayArea.setBorder(BorderFactory.createTitledBorder("Vehicle List"));

        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Vehicle image preview
        vehicleImageLabel = new JLabel();
        vehicleImageLabel.setHorizontalAlignment(JLabel.CENTER);
        vehicleImageLabel.setBorder(BorderFactory.createTitledBorder("Vehicle Preview"));
        vehicleImageLabel.setPreferredSize(new Dimension(300, 220));
        vehicleImageLabel.setOpaque(false);

        // Bottom panel layout
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(vehicleImageLabel, BorderLayout.EAST);

        backgroundPanel.add(formPanel, BorderLayout.NORTH);
        backgroundPanel.add(bottomPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String type = typeField.getText().trim().toLowerCase();
        String model = modelField.getText().trim().toLowerCase();
        String number = numberField.getText().trim();

        if (type.isEmpty() || model.isEmpty() || number.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your fields are empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String info = "Type: " + type + ", Model: " + model + ", Number: " + number + "\n";
        displayArea.append(info);

        // Attempt to load model-specific image first
        String imagePath = "images/" + type + "_" + model + ".png";
        File imgFile = new File(imagePath);

        // Fallbacks
        if (!imgFile.exists()) {
            imagePath = "images/" + type + "_" + model + ".jpg";
            imgFile = new File(imagePath);
        }
        if (!imgFile.exists()) {
            imagePath = "images/" + type + ".png";
            imgFile = new File(imagePath);
        }
        if (!imgFile.exists()) {
            imagePath = "images/" + type + ".jpg";
            imgFile = new File(imagePath);
        }

        System.out.println("Trying to load: " + imgFile.getAbsolutePath());

        if (imgFile.exists()) {
            try {
                ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                Image scaledImage = icon.getImage().getScaledInstance(280, 200, Image.SCALE_SMOOTH);
                vehicleImageLabel.setIcon(new ImageIcon(scaledImage));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage());
            }
        } else {
            vehicleImageLabel.setIcon(null);
            JOptionPane.showMessageDialog(this, "Image not found for model/type at: " + imgFile.getAbsolutePath(), "Image Error", JOptionPane.WARNING_MESSAGE);
        }

        // Clear fields
        typeField.setText("");
        modelField.setText("");
        numberField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VehicleManager::new);
    }
}
