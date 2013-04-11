x = wavread('training/gesture_0/sample_0.wav');
x = SSBoll79(x,44100,0.5);
length = size(x,1);
fs = 44100;                % sample rate
%% plot the sigmal in the time-domain
figure;
t = (0:length-1)*(1/fs);   % time range
plot(t,x);  grid on
xlabel('time(second)');
ylabel('amplitude');
title('signal in time domain');

%% analyze the signal in the frequency domain
m = 4096;                   % frame size
num_frames = length/m;
num_plot_rows = length/(m*3)+1;
index = 1;

for i=1:num_frames
    y = fft(x(index:index+m-1));
    y = real(y);
    index = index + m;
    % plot in the frequency domain
    f = (0:m-1)*(fs/m);     % frequency range
    y = abs(y);
    
    figure
    plot(f(1,:),y(:,1)); grid on
    xlabel('frequency');
    ylabel('power');
    title('frequency analysis');
end