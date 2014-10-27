module Main where

import HDL.Hydra.Core.Lib
import HDL.Hydra.Circuits.StandardCircuits
import Fib

------------------------------------------------------------------------
-- Main program

-- Print a line to separate output from different simulations
separator :: IO ()
separator = putStrLn (take 76 (repeat '-'))

-- The main program runs a sequence of simulations

main :: IO ()
main =
  do separator
     putStrLn "Simulate Fibanacci"
     run_fib fib_input

run_fib input = runAllInput input output
    where
        reset = getbit input 0
        size = getbit input 1
        (x,y) = Fib size reset
