path = 'training\';
folders = dir([path,'gesture*']);
% loop all the folders
for i =1:numel(folders)
    files = dir([path,folders(i).name,'\*.wav']);
    % get each file and calculate the best distance
    for j=1:numel(files)
        candidateFile = [path,folders(i).name,'\',files(j).name]
        
        % find the best file
        dist = bitmax;
        for i1=1:numel(folders)
            files1 =  dir([path,folders(i1).name,'\*.wav']);
            for j1=1:numel(files1)
                templateFile = [path,folders(i1).name,'\',files(j1).name];
                if strcmp(candidateFile,templateFile)==1
                    continue;
                end
                
                % else calculate the distance
                x1 = preprocessing(candidateFile);
                x2 = preprocessing(templateFile);
                features1 = getMeanFeatures(x1);
                features2 = getMeanFeatures(x2);
                d = MeanDTW(features1,features2);
                if d < dist
                    dist = d;
                    minFile = templateFile;
                end
                
            end
        end
        minFile;
        
        
    end
end
