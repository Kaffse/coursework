------------------------------------------------------------------------
-- Fibonacci number generator
------------------------------------------------------------------------

module Main where

import HDL.Hydra.Core.Lib
import HDL.Hydra.Circuits.StandardCircuits

-- fib generic circuit, takes a size then a reset value
fib :: Clocked a => Int -> a -> ([a],[a])
fib size reset
	= (x,y)
	where
        (_, result) = rippleAdd zero (bitslice2 x y) --work out next fib number
        --if reset, use default values
        --else add
        x = wlatch size (
                mux1w reset 
                    result 
                    (take (size-1) (repeat zero) ++ [one])
                )
        y = wlatch size (mux1w reset x (take size (repeat zero)))

main :: IO ()
main =
     run_fib fib_input

run_fib input = runAllInput input output
    where
        size = 8 --Static size, as required by specs
        reset = getbit input 0

        (x,y) = fib size reset

        output =
          [string "Input: reset = ", bit reset,
           string "  Output: x = ", bindec 3 x, string " y = ", bindec 3 y]

fib_input = [[1],[0],[0],[0],[0],[0],[0],[0],[1],[0],[0],[0],[0],[0],[0],[0]]
