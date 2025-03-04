package org.example.ui.lines;

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