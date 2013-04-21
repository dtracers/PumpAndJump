package net.bluecow.spectro.detection.tempo;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DistancePaint extends JPanel
{
	public static void main(String args[]) throws InterruptedException
	{
		String file = "The Hand That Feeds - Nine Inch Nails.mp3";
		RegressionDetection detector = new RegressionDetection(null);
		detector.createBeatsFromAFile(file);
		DistancePaint p = new DistancePaint();
		p.setBackground(Color.white);
		detector.painter =p;
		JFrame f = createJFrame(p,file);
		for(int k=0;k<detector.detectedBeats.size();k++)
		{
			detector.detectTempo(k);
			Thread.sleep(100);
			f.repaint();
		}
		f.repaint();
	}

	public static JFrame createJFrame(DistancePaint p,String name)
	{
		JFrame frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(false);
		frame.setSize(900,900);
		frame.add(p);
		frame.setVisible(true);
		return frame;
	}


	int width = 600,height = 600;
	int offX = 10;
	int offY = 10;
	double[] line;
	ArrayList<Distance> distances;

	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		if(distances!=null)
		{
			int length = distances.size();
			g.setColor(Color.green);
			for(int k =0;k<distances.size();k++)
			{
				Distance d = distances.get(k);
				double Xlocation = k*width/length;
				double Ylocation = height-d.distance;

				g.drawRect((int)Xlocation, (int)Ylocation, 1, 1);
				System.out.println("Drawing "+Xlocation+" "+Ylocation);
			}

			if(line!= null)
			{
				double a = line[0];
				double b = line[1];
				double R2 = line[2];
				if(R2>.998)
				{
					g.setColor(Color.blue);
					g.drawLine((int)offX,(int) (height-(a*0+b+offY)), (int)(width+offX),(int) (height-(a*distances.size()+b+offY)));
				}
					g.drawString("Y = "+a+"*x"+b, 0, 20);
					g.drawString("R = "+R2, 0, 30);
			}
		}else
		{
			System.out.println("NULL!");
		}
	}
}
