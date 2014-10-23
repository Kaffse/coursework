------------------------------------------------------------------------
-- Hydra: A functional digital circuit design language
-- Example: RunAdd
------------------------------------------------------------------------

-- Run simulations of a collection of binary adder circuits defined in
-- Add.hs.  These include adders defined for specific word sizes as
-- well as a general n-bit adder generator at several word sizes.  To
-- run the simulations, enter the following:

--    ghci
--    :load RunAdd
--    main

-- This file illustrates how to write and run simulation drivers, so
-- it contains more elaborate comments than usual.  The file contains:

-- Module definition and imports
-- Main program
-- Simulation driver for half adder
-- Simulation driver for word adder
-- Test data

------------------------------------------------------------------------
-- Module definition and imports

module Main where

import HDL.Hydra.Core.Lib
import HDL.Hydra.Circuits.StandardCircuits
import Add

------------------------------------------------------------------------
-- Main program

-- Print a line to separate output from different simulations
separator :: IO ()
separator = putStrLn (take 76 (repeat '-'))

-- The main program runs a sequence of simulations

main :: IO ()
main =
  do separator
     putStrLn "Simulate half adder"
     run_halfAdd halfAdd_input1

     separator
     putStrLn "4-bit adder"
     run_adder rippleAdd4 4 test_data_add4

     separator
     putStrLn "6-bit adder"
     run_adder rippleAdd6 6 test_data_add6

     separator
     putStrLn "8-bit adder"
     run_adder rippleAdd8 8 test_data_add8

     separator
     putStrLn "General n-bit adder at 8 bits"
     run_adder rippleAdd 8 test_data_add8

     separator
     putStrLn "General n-bit adder at 16 bits"
     run_adder rippleAdd 16 test_data_add16

     separator

------------------------------------------------------------------------
-- Simulation driver for half adder

run_halfAdd input = runAllInput input output
  where

-- Extract input signals
    x = getbit input 0
    y = getbit input 1

-- The circuit to be simulated
    (c,s) = halfAdd x y

-- Format the output
    output =
      [string "Input: x = ", bit x, string " y = ", bit y,
       string "  Output: c = ", bit c, string " s = ", bit s]

------------------------------------------------------------------------
-- Simulation driver for word adder

type Bit = Stream Bool
type Word = [Bit]

run_adder
  :: (Bit -> [(Bit,Bit)] -> (Bit,Word))
  -> Int
  -> [[Int]]
  -> IO ()

run_adder adder k input = runAllInput input output
  where

-- Extract input signals
    cin = getbit   input 0
    x   = getbin k input 1
    y   = getbin k input 2

-- The circuit to be simulated
    (cout,s) = adder cin (zip x y)

-- Format the output
    output =
      [string "  x = ", bindec 6 x,
       string "  y = ", bindec 6 y,
       string "  cin = ", bit cin,
       string " ==> cout = ", bit cout,
       string "  s = ", bindec 6 s]

------------------------------------------------------------------------
-- Test data

-- Test data for half adder consists of two bits per cycle

halfAdd_input1 =
  [[0, 0],
   [0, 1],
   [1, 0],
   [1, 1]]

-- Test data for word adder consists of a carry input bit and two
-- integers per cycle.  Any of the test data definitions can be used
-- with a word adder of any size, but the specific test data defined
-- below keeps the data small enough to make sense; thus
-- test_data_add4 contains smaller numbers than test data for larger
-- words.

test_data_add4 =
--  c  x  y
  [[0,  5,  8],
   [1,  2, 12]]

test_data_add6 =
--  c  x  y
  [[0,  5,  8],
   [1,  2, 12],
   [0, 41, 13]]

test_data_add8 =
--  c    x    y
  [[0,   5,   8],
   [1,   2,  12],
   [0,  41,  13],
   [0, 103,  59],
   [0, 178, 193],
   [1,  17, 209]]

test_data_add16 =
--  c      x      y
  [[0,     5,     8],
   [1,     2,    12],
   [0,    41,    13],
   [0,   103,    59],
   [0,   178,   193],
   [0,  9037, 20185],
   [0, 31000, 32000],
   [0, 51000, 40000],
   [1,    17,   209]]
