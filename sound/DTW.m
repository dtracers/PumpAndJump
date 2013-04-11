function distance = DTW(mfccs1, mfccs2)
mfccs1 = mfccs1';
mfccs2 = mfccs2';

globalPathConstraint = 20;   
n = size(mfccs1,1);
m = size(mfccs2,1);

dtw = zeros(n,m);
dtw(:,:) = bitmax;
dtw(1,1) = getDistance(mfccs1(1,:),mfccs2(2,:));
for i=2:n
    dtw(i,1) =dtw(i-1,1) + getDistance(mfccs1(i,:),mfccs2(1,:));
end

for i=2:n
    dtw(1,i) = dtw(1,i-1)+ getDistance(mfccs1(1,:),mfccs2(i,:));
end

for i=2:n
    for j=max(2,i-globalPathConstraint):min(m,i+globalPathConstraint)
        cost = getDistance(mfccs1(i,:), mfccs2(j,:));
        d1 = cost + dtw(i-1,j);
        d2 = cost + dtw(i,j-1);
        d3 = 2 * cost + dtw(i-1,j-1);
        d4 = bitmax;
        
        if j>2
            d4 = 3*cost + dtw(i-1,j-2);
        end
        
        d5 = 3*cost + dtw(i-2,i-1);
        dtw(i,j)= min(d1,d2,d3,d4,d5);
    end
end

distance = dtw(n,m)/(m+n);



