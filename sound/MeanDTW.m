function distance = MeanDTW(query, template)
%       the function calculate the distance between two vectors
%       using dynanmic time wrapping. 
%       Input:
%           query: the candidate one dimensional vector
%           template: with which query will match, one dimensional vector
%       Ouput:
%           distance: distance between query and template
globalPathConstraint = 20;
query_size = size(query',1);
template_size = size(template',1);
if (query_size < 1) || (template_size < 1)
    disp('Array is out of bounds');
end

DTW = zeros(query_size,template_size);
for i=1:query_size
    for j=1:template_size
        DTW(i,j) = bitmax;
    end
end

%% Initialize base case and boundary situation
DTW(1,1) = getPointDistance(query(1),template(1));

for i=2:query_size
    DTW(i,1) = DTW(i-1,1) + getPointDistance(query(i),template(1));
end

for i=2:template_size
    DTW(1,i) = DTW(1,i-1) + getPointDistance(query(1),template(i));
end

%% Loop through all the cases, and find the minimum distance
for i=2:query_size
    a1 = max(2,i-globalPathConstraint/2);
    a2 = min(template_size,i+globalPathConstraint/2-1);
    for j=a1:a2
        cost = getDistance(query(i),template(j));
        d1 = cost + DTW(i-1,j);
        d2 = cost + DTW(i,j-1);
        d3 = 2*cost + DTW(i-1,j-1);
        DTW(i,j) = min([d1,d2,d3]);
    end
end

distance = DTW(query_size,template_size);






