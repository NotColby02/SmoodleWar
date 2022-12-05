package smoodleWar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class DrawingField extends JPanel implements MouseListener, MouseMotionListener {

	public DrawingField() {
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(getPreferredSize());
	}
	
	@Override
	public Dimension getPreferredSize(){
		Dimension size = super.getPreferredSize();
		size.width = 500;
		size.height = 500;
		return size;
		
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		//g.drawString("Blah blah!", 20, 20);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		Graphics g = this.getGraphics();
		g.setColor(Color.black);
		
		// get X and y position
		int x, y;
		x = e.getX();
		y = e.getY();
		
		// draw a Oval at the point
		// where mouse is moved
		g.fillOval(x, y, 5, 5);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Graphics g = this.getGraphics();
		g.setColor(Color.black);
		
		// get X and y position
		int x, y;
		x = e.getX();
		y = e.getY();
		
		// draw a Oval at the point
		// where mouse is moved
		g.fillOval(x, y, 5, 5);

	}

	// NOT NEEDED
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
