#include "point.h"
#include <vector>

#ifndef _WAMS_POSE_H
#define _WAMS_POSE_H

namespace WAMS
{
	float* bmin = new float[7];
	float* bmax = new float[7];
	float* DistanceConstants = new float[7];
	float* BoundsConstants = new float[7];
	class Pose
	{
		public:
			vector<float*> data;
			Pose()
			{

			}

			void addData( float* d )
			{
				data.push_back( d );
			}

			float minf( int n )
			{
				if( n < 7 )
				{
					float f = 0.0f; 
					for( unsigned int i = 0; i < data.size(); i++ )
					{
						if( data[i][n] < f )
						{
							f = data[i][n];
						}
					}
					return f;
				}
				return 0.0f;
			}

			float maxf( int n )
			{
				if( n < 7 )
				{
					float f = 0.0f; 
					for( unsigned int i = 0; i < data.size(); i++ )
					{
						if( data[i][n] > f )
						{
							f = data[i][n];
						}
					}
					return f;
				}
				return 0.0f;
			}

			float closest( int n, float v )
			{
				if( n < 7 )
				{
					float f = FLT_MAX;
					for( unsigned int i = 0; i < data.size(); i++ )
					{
						if( data[i][n] - v < f )
						{
							f = data[i][n];
						}
					}
					if( f != FLT_MAX )
						return f;
				}
				return -1.0f;
			}
	};

	vector< Pose* > poses;

	void setBounds()
	{
		for(int j = 0; j <7; j++)
		{
			bmin[j] = 0;
			bmax[j] = 0;
		}

		for(int j = 0; j <7; j++)
		{
			for( unsigned int i = 0; i < poses.size(); i ++ )
			{
				if( poses[i]->minf(j) < bmin[j] )
					bmin[j] = poses[i]->minf( j );

				if( poses[i]->maxf(j) > bmax[j] )
					bmax[j] = poses[i]->maxf( j );
			}
		}
	}

	void naturalGod(double* p,double* x)
	{
		p+=3;

		//bounds calculations
		float* boundsConst = new float[7];
		for(int i = 0; i < 7; i++)
		{
			float minDist = fabs((float)p[i] - bmin[i]);
			float maxDist = fabs((float)p[i] - bmax[i]);
			if( (bmax[i] - bmin[i]) == 0.0f )
			{
				if(minDist == 0.0f && maxDist == 0.0f)
					boundsConst[i] = 0.0f;
				else
					boundsConst[i] = 1.0f;
			}
			else
				boundsConst[i] = (minDist+maxDist)/(bmax[i] - bmin[i])-1;
		}

		//closest calculations
		float* closestPoseValues = new float[7];
		float* tempValues = new float[7];
		float squareMin;
		for( unsigned int i = 0; i < poses.size(); i ++ )
		{
			float squareDist = 0;
			for(int j = 0; j< 7; j++)
			{
				tempValues[j]=poses[i]->closest(j,(float)p[j])-(float)p[j];
				squareDist += tempValues[j]*tempValues[j];
			}
			if(i==0||squareDist<squareMin)
			{
				for(int j = 0; j< 7; j++)
				{
					closestPoseValues[j] = tempValues[j];
				}
				squareMin = squareDist;
			}
		}
		delete tempValues;

		//x calculations

		for(int i = 0; i < 7; i++)
		{
			//cerr<<"b "<<i<<":"<<boundsConst[i]<<endl;
			//cout<<"c "<<fabs(closestPoseValues[i])<<endl;
			x[i] = (double)(boundsConst[i]*BoundsConstants[i]); 
			x[i+7] = (double)(fabs(closestPoseValues[i])*DistanceConstants[i]);
			//cout<<x[i]<<endl;
		}
		delete boundsConst;
		delete closestPoseValues;
	}

	void PosesInit()
	{
		poses.push_back( new Pose() );
		/*
		Chair leg 1
							/
						   /
						  /
		_____| __________/

		*/
						
		float* d = new float[7]; 
		d[0] = 48.32f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -41.68f;
		d[4] = 0.0f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );
		d = new float[7];
		d[0] = 49.86f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -40.14f;
		d[4] = 0.0f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );
		

		/*

		H_________K______A___T/

		
		Will's pose*/
		
		poses.push_back( new Pose() );
		d = new float[7];
		d[0] = 90.0f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = 0.0f;
		d[4] = 26.68f;	d[5] = 0.0f;
		d[6] = 76.65f;
		poses[0]->addData( d );
		d = new float[7];
		d[0] = 90.0f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = 0.0f;
		d[4] = 43.0f;	d[5] = 0.0f;
		d[6] = 82.10f;
		poses[0]->addData( d );
		d = new float[7];
		d[0] = 90.0f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = 0.0f;
		d[4] = 5.6f;	d[5] = 0.0f;
		d[6] = 69.85f;
		poses[0]->addData( d );
		
		/*
		chair leg 2
			     /K\
			    /   \
	      _T__A/	 \H
		*/
		poses.push_back( new Pose() );
		d = new float[7];
		d[0] = 231.06f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -107.94f;
		d[4] = 55.439f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );
		d = new float[7];
		d[0] = 223.17f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -102.85f;
		d[4] = 49.88f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );
		
		/*
		Leg 3 Hiesman Back
			    H
		       /
		      /
		     K
		   / 
		 /
		A
		|
		T__
		
		
		*/
		
		poses.push_back( new Pose() );
		d = new float[7];
		d[0] = 67.114f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -26.995f;
		d[4] = 25.024f;	d[5] = 0.0f;
		d[6] = 92.139f;
		poses[0]->addData( d );
		d = new float[7];
		d[0] = 61.726f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -41.976f;
		d[4] = 13.1737f;	d[5] = 0.0f;
		d[6] = 74.9f;
		poses[0]->addData( d );
		
		/*

		Leg 3 Hiesman Front

		H___K
		    /
		   /
		  A__T


		*/
		poses.push_back( new Pose() );
		d = new float[7];
		d[0] = 120.0f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -79.8357f;
		d[4] = 139.8357f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );
		d = new float[7];
		d[0] = 129.166f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -79.8357f;
		d[4] = 130.669f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );

		/*
			New Step
		*/


		poses.push_back( new Pose() );
		d = new float[7];
		d[0] = 108.40f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -8.24f;
		d[4] = 71.59f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );
		d = new float[7];
		d[0] = 118.27f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -18.11f;
		d[4] = 61.73f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );

		/*
			new step 2
		*/

		poses.push_back( new Pose() );
		d = new float[7];
		d[0] = 82.016f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -49.6798f;
		d[4] = 57.6633f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );
		d = new float[7];
		d[0] = 90.0f;	d[1] =  0.0f;	d[2] =  0.0f;
		d[3] = -42.6679f;
		d[4] = 42.6679f;	d[5] = 0.0f;
		d[6] = 0.0f;
		poses[0]->addData( d );
		setBounds();
		for( int i = 0; i < 7; i++ )
		{
			//cerr<<"mi "<<bmin[i]<<endl;
			//cerr<<"ma "<<bmax[i]<<endl;
			//cerr<<"d "<<i<<":"<<(bmax[i]-bmin[i])<<endl;
			DistanceConstants[i] = 0.05f;//.05f;//.003
			BoundsConstants[i] = 0.048f;//0.048f;
		}
		BoundsConstants[3] = 1.0f;
		BoundsConstants[4] = 1.0f;
	}
};

#endif