function meanFeatures = getMeanFeatures(samples)
unit = 44.1;
frameSize = 30;
overlapping =15;

s = 1;
index = 1;
while index < numel(samples)-frameSize*unit
    meanFeatures(s) = mean(abs(samples(index:index + frameSize*unit-1)));
    index = index + round(overlapping * unit);
    s= s+1;
end