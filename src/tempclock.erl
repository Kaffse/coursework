-module(tempclock). 
-export([start/1]). 

start(sensors) ->
    spawn(?MODULE, loop, [sensors]).

send_tick(sensors) ->
    {Sone, Stwo} = sensors, 
    Sone ! {self(), {tick}},
    Stwo ! {self(), {tick}}.

loop(sensors) ->
    receive
    after 1000 -> 
        send_tick(sensors),
        loop(sensors)
    end.
