package org.example.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class LogicGate {
    int x, y;
    String type;

    public LogicGate(String type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
    }
}

class DrawingPanel extends JPanel {
    private final ArrayList<LogicGate> gates = new ArrayList<>();
    private LogicGate selectedGate = null;
    private int offsetX, offsetY;

    public DrawingPanel() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                for (LogicGate gate : gates) {
                    if (new Rectangle(gate.x, gate.y, 50, 50).contains(e.getPoint())) {
                        selectedGate = gate;
                        offsetX = e.getX() - gate.x;
                        offsetY = e.getY() - gate.y;
                        return;
                    }
                }
                selectedGate = null;
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

    public void addGate(String type) {
        gates.add(new LogicGate(type, 50, 50));
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (LogicGate gate : gates) {
            g.setColor(Color.BLACK);
            g.drawRect(gate.x, gate.y, 50, 50);
            g.drawString(gate.type, gate.x + 15, gate.y + 30);
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
        for (String gate : gates) {
            JButton button = new JButton(gate);
            button.addActionListener(e -> drawingPanel.addGate(gate));
            toolbar.add(button);
        }
        
        frame.add(toolbar, BorderLayout.NORTH);
        frame.add(drawingPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
