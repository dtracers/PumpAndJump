package net.bluecow.spectro.detection;

import java.awt.Color;
import java.util.ArrayList;


public class Statistics
{
	public static boolean debug = false;

	/**
	 * a is at 0
	 * b is at 1
	 *
	 * @param startIndex
	 * @param endIndex
	 * @return a double array with the line equation and it also will fill the Distance array
	 */
	public static double[] leastSquares(int startIndex,int numberOf,ArrayList<Beat> beats,ArrayList<Distance> distances)
	{
		int start = startIndex;
		int end = startIndex+numberOf;

		double[] x = new double[numberOf];
	    double[] y = new double[numberOf];
	    double sumx  =0;//= x[N];
		double sumy  =0;//+= y[N];
		double sumx2 =0;//+= x[N]*x[N];
		double sumy2 =0;//+= y[N]*y[N];
		double sumxy =0;//+= x[N]*y[N];
		for(int k=start+1;k<end;k++)
		{
			Beat startingBeat = beats.get(k-1);
			Beat otherBeat = beats.get(k);
			int index = k -(start+1);
			Distance d = new Distance(otherBeat.sampleLocation - startingBeat.sampleLocation,1,startingBeat,otherBeat);
			x[index] = index;
			y[index] = d.distance;
			distances.add(d);

			sumx  += x[index];
			sumy  += y[index];
			sumx2 += x[index]*x[index];
			sumy2 += y[index]*y[index];
			sumxy += x[index]*y[index];
		}

		double a = (numberOf*sumxy - sumx*sumy) / (numberOf*sumx2 - sumx*sumx);
		double b = (sumy - a*sumx) / numberOf;
		return new double[]{a,b};
	}

	/**
	 * a is at 0
	 * b is at 1
	 *
	 * @param startIndex
	 * @param endIndex
	 * @return a double array with the line equation and it also will fill the Distance array
	 */
	public static double[] leastSquares(int numberOf,ArrayList<Beat> beats,ArrayList<Distance> distances)
	{
		int MAXN = numberOf;
		int n = 1;
		double[] x = new double[MAXN];
		double[] y = new double[MAXN];

		// first pass: read in data, compute xbar and ybar
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
		while(n<beats.size())
		{
			Beat startingBeat = beats.get(n-1);
			Beat otherBeat = beats.get(n);
			Distance d = new Distance(otherBeat.sampleLocation - startingBeat.sampleLocation,1,startingBeat,otherBeat);
			distances.add(d);
		    x[n] = n-1;
		    y[n] = d.distance;
		    sumx  += x[n];
		    sumx2 += x[n] * x[n];
		    sumy  += y[n];
		    n++;
		}
		double xbar = sumx / n;
		double ybar = sumy / n;

		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
		    xxbar += (x[i] - xbar) * (x[i] - xbar);
		    yybar += (y[i] - ybar) * (y[i] - ybar);
		    xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;

		// print results
		if(debug)
			System.out.println("y   = " + beta1 + " * x + " + beta0);

		// analyze results
		int df = n - 2;
		double rss = 0.0;      // residual sum of squares
		double ssr = 0.0;      // regression sum of squares
		for (int i = 0; i < n; i++) {
		    double fit = beta1*x[i] + beta0;
		    rss += (fit - y[i]) * (fit - y[i]);
		    ssr += (fit - ybar) * (fit - ybar);
		}
		double R2    = ssr / yybar;
		double svar  = rss / df;
		double svar1 = svar / xxbar;
		double svar0 = svar/n + xbar*xbar*svar1;

		if(debug)
		{
			System.out.println("R^2                 = " + R2);
			System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
			System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
			svar0 = svar * sumx2 / (n * xxbar);
			System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

			System.out.println("SSTO = " + yybar);
			System.out.println("SSE  = " + rss);
			System.out.println("SSR  = " + ssr);
		}

		return new double[]{beta1,beta0,R2,Math.sqrt(svar1),Math.sqrt(svar1),Math.sqrt(svar1),yybar,rss,ssr};
	}

	/**
	 * a is at 0
	 * b is at 1
	 *
	 * @param startIndex
	 * @param endIndex
	 * @return a double array with the line equation and it also will fill the Distance array
	 */
	public static double[] weightedLeastSquares(int numberOf,ArrayList<Distance> distances,double average)
	{
		int MAXN = numberOf*10;
		int n = 0;
		int counter = 0;
		double[] x = new double[MAXN];
		double[] y = new double[MAXN];

		// first pass: read in data, compute xbar and ybar
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
		while(counter<distances.size())
		{
			Distance d = distances.get(counter);
			double strength = d.strength;
			if(strength>average)
				strength = average;
			while(strength <= average)
			{
			    x[n] = counter;
			    y[n] = distances.get(counter).distance;
			    sumx  += x[n];
			    sumx2 += x[n] * x[n];
			    sumy  += y[n];
			    n++;
			    strength+=.2;
			}
		}
		double xbar = sumx / n;
		double ybar = sumy / n;

		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
		    xxbar += (x[i] - xbar) * (x[i] - xbar);
		    yybar += (y[i] - ybar) * (y[i] - ybar);
		    xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		double beta1 = xybar / xxbar;
		double beta0 = ybar - beta1 * xbar;

		// print results
		if(debug)
			System.out.println("y   = " + beta1 + " * x + " + beta0);

		// analyze results
		int df = n - 2;
		double rss = 0.0;      // residual sum of squares
		double ssr = 0.0;      // regression sum of squares
		for (int i = 0; i < n; i++) {
		    double fit = beta1*x[i] + beta0;
		    rss += (fit - y[i]) * (fit - y[i]);
		    ssr += (fit - ybar) * (fit - ybar);
		}
		double R2    = ssr / yybar;
		double svar  = rss / df;
		double svar1 = svar / xxbar;
		double svar0 = svar/n + xbar*xbar*svar1;

		if(debug)
		{
			System.out.println("R^2                 = " + R2);
			System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
			System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
			svar0 = svar * sumx2 / (n * xxbar);
			System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

			System.out.println("SSTO = " + yybar);
			System.out.println("SSE  = " + rss);
			System.out.println("SSR  = " + ssr);
		}

		return new double[]{beta1,beta0,R2,Math.sqrt(svar1),Math.sqrt(svar1),Math.sqrt(svar1),yybar,rss,ssr};
	}


	/**
	 * this will compute the distances between the points and the line
	 * abs(y1 - a*x1 - b)/sqrt(a^2+1)
	 * it will set the values to the distance
	 * it returns the average distance
	 */
	public static double distances(int startIndex,int numberOf,ArrayList<Distance> distances,double[] line)
	{
		int end = startIndex+numberOf;

	//	double[] distanceFromLine = new double[numberOf];

		double total = 0;
		for(int k=startIndex;k<end;k++)
		{
			Distance d = distances.get(k);
			double top = Math.abs(d.distance-line[0]*k-line[1]);
			double bottom = Math.sqrt(line[0]*line[0]+1);
			d.strength = top/bottom;
			total += d.strength;
		}
		return total/numberOf;
	}

	/**
	 * Removes the items below the average
	 * @param distances
	 * @param temp
	 * @param average
	 */

	public static void removeItemsBelowAverage(ArrayList<Distance> distances,ArrayList<Beat> temp, double average)
	{
		for(int k=0;k<distances.size();k++)
		{
			Distance d = distances.get(k);
			if(d.strength<average)
			{
				distances.remove(k);
				temp.remove(d.other);
				k-=1;
			}
		}
	}

	/**
	 * Removes the items above the average
	 * @param distances
	 * @param temp
	 * @param average
	 * @return
	 */

	public static ArrayList<Beat> removeItemsAboveAverage(ArrayList<Distance> distances,ArrayList<Beat> temp, double average)
	{
		ArrayList<Beat> removedItems = new ArrayList<Beat>();
		for(int k=0;k<distances.size();k++)
		{
			Distance d = distances.get(k);
			if(d.strength>average)
			{
				distances.remove(k);
				temp.remove(d.other);
				removedItems.add(d.other);
				k-=1;
			}
		}
		return removedItems;
	}


	//more general methods

	/**
	 * Computes the average from the things to Average
	 * @param thingsToAverage
	 * @return
	 */
	public static double average(double[] thingsToAverage)
	{
		int length = thingsToAverage.length;
		double total = 0;
		for(int k=0; k<length;k++)
		{
			total+=thingsToAverage[k];
		}
		return total/length;
	}

	public static void copy(int start,int numberOf,ArrayList from,ArrayList to)
	{
		int end = start+numberOf;
		for(int k= start;k<end;k++)
		{
			to.add(from.get(k));
		}
	}

}

class Distance implements Comparable
{
	Color col = new Color((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255));
	double distance;
	double strength = 0.5;
	Beat starting;
	Beat other;
	public Distance(long l, double i, Beat startingBeat, Beat other)
	{
		distance = l;
		strength = i;
		starting = startingBeat;
		this.other = other;
	}
	@Override
	public int compareTo(Object arg0)
	{
		return (int) Math.signum(distance-((Distance)arg0).distance);
	}

	public String toString()
	{
		return "d: "+distance+" s: "+strength;
	}
}
