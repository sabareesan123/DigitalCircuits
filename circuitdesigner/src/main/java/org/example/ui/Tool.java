package org.example.ui;// Java Program to create a simple toolbar and add buttons and combobox to it.

import javax.swing.*;
import java.awt.*;

public class Tool extends JFrame {
	// toolbar
	static JToolBar tb;

	// buttons
	static JButton b1, b2;

	// create a frame
	static JFrame f;

	// create a combo box

	static JComboBox x;

	public static void main(String[] args)
	{

		// create a frame
		f = new JFrame("Toolbar demo");

		// set layout for frame
		f.setLayout(new BorderLayout());

		// create a toolbar
		tb = new JToolBar();

		// create a panel
		JPanel p = new JPanel();

		// create a combobox
		x = new JComboBox(new String[] { "item 1", "item 2", "item 3" });

		// create new buttons
		b1 = new JButton("button 1");
		b2 = new JButton("button 2");

		// add buttons
		p.add(b1);
		p.add(b2);

		// add menu to menu bar
		p.add(x);

		tb.add(p);

		// add toolbar to frame
		f.add(tb, BorderLayout.NORTH);

		// set the size of the frame
		f.setSize(500, 500);
		f.setVisible(true);
	}
}
