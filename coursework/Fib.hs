------------------------------------------------------------------------
-- Fibonacci number generator
------------------------------------------------------------------------

module Fib where

import HDL.Hydra.Core.Lib
import HDL.Hydra.Circuits.StandardCircuits

fib :: Clocked a => Int -> a -> ([a],[a])
fib size reset
	= (x,y)
	where
        x = wlatch size (mux1 reset (rippleAdd zero (bitSlice2 x y)) (take size (cycle zero)))
        y = wlatch size (mux1 reset x (take (size-1) (cycle zero) ++ one))

