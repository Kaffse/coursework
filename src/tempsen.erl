-module(tempsen). 
-export(start/2). 

start(convertor, display) ->
    spawn(?MODULE, loop, [convertor, display])

send_temp(convertor) ->
    ReplyRef = make_ref(). 
    Temp = random:uniform(50).  
    convertor ! {self(), ReplyRef, {celtofar, Temp}}.  

loop(convetor, display) ->
    recieve 
        {Pid, {tick}} ->
            send_temp(convertor)
        {Pid, MsgRef, {cel, C, F}} ->
            display ! {self(), MsgRef, {cel, C}}
        {Pid, MsgRef, {far, F, C}} ->
            display ! {self(), MsgRef, {far, F}}
    end. 
    loop(convertor, display).
