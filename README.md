Jaty68k
====

Fork of a [Motorola 680x0 emulator written in Java](https://github.com/tonyheadford/m68k) that is specifically designed to easily run and debug the linux kernel of the Katy 68k PCB project.

http://www.bigmessowires.com/68-katy/

Building
--------

There is an Apache Ant build file included. Just run ant from the project directory.

Running
-------

There is a simple cpu monitor console to enable testing/debugging. This can be invoked by running the following at the command prompt:

	$ java -cp m68k.jar m68k.Monitor linux-pcb.bin
	
Once the zBug(ROM) loads simply type "j" and then "003000" to start Katy 68k


Changes
-------
* Application console uses a monospaced font now.

* The Java monitor is working again.

* Add the Go command in the java monitor help page


After starting m68.jar whithout a file you have access to the java monitor. Try "h" to see the help page. Java monitor is also available after exception.

        $ java -cp m68k.jar m68k.Monitor

        > h<CR>

Then type:

        > load 0 linux-pcb.bin<CR>
        > g<CR>

Inside the zBug(ROM) monitor type "r" to see the registers, than "s" to get exception. and return back to the java monitor.


TODO
----
* It would be nice to have extensive trace capability and register & device infrastructure like in https://github.com/John-Titor/py68k project. 

* backport changes from upstream m68k project 


