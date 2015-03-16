-module(tempdisplay). 
-export(start/0). 

start() ->
    spawn(?MODULE, loop)

loop() ->
    recieve
        {Pid, MsgRef, {Type, T}} ->
            case Type of
                cel ->
                    io:format("Reading: ~f ~s", T, "degrees C")
                far ->
                    io:format("Reading: ~f ~s", T, "degrees F")
    end.
    loop().
