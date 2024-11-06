// JFrame in Java is a class that allows you to create and manage a top-lеvеl window in a Java application. It sеrvеs as thе main window for GUI-basеd Java applications and providеs a platform-indеpеndеnt way to crеatе graphical usеr intеrfacеs. In Java JFrame is a part of javax.swing package.

package main;

import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;

public class GameWindow {

	private JFrame jframe;

	public GameWindow(GamePanel gamePanel) { // Constructor

		jframe = new JFrame();

		// jframe.setSize(400, 400); // window size
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close the program whenever we close the window
		jframe.add(gamePanel); // add JPanel to JFrame
		jframe.setResizable(false); // not allowing to resize the window.
		jframe.pack(); // fit the size of the window to the preferred size of its components. We have
						// only one component - JPanel, so its going to create a window that fits
						// JPanel.
		jframe.setLocationRelativeTo(null); // spawn window in the center of a screen
		jframe.setVisible(true); // make window visible. must be at the bottom to avoid a bag with blank window.

		jframe.addWindowFocusListener(new WindowFocusListener() { // detect if we lost the focus. if we did, all booleans must stop
			
			@Override
			public void windowLostFocus(WindowEvent e) {
				
				gamePanel.getGame().windowFocusLost();
				
			}
			
			@Override
			public void windowGainedFocus(WindowEvent e) {
				
				
			}
		});
	}

}
