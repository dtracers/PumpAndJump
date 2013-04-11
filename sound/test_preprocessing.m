files = dir(['training\gesture_9\','*.wav'])
for i=1:numel(files)
    f = ['training\gesture_9\',files(i).name];
    preprocessing(f);
end
