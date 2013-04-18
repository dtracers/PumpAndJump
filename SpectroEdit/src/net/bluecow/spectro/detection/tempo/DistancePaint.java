package net.bluecow.spectro.detection.tempo;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DistancePaint extends JPanel
{
	public static void main(String args[])
	{
		TempoDetector detector = new RegressionDetection(null);
		detector.createBeatsFromAFile("The Hand That Feeds - Nine Inch Nails.txt");
		DistancePaint p = new DistancePaint();
		createJFrame(p);
		for(int k=0;k<detector.detectedBeats.size();k++)
		{

		}
	}

	public static void createJFrame(DistancePaint p)
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(false);
		frame.setSize(900,900);
		frame.add(p);
	}


	int width = 600,height = 600;
	int offX = 10;
	int offY = 10;
	double[] line;
	ArrayList<Distance> distances;
	public void PaintComponent(Graphics g)
	{
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
			}

			if(line!= null)
			{
				double a = line[0];
				double b = line[1];
				double R2 = line[2];
				g.setColor(Color.blue);
				g.drawLine((int)offX,(int) (height-(a*0+b+offY)), (int)(width+offX),(int) (height-(a*offY+b+offY)));
			}
		}
	}
}
