files = dir('training\gesture_89\*.wav');
for i=1:numel(files)
    [x,fs] = wavread(['training\gesture_89\',files(i).name]);
    output = SSBoll79(x,fs,0.5);
    wavwrite(output,fs,['new\gesture_89\',files(i).name]);  
end