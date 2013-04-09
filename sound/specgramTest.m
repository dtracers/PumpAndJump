[x,fs] = wavread('sample_0.wav');
N = size(x,1);
frames = N/4096;
index = 1;
for i=1:frames
    figure
    title(['%d th figure',i]);
    subplot(2,1,1);
    plot(x(index:index+4096-1));
    subplot(2,1,2);
    specgram(x(index:index+4096-1),512,fs);
    index = index + 4096;
end