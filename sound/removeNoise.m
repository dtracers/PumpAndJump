function []= removeNoise(file,number)
x = wavread(file);
length = size(x,1);
fs = 44100;
m = 4096;
t = (0:length-1)*(1/fs);   % time range
N = 100;
% plot the original wave
figure
subplot(1,2,1); plot(t,x);
title(['plot for original ',number]);
% fft to remove noise frequency
frame_size = length/m ;
index = 1;

% automatically learn the threshold
sample = x(1:4096);
sample_fft = fft(sample);
NOISE_THRESHOLD = mean(abs(sample_fft)) * 1.57;
fprintf('%d\n',NOISE_THRESHOLD);
for i=1:frame_size
    xx = x(index: index + m-1);
    y = fft(xx);
    y(1:N) = 0;
    y(m-N+1:m) = 0;
    for j=1:m
        if abs(y(j)) < NOISE_THRESHOLD;
            y(j) = 0;
        end
    end
    xx = ifft(y);
    xx = real(xx);
    x(index: index + m-1)= xx;
    index = index + m;
end

% smooth x

for i=3:length-2
    x(i) = (x(i-1) + x(i-2) + x(i) + x(i+1)+ x(i+2))/5;
end

x(1:50) = 0;
x(length-50:length) = 0;

% plot the new wave
subplot(1,2,2);
plot(t,x);
title(['plot after noise reduction of ',number]);