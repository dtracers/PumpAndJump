% the function aims to display all the wav files 
% to see the result waves after noise reduction
files = dir(['training\gesture_01\','*.wav']);
for i=1:numel(files)
    
    x = wavread(['training\gesture_01\',files(i).name]);
    output = SSBoll79(x,44100,0.5);
    figure
    subplot(2,1,1);
    plot(x);
    title(['original signal for ', files(i).name]);
    subplot(2,1,2);
    plot(output);
    title(['signal for ',files(i).name, ' after noise reduction']);
    clear x;
    clear output;
end
