function eDist = getDistance(v1,v2)
length = size(v1,1);
eDist = 0;
for i=1:length
    eDist = eDist + (v1(i,1)-v2(i,1))*(v1(i,1)-v2(i,1));
end

eDist = sqrt(eDist);