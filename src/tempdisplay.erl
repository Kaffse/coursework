-module(tempdisplay). 
-export([start/0]). 

start() ->
    spawn(?MODULE, loop, []).

loop() ->
    receive
        {Pid, MsgRef, {Type, T}} ->
            case Type of
                cel ->
                    io:format("Reading: ~f ~s", T, "degrees C"),
                    loop();
                far ->
                    io:format("Reading: ~f ~s", T, "degrees F"),
                    loop()
            end
    end.
