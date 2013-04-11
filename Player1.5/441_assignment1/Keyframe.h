#ifndef _WAMS_KEYFRAME_H
#define _WAMS_KEYFRAME_H

#include <vector>
#include <iostream>

using namespace std;

#define WAMS_PLAYER_DOF 16

namespace WAMS
{
	class Keyframe
	{
		public:
			float p[WAMS_PLAYER_DOF];
			float t;
			Keyframe( float* _p, float _t ): t( _t )
			{
				for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
				{
					p[i] = _p[i];
				}
			}
	};

	float lerp( float t, float p1, float p2 )
	{
		return p1+(p2-p1)*t;
	}

	float catmullrom( float t, float p0, float p1, float p2, float p3 )
	{
		return ( 0.5f * (
            (2 * p1 )  +
            ( (-p0 + p2) * t ) +
            ( (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t ) +
            ( (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t ) ) );
	}

	vector< Keyframe* > keyframes;
	Keyframe* kf0;
	Keyframe* kf1;
	Keyframe* kf2;
	Keyframe* kf3;
	float lastT = 0.0f;
	int lastIndex = 1;
	float lastTime = 0.0f;

	float* getAnimation( float t )
	{
		if( t < lastT )
		{
			lastIndex = 1;
			lastTime = 0.0f;
			lastT = 0;
		}
		else
		{
			lastT = t;
		}

		if( keyframes.size() - lastIndex < 1 )
		{
			if( keyframes.size() >= 2 )
			{
				float* newpos = new float[ WAMS_PLAYER_DOF ];
				for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
				{
					newpos[i] = keyframes[keyframes.size()-1]->p[i];
				}
				return newpos;
			}
			return NULL;
		}

		int currentIndex = lastIndex;
		float currentTime = lastTime;

		if( keyframes[2]->t > lastTime )
			currentTime = keyframes[2]->t;
		
		while( currentTime < t )
		{
			
			if( keyframes.size() - currentIndex < 3 )
			{
				if( keyframes.size() >= 1 )
				{
					float* newpos = new float[ WAMS_PLAYER_DOF ];
					for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
					{
						newpos[i] = keyframes[keyframes.size()-1]->p[i];
					}
					return newpos;
				}
				return NULL;
			}
			currentTime = keyframes[ currentIndex+2 ]->t;
			currentIndex++;
		}

		lastTime = currentTime;

		lastIndex = currentIndex;
		
		kf0 = keyframes[ currentIndex - 1];
		kf1 = keyframes[ currentIndex  ];
		kf2 = keyframes[ currentIndex + 1 ];
		if( keyframes.size() <= currentIndex+2 )
			kf3 = keyframes[ 0 ];
		else
			kf3 = keyframes[ currentIndex + 2 ];

		float* newpos = new float[ WAMS_PLAYER_DOF ];

		for( int i = 0; i < WAMS_PLAYER_DOF; i++ )
		{
			float newP;
			if( abs( kf3->p[i] - kf0->p[i] )*.05f > abs(kf2->p[i] - kf1->p[i])  )
			{
				newP = lerp( (t - kf1->t)/( kf2->t - kf1->t ), kf1->p[i], kf2->p[i] );
			}
			else
			{
				newP = catmullrom( (t - kf1->t)/( kf2->t - kf1->t ), kf0->p[i], kf1->p[i], kf2->p[i], kf3->p[i] );
			}
			newpos[i] = newP;
		}

		return newpos;
	}
};

#endif