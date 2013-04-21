package net.bluecow.spectro.detection.tempo;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class DistancePaint extends JPanel
{
	public static void main(String args[]) throws InterruptedException
	{
		String file = "The Hand That Feeds - Nine Inch Nails.mp3";
		file = getFileName();
		RegressionDetection detector = new RegressionDetection(null);
		detector.createBeatsFromAFile(file);
		DistancePaint p = new DistancePaint();
		p.setBackground(Color.white);
		detector.painter =p;
		JFrame f = createJFrame(p,file);

		if(TempoDetector.realTempo!=0)
		{
			 realDistance = TempoDetector.calculateDistanceFromTempo(TempoDetector.realTempo);
		}
		for(int k=0;k<detector.detectedBeats.size();k++)
		{
			detector.detectTempo(k);
			Thread.sleep(500);
			f.repaint();
		}
		f.repaint();
	}

	public static String getFileName()
	{
		JFileChooser jfc = new JFileChooser("../SpectroEdit/");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("txt files", "txt");
		jfc.setFileFilter(filter);
	    jfc.showDialog(null,"Open");
	    jfc.setVisible(true);
	    File filename = jfc.getSelectedFile();
	    return filename.getAbsolutePath();
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


	int width = 800,height = 800;
	int offX = 10;
	int offY = 10;
	double[] line;
	ArrayList<Distance> distances;
	public double averageDistance;
	public ArrayList<Distance> secondRound;
	public double[] line2;

	static double realDistance = 0;
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		double maxHeight =0.3; //(height-30)/distances.get(distances.size()-1).distance;

		int maxIndex = 0;
		if(distances!=null)
		{

			//System.out.println(maxHeight);
			int length = distances.size();

			g.setColor(Color.green);
			for(int k =0;k<distances.size();k++)
			{
				Distance d = distances.get(k);
				maxIndex = d.tempIndex;
				double Xlocation = ((double)maxIndex)*(double)width/length;
				double Ylocation = height-(d.distance)*maxHeight;

				g.drawRect((int)Xlocation, (int)Ylocation, 1, 1);
			}

			g.setColor(Color.red);
			if(TempoDetector.realTempo!=0)
			{
				g.drawLine(offX,height+offY,width+offX,(int)(height-((realDistance)*maxIndex*maxHeight+offY)));
				g.drawString("REAL TEMPO "+TempoDetector.realTempo, 200, 80);
				g.drawString("REAL Distance "+realDistance, 200, 90);
			}

			g.setColor(Color.GRAY);
			g.drawLine(offX,height+offY,width+offX,(int)(height-((averageDistance)*maxIndex*maxHeight+offY)));

			if(line!= null)
			{
				double a = line[0];
				double b = line[1];
				double R2 = line[2];
				if(R2>.998||true)
				{
					g.setColor(Color.blue);
					g.drawLine(offX,(int) (height-(a*0+b+offY)), width+offX,(int) (height-(a*maxIndex+b+offY)*maxHeight));
				}
					g.drawString("Y = "+a+"*x"+b, 0, 20);
					g.drawString("R = "+R2, 0, 40);
					g.drawString("Avg Dist "+averageDistance, 0, 70);
					g.drawString("TEMPO "+TempoDetector.calculateTempoFromDistance(a), 0, 80);
			}

			if(secondRound!= null)
			{
				g.setColor(Color.red);
				for(int k =0;k<secondRound.size();k++)
				{
					Distance d = secondRound.get(k);
					double Xlocation = d.tempIndex*width/length;
					double Ylocation = height-d.distance*maxHeight;

					g.drawRect((int)Xlocation, (int)Ylocation, 1, 1);
				}
			}

			if(line2!= null)
			{
				double a = line2[0];
				double b = line2[1];
				double R2 = line2[2];
				if(R2>.998||true)
				{
					g.setColor(Color.GRAY);
					g.drawLine(offX,(int) (height-(a*0+b+offY)), width+offX,(int) (height-(a*maxIndex+b+offY)*maxHeight));
				}
					g.setColor(Color.black);
					g.drawString("Y = "+a+"*x"+b, 0, 30);
					g.drawString("R = "+R2, 0, 50);
					g.drawString("TEMPO "+TempoDetector.calculateTempoFromDistance(a), 0, 90);
			}
		}else
		{
			System.out.println("NULL!");
		}
	}
}
