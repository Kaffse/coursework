ArrayMaxRun : ArrayMax
	./ArrayMax >ArrayMax-output-temp.txt

ArrayMax : ArrayMax.hs M1cpu.hs M1run.hs
	ghc --make ArrayMax

clean :
	rm -f *.o *.hi *~ *temp.txt
	rm -f ArrayMax
