package org.example.ui.images;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.image.BufferedImage;

class LogicGate {
    int x, y;
    String type;
    BufferedImage image;

    public LogicGate(String type, int x, int y, BufferedImage image) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.image = image;
    }
}

class Connection {
    LogicGate start, end;

    public Connection(LogicGate start, LogicGate end) {
        this.start = start;
        this.end = end;
    }
}

class DrawingPanel extends JPanel {
    private final ArrayList<LogicGate> gates = new ArrayList<>();
    private final ArrayList<Connection> connections = new ArrayList<>();
    private LogicGate selectedGate = null;
    private LogicGate firstSelectedGate = null;
    private int offsetX, offsetY;

    public DrawingPanel() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (LogicGate gate : gates) {
                    if (new Rectangle(gate.x, gate.y, 50, 50).contains(e.getPoint())) {
                        if (firstSelectedGate == null) {
                            firstSelectedGate = gate;
                        } else {
                            connections.add(new Connection(firstSelectedGate, gate));
                            firstSelectedGate = null;
                            repaint();
                        }
                        selectedGate = gate;
                        offsetX = e.getX() - gate.x;
                        offsetY = e.getY() - gate.y;
                        return;
                    }
                }
                selectedGate = null;
                firstSelectedGate = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (selectedGate != null) {
                    selectedGate.x = e.getX() - offsetX;
                    selectedGate.y = e.getY() - offsetY;
                    repaint();
                }
            }
        });
    }

    public void addGate(String type, BufferedImage image) {
        gates.add(new LogicGate(type, 50, 50, image));
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (LogicGate gate : gates) {
            if (gate.image != null) {
                g.drawImage(gate.image, gate.x, gate.y, 50, 50, this);
            } else {
                g.setColor(Color.BLACK);
                g.drawRect(gate.x, gate.y, 50, 50);
                g.drawString(gate.type, gate.x + 15, gate.y + 30);
            }
        }

        g.setColor(Color.RED);
        for (Connection conn : connections) {
            g.drawLine(conn.start.x + 25, conn.start.y + 25, conn.end.x + 25, conn.end.y + 25);
        }
    }
}

public class LogicGateSimulator {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Logic Gate Simulator");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JToolBar toolbar = new JToolBar();
        DrawingPanel drawingPanel = new DrawingPanel();

        String[] gates = {"AND", "OR", "NOR", "XOR"};
        File file = new File("/home/sabareesan/apps/fiverr/Digital/circuitdesigner/src/main/java/org/example/ui/images/and.png");
        URI uri = file.toURI(); // Converts it to file:// format
        File file2 = new File("/home/sabareesan/apps/fiverr/Digital/circuitdesigner/src/main/java/org/example/ui/images/or.png");
        URI uri2 = file2.toURI(); // Converts it to file:// format
        URI[] uris = new URI[4];
        uris[0] = uri;
        uris[1]= uri2;
        uris[2] = uri;
        uris[3]= uri2;
//        File file = new File("/home/sabareesan/apps/fiverr/Digital/circuitdesigner/src/main/java/org/example/ui/images/and.png");
//        URI uri = file.toURI(); // Converts it to file:// format
//        File file = new File("/home/sabareesan/apps/fiverr/Digital/circuitdesigner/src/main/java/org/example/ui/images/and.png");
//        URI uri = file.toURI(); // Converts it to file:// format

        String[] imagePaths = {"file://home/sabareesan/apps/fiverr/Digital/circuitdesigner/src/main/java/org/example/ui/images/and.png",
                "file://home/sabareesan/apps/fiverr/Digital/circuitdesigner/src/main/java/org/example/ui/images/and.png",
                "file://home/sabareesan/apps/fiverr/Digital/circuitdesigner/src/main/java/org/example/ui/images/and.png",
                "file://home/sabareesan/apps/fiverr/Digital/circuitdesigner/src/main/java/org/example/ui/images/and.png"}; // Place your image files in the project directory
        try {
            for (int i = 0; i < gates.length; i++) {
                BufferedImage image = ImageIO.read(uris[i].toURL());//LogicGateSimulator.class.getResource(imagePaths[i])
                ImageIcon icon = new ImageIcon(image.getScaledInstance(30, 30, Image.SCALE_SMOOTH)); // Scale image if needed
                JButton button = new JButton(icon);
                String gateType = gates[i];
                button.addActionListener(e -> drawingPanel.addGate(gateType, image));
                toolbar.add(button);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading images. Make sure the image files are in the correct location.");
        }

        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(drawingPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}