import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Runner extends JPanel {
	public static void main(String args[])
	{
		JFrame f = new JFrame();
		f.setVisible(false);
		f.setSize(700,700);
		f.add(new Runner());
		f.
		f.setVisible(true);
	}
	public void
}

class listener implements MouseListener,MouseMotionListener
{

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

}

class bodyPart
{
	Point start, end;
	int length;
	boolean sMove;//if true then the start position is able to move
	boolean eMove;

	public bodyPart(Point start,Point end)
	{
		this.start=start;
		this.end= end;
	}

	public void translateStartPoint(double x,double y)
	{
		if(sMove)
		{
			start.translate((int)x,(int) y);
		}
	}

	public void translateEndPoint(double x,double y)
	{
		if(eMove)
		{
			end.translate((int)x,(int) y);
		}
	}

	public void moveStartPoint(double x,double y)
	{
		if(sMove)
		{
			start.move((int)x,(int) y);
		}
	}

	public void moveEndPoint(double x,double y)
	{
		if(eMove)
		{
			end.move((int)x,(int) y);
		}
	}
	public void draw(Graphics g)
	{
		g.drawLine(start.x,start.y, end.x, end.y);
	}
}