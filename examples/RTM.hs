----------------------------------------------------------------------------
-- Register Transfer Machine circuit
----------------------------------------------------------------------------

module RTM where
import HDL.Hydra.Core.Lib
import HDL.Hydra.Circuits.StandardCircuits

-- This module defines the rtm circuit and a simulation driver to
-- execute it.  The circuit will perform a sequence of operations on
-- the rtm.  The control and data signal settings are defined in
-- rtm_testinput_1, and the comments describe the action to be
-- performed and the expected effect.

-- To run the simulation:
--    ghci
--    :load RTM
--    main

----------------------------------------------------------------------------
-- Main program: run simulation

main =
  do putStrLn "Register Transfer Machine"
     sim_rtm 8 3 rtm_testinput_1

----------------------------------------------------------------------------
-- The rtm circuit

-- Size parameters
--   n = word size; each register contains n bits
--   k = file size; there are 2**k registers
-- The inputs are:
--   ld :: a (bit)
--      load control; 1 means load reg[d] := y
--   add :: a (bit)
--      data bus y control: y = if add=0 then x else s
--   d :: [a] (n-bit word)
--      destination register
--   sa :: [a] (k-bit word)
--      source register a (first operand)
--   sb :: [a] (k-bit word)
--      source register b (second operand)

rtm :: Clocked a => Int -> Int
  -> a -> a -> [a] -> [a] -> [a] -> [a] -> ([a],[a],[a],a,[a])

rtm n k ld add d sa sb x = (a,b,y,c,s)
  where
    (a,b) = regfile n k ld d sa sb y
    y = mux1w add x s
    (c,s) = rippleAdd zero (bitslice2 a b)

----------------------------------------------------------------------------
-- Test data

-- The effect is:
--   if ld=0 then no state change
--   if ld=1 then reg[d] := if add=0 then x else s

-- The expected outputs are:
--   a = R[sa]
--   b = R[sb]
--   s = a+b  (output of adder; carry output is ignored)
--   y = if add=0 then x else s  (data bus; value to be loaded if ld=1)

-- The only "real" outputs of the circuit are a and b.  The internal
-- signals s and y are also printed to see how the circuit works.


rtm_testinput_1 =
--       Inputs                   Effect            Expected Outputs
--  ld add d sa sb   x                              a      b     s    y
-- ~~~~~~~~~~~~~~~~~~~      ~~~~~~~~~~~~~~~~~   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  [[1, 0, 3, 0, 0, 125], -- R3 :=   x   = 125   R0=  0 R0=  0     0  125 (x)
   [1, 0, 6, 3, 0,  10], -- R6 :=   x   =  10   R3=125 R0=  0   125   10 (x)
   [1, 1, 2, 3, 6,   0], -- R2 := R3+R6 = 135   R3=125 R6= 10   135  135 (s)
   [1, 0, 1, 1, 2,  75], -- R1 :=   x   =  75   R1=  0 R2=135   135   75 (x)
   [1, 1, 1, 1, 2,   0], -- R1 := R1+R2 = 210   R1= 75 R2=135   210  210 (s)
   [0, 0, 0, 6, 1,   0]] -- nop                 R6= 10 R1=210   220    0 (x)

----------------------------------------------------------------------------
-- Simulation driver

sim_rtm n k input = runAllInput input output
  where
    ld  = getbit   input 0
    add = getbit   input 1
    d   = getbin k input 2
    sa  = getbin k input 3
    sb  = getbin k input 4
    x   = getbin n input 5
    (a,b,y,c,s) = rtm n k ld add d sa sb x
    output =
      [string "Input: ",
       bit ld, bit add, bindec 2 d, bindec 2 sa, bindec 2 sb,
       bindec 4 x,
       string "  Output: ",
       bindec 4 a, bindec 4 b, bindec 4 s, bindec 4 y]
