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