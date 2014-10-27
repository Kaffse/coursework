------------------------------------------------------------------------
-- Fibonacci number generator
------------------------------------------------------------------------

module Fib where

import HDL.Hydra.Core.Lib
import HDL.Hydra.Circuits.StandardCircuits

fib :: Signal a => (a,a) -> a -> a
fib (x0,x1) reset
	= out
	where
		

rippleAdd4 :: Signal a => a -> [(a,a)] -> (a,[a])
rippleAdd4 cin [(x0,y0),(x1,y1),(x2,y2),(x3,y3)]
  = (c0, [s0,s1,s2,s3])
  where
    (c0,s0) = fullAdd (x0,y0) c1
    (c1,s1) = fullAdd (x1,y1) c2
    (c2,s2) = fullAdd (x2,y2) c3
    (c3,s3) = fullAdd (x3,y3) cin
