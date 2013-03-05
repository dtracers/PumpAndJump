I attempted to model the human leg for this project with 10 dof( 3 postion + 7 orientation ).
3 dof in the hip, 1 in the knee, 2 in the ankle, 1 near the toes.

press c to enter camera mode( the default mode )
- use w,a,s,d to move the camera
- left click with the mouse to change where the camera is looking

press r to enter the inverse kinematics
- right click to place fixed constraints
- left click to drag the leg( note that the leg can only move in certain driets with each joint )
- press v to remove all fixed joints
- press k to add a keyframe and then you must enter a time into the console
	- note to make all times absolute and greater than zero
	- also keyframes can only be add in the correct order so you must add
	  a keyframe at time 1.0, after time 0.0, but before time 2.0
	- also the time is in seconds
- also sometimes its kinda slow so remember to hold left click longer
	if it doesnt start moving at all try viewing from a different angle or restarting the program
	if it still doesnt move then you can go to the method:

	costEvaluation

	and

	uncomment these lines

	//	for( int i = 0; i < 14; i++ )
	//	{
	//		xm[ (fp->size()+2)*3 + i ] = 0.0;
	//	}

	and comment this one

	naturalGod( p, &xm[ (fp->size()+2)*3 ]);

	as it was my attempt at a natural function should run in plenty of time without it.

press f to animate the leg
- press v while animating to delete all keyframes in memory
- press p at anytime to print your keyframes into the text document ani.dat
- press l at anytime to load keyframes into the program
	- also note that there is already a jumping animation in ani.dat
	  so press l intially to view it