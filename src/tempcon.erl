-module(tempcon). 
-export(start/0). 

start() ->
    spawn(?MODULE, loop)

send_cel_to_far(Sensor, Ref, C) ->
    F = C * (9/5) + 32.
    Sensor ! {self(), Ref, {far, F, C}}

send_far_to_cel(Sensor, Ref, F) ->
    C = (F - 32) * (5/9).
    Sensor ! {self(), Ref, {cel, C, F}}

loop() ->
    recieve
        {From, MsgRef, {celtofar, C}} ->
            send_cel_to_far(From, MsgRef, C, F)
        {From, MsgRef, {fartocel, F}} ->
            send_far_to_cel(From, MsgRef, C, F)
    end.
    loop().
