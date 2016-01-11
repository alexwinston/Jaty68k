package m68k.cpu;

import junit.framework.TestCase;
import m68k.cpu.instructions.RTE;
import m68k.memory.AddressSpace;
import m68k.memory.MemorySpace;

/*
//  M68k - Java Amiga MachineCore
//  Copyright (c) 2008-2010, Tony Headford
//  All rights reserved.
//
//  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
//  following conditions are met:
//
//    o  Redistributions of source code must retain the above copyright notice, this list of conditions and the
//       following disclaimer.
//    o  Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
//       following disclaimer in the documentation and/or other materials provided with the distribution.
//    o  Neither the name of the M68k Project nor the names of its contributors may be used to endorse or promote
//       products derived from this software without specific prior written permission.
//
//  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
//  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
//  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
//  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
//  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
//  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
//  OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//
*/
public class MC68000Test extends TestCase
{
	AddressSpace bus;
	Cpu cpu;

	public void setUp()
	{
		bus = new MemorySpace(1024);	//create 1kb of memory for the cpu
		cpu = new MC68000();
		cpu.setAddressSpace(bus);
		cpu.reset();
	}

	public void testDataRegs()
	{
		for(int r = 0; r < 7; r++)
		{
			cpu.setDataRegisterByte(r, 0xaa);
			assertEquals(0xaa, cpu.getDataRegisterByte(r));
			assertEquals(0xaa, cpu.getDataRegisterWord(r));
			assertEquals(0xaa, cpu.getDataRegisterLong(r));
			assertEquals(0xffffffaa, cpu.getDataRegisterByteSigned(r));
			assertEquals(0x000000aa, cpu.getDataRegisterWordSigned(r));

			cpu.setDataRegisterWord(r, 0xa5a5);
			assertEquals(0xa5, cpu.getDataRegisterByte(r));
			assertEquals(0xa5a5, cpu.getDataRegisterWord(r));
			assertEquals(0xa5a5, cpu.getDataRegisterLong(r));
			assertEquals(0xffffffa5, cpu.getDataRegisterByteSigned(r));
			assertEquals(0xffffa5a5, cpu.getDataRegisterWordSigned(r));

			cpu.setDataRegisterLong(r, 0x85858585);
			assertEquals(0x85, cpu.getDataRegisterByte(r));
			assertEquals(0x8585, cpu.getDataRegisterWord(r));
			assertEquals(0x85858585, cpu.getDataRegisterLong(r));
			assertEquals(0xffffff85, cpu.getDataRegisterByteSigned(r));
			assertEquals(0xffff8585, cpu.getDataRegisterWordSigned(r));

			cpu.setDataRegisterLong(r, 0x12345678);
			assertEquals(0x78, cpu.getDataRegisterByte(r));
			assertEquals(0x5678, cpu.getDataRegisterWord(r));
			assertEquals(0x12345678, cpu.getDataRegisterLong(r));
			assertEquals(0x78, cpu.getDataRegisterByteSigned(r));
			assertEquals(0x5678, cpu.getDataRegisterWordSigned(r));
		}
	}

	public void testAddrRegs()
	{
		for(int r = 0; r < 7; r++)
		{
			cpu.setAddrRegisterByte(r, 0xaa);
			assertEquals(0xaa, cpu.getAddrRegisterByte(r));
			assertEquals(0xaa, cpu.getAddrRegisterWord(r));
			assertEquals(0xaa, cpu.getAddrRegisterLong(r));
			assertEquals(0xffffffaa, cpu.getAddrRegisterByteSigned(r));
			assertEquals(0x000000aa, cpu.getAddrRegisterWordSigned(r));

			cpu.setAddrRegisterWord(r, 0xa5a5);
			assertEquals(0xa5, cpu.getAddrRegisterByte(r));
			assertEquals(0xa5a5, cpu.getAddrRegisterWord(r));
			assertEquals(0xa5a5, cpu.getAddrRegisterLong(r));
			assertEquals(0xffffffa5, cpu.getAddrRegisterByteSigned(r));
			assertEquals(0xffffa5a5, cpu.getAddrRegisterWordSigned(r));

			cpu.setAddrRegisterLong(r, 0x85858585);
			assertEquals(0x85, cpu.getAddrRegisterByte(r));
			assertEquals(0x8585, cpu.getAddrRegisterWord(r));
			assertEquals(0x85858585, cpu.getAddrRegisterLong(r));
			assertEquals(0xffffff85, cpu.getAddrRegisterByteSigned(r));
			assertEquals(0xffff8585, cpu.getAddrRegisterWordSigned(r));

			cpu.setAddrRegisterLong(r, 0x12345678);
			assertEquals(0x78, cpu.getAddrRegisterByte(r));
			assertEquals(0x5678, cpu.getAddrRegisterWord(r));
			assertEquals(0x12345678, cpu.getAddrRegisterLong(r));
			assertEquals(0x78, cpu.getAddrRegisterByteSigned(r));
			assertEquals(0x5678, cpu.getAddrRegisterWordSigned(r));
		}
	}

	public void testPC()
	{
		bus.writeLong(4, 0x12345678);
		cpu.setPC(4);
		int val = cpu.fetchPCLong();
		assertEquals(0x12345678, val);
		assertEquals(8, cpu.getPC());

		cpu.setPC(4);
		val = cpu.fetchPCWord();
		assertEquals(0x1234, val);
		assertEquals(6, cpu.getPC());
		val = cpu.fetchPCWord();
		assertEquals(0x5678, val);
		assertEquals(8, cpu.getPC());

		cpu.setPC(4);
		bus.writeLong(4, 0x12348765);
		val = cpu.fetchPCWordSigned();
		assertEquals(0x1234, val);
		assertEquals(6, cpu.getPC());
		val = cpu.fetchPCWordSigned();
		assertEquals(0xffff8765, val);
		assertEquals(8, cpu.getPC());
	}

	public void testFlags()
	{
		cpu.setSR(0x27ff);
		assertTrue(cpu.isFlagSet(Cpu.C_FLAG));
		assertTrue(cpu.isFlagSet(Cpu.V_FLAG));
		assertTrue(cpu.isFlagSet(Cpu.Z_FLAG));
		assertTrue(cpu.isFlagSet(Cpu.N_FLAG));
		assertTrue(cpu.isFlagSet(Cpu.X_FLAG));
		assertTrue(cpu.isSupervisorMode());
		assertEquals(7, cpu.getInterruptLevel());

		cpu.setSR(0);
		assertFalse(cpu.isFlagSet(Cpu.C_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.V_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.Z_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.N_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.X_FLAG));
		assertFalse(cpu.isSupervisorMode());
		assertEquals(0, cpu.getInterruptLevel());

		cpu.setCCRegister(Cpu.C_FLAG);
		assertTrue(cpu.isFlagSet(Cpu.C_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.V_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.Z_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.N_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.X_FLAG));
		assertFalse(cpu.isSupervisorMode());
		assertEquals(0, cpu.getInterruptLevel());

		cpu.setCCRegister(Cpu.V_FLAG);
		assertFalse(cpu.isFlagSet(Cpu.C_FLAG));
		assertTrue(cpu.isFlagSet(Cpu.V_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.Z_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.N_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.X_FLAG));
		assertFalse(cpu.isSupervisorMode());
		assertEquals(0, cpu.getInterruptLevel());

		cpu.setCCRegister(Cpu.Z_FLAG);
		assertFalse(cpu.isFlagSet(Cpu.C_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.V_FLAG));
		assertTrue(cpu.isFlagSet(Cpu.Z_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.N_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.X_FLAG));
		assertFalse(cpu.isSupervisorMode());
		assertEquals(0, cpu.getInterruptLevel());

		cpu.setCCRegister(Cpu.N_FLAG);
		assertFalse(cpu.isFlagSet(Cpu.C_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.V_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.Z_FLAG));
		assertTrue(cpu.isFlagSet(Cpu.N_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.X_FLAG));
		assertFalse(cpu.isSupervisorMode());
		assertEquals(0, cpu.getInterruptLevel());

		cpu.setCCRegister(Cpu.X_FLAG);
		assertFalse(cpu.isFlagSet(Cpu.C_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.V_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.Z_FLAG));
		assertFalse(cpu.isFlagSet(Cpu.N_FLAG));
		assertTrue(cpu.isFlagSet(Cpu.X_FLAG));
		assertFalse(cpu.isSupervisorMode());
		assertEquals(0, cpu.getInterruptLevel());
	}

	public void testException()
	{
		bus.writeLong(0x08, 0x56789);
		bus.writeLong(0x0c, 0x12345);
		bus.writeLong(0x10, 0x23456);
		cpu.setPC(0x32);
		cpu.setSR(0x04);
		cpu.setAddrRegisterLong(7, 0x0100);
		cpu.setSSP(0x0200);
		cpu.raiseException(2);
		assertEquals("pc", 0x56789, cpu.getPC());
		assertEquals("a7", 0x01fa, cpu.getAddrRegisterLong(7)); // 0x0200 - push pc + push sr (6 bytes)
		assertEquals("usp", 0x0100, cpu.getUSP());
		assertTrue("supervisor", cpu.isSupervisorMode());
		int sr = cpu.popWord();
		int pc = cpu.popLong();
		assertEquals(0x04, sr);
		assertEquals(0x32, pc);
		cpu.setSR(sr);
		assertFalse(cpu.isSupervisorMode());
		assertEquals(0x0100, cpu.getUSP());
		assertEquals(0x0100, cpu.getAddrRegisterLong(7));
		assertEquals(0x0200, cpu.getSSP());
	}

	public void testExceptionsNested()
	{
		bus.writeLong(0x08, 0x56789);
		bus.writeWord(0x56789, 0x4e73);
		bus.writeLong(0x14, 0x66789); // irq5
		bus.writeWord(0x66789, 0x4e73); // irq5
		bus.writeLong(0x04, 0x76789); // irq5
		bus.writeWord(0x76789, 0x4278); // irq1
		bus.writeWord(0x7678b, 0x850); // irq1
		bus.writeWord(0x7678d, 0x4e73); // irq1
		bus.writeLong(0x0c, 0x12345);
		bus.writeLong(0x10, 0x23456);
		cpu.setPC(0x32);
		cpu.setSR(0x04);
		cpu.setAddrRegisterLong(7, 0x0100);
		assertEquals(0x0100, cpu.getUSP());
		cpu.setSSP(0x0300);
		cpu.raiseException(5);
		assertEquals("pc", 0x66789, cpu.getPC());
		assertEquals("a7", 0x02fa, cpu.getAddrRegisterLong(7)); // 0x0200 - push pc + push sr (6 bytes)
		assertEquals("usp", 0x0100, cpu.getUSP());
		assertTrue("supervisor", cpu.isSupervisorMode());

		cpu.raiseException(2);
		assertEquals("pc", 0x56789, cpu.getPC());
		assertTrue("supervisor", cpu.isSupervisorMode());

		cpu.raiseException(1);
		assertEquals("pc", 0x76789, cpu.getPC());
		assertTrue("supervisor", cpu.isSupervisorMode());
		cpu.execute();
		assertEquals("pc", 0x7678d, cpu.getPC());

		// rte
		cpu.execute();
		cpu.execute();
		cpu.execute();

		assertFalse(cpu.isSupervisorMode());
		assertEquals(0x32, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertEquals(0x0100, cpu.getUSP());
		assertEquals(0x0100, cpu.getAddrRegisterLong(7));
		assertEquals(0x0300, cpu.getSSP());

		bus.writeWord(0x32, 0x4278); // irq1
		bus.writeWord(0x34, 0x851); // irq1
		cpu.execute();
		assertEquals(0x36, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertFalse(cpu.isSupervisorMode());
	}

	public void testInterruptsSequential()
	{
		bus.writeLong(0x68, 0x56789);
		bus.writeWord(0x56789, 0x4e73);
		bus.writeLong(0x74, 0x66789); // irq5
		bus.writeWord(0x66789, 0x4e73); // irq5
		bus.writeLong(0x64, 0x76789); // irq5
		bus.writeWord(0x76789, 0x4278); // irq1
		bus.writeWord(0x7678b, 0x850); // irq1
		bus.writeWord(0x7678d, 0x4e73); // irq1
		bus.writeLong(0x0c, 0x12345);
		bus.writeLong(0x10, 0x23456);
		cpu.setPC(0x32);
		cpu.setSR(0x04);
		cpu.setAddrRegisterLong(7, 0x0100);
		assertEquals(0x0100, cpu.getUSP());
		cpu.setSSP(0x0300);
		cpu.raiseInterrupt(5);
		assertEquals("pc", 0x66789, cpu.getPC());
		assertEquals("a7", 0x02fa, cpu.getAddrRegisterLong(7)); // 0x0200 - push pc + push sr (6 bytes)
		assertEquals("usp", 0x0100, cpu.getUSP());
		assertTrue("supervisor", cpu.isSupervisorMode());

		// rte
		cpu.execute();

		cpu.raiseInterrupt(2);
		System.out.println(Integer.toHexString(cpu.getPC()));
		assertEquals("pc", 0x56789, cpu.getPC());
		assertTrue("supervisor", cpu.isSupervisorMode());

		//rte
		cpu.execute();

		cpu.raiseInterrupt(1);
		assertEquals("pc", 0x76789, cpu.getPC());
		assertTrue("supervisor", cpu.isSupervisorMode());
		cpu.execute();
		assertEquals("pc", 0x7678d, cpu.getPC());

		// rte
		cpu.execute();

		assertFalse(cpu.isSupervisorMode());
		assertEquals(0x32, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertEquals(0x0100, cpu.getUSP());
		assertEquals(0x0100, cpu.getAddrRegisterLong(7));
		assertEquals(0x0300, cpu.getSSP());

		bus.writeWord(0x32, 0x4278);
		bus.writeWord(0x34, 0x851);
		cpu.execute();
		assertEquals(0x36, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertFalse(cpu.isSupervisorMode());
	}

	public void testInterruptsNested()
	{
		bus.writeLong(0x68, 0x56789);
		bus.writeWord(0x56789, 0x4e73);
		bus.writeLong(0x74, 0x66789); // irq5
		bus.writeWord(0x66789, 0x4e73); // irq5
		bus.writeLong(0x64, 0x76789); // irq5
		bus.writeWord(0x76789, 0x4278); // irq1
		bus.writeWord(0x7678b, 0x850); // irq1
		bus.writeWord(0x7678d, 0x4e73); // irq1
		bus.writeLong(0x0c, 0x12345);
		bus.writeLong(0x10, 0x23456);
		cpu.setPC(0x32);
		cpu.setSR(0x04);
		cpu.setAddrRegisterLong(7, 0x0100);
		assertEquals(0x0100, cpu.getUSP());
		cpu.setSSP(0x0300);
		cpu.raiseInterrupt(2);
		assertEquals("pc", 0x56789, cpu.getPC());
		assertEquals("a7", 0x02fa, cpu.getAddrRegisterLong(7)); // 0x0200 - push pc + push sr (6 bytes)
		assertEquals("usp", 0x0100, cpu.getUSP());
		assertTrue("supervisor", cpu.isSupervisorMode());

		cpu.raiseInterrupt(5);
		System.out.println(Integer.toHexString(cpu.getPC()));
		assertEquals("pc", 0x66789, cpu.getPC());
		assertTrue("supervisor", cpu.isSupervisorMode());

		cpu.raiseInterrupt(2);
		assertEquals("pc", 0x66789, cpu.getPC());

		//rte
		cpu.execute();
		cpu.execute();

		cpu.raiseInterrupt(1);
		assertEquals("pc", 0x76789, cpu.getPC());
		assertTrue("supervisor", cpu.isSupervisorMode());
		cpu.execute();
		assertEquals("pc", 0x7678d, cpu.getPC());

		// rte
		cpu.execute();

		assertFalse(cpu.isSupervisorMode());
		assertEquals(0x32, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertEquals(0x0100, cpu.getUSP());
		assertEquals(0x0100, cpu.getAddrRegisterLong(7));
		assertEquals(0x0300, cpu.getSSP());

		bus.writeWord(0x32, 0x4278);
		bus.writeWord(0x34, 0x851);
		cpu.execute();
		assertEquals(0x36, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertFalse(cpu.isSupervisorMode());
	}

	public void testTrapInterruptNested()
	{
		bus.writeWord(0x30, 0x4e41);
		bus.writeWord(0x32, 0x4278);
		bus.writeLong(0x84, 0x11111);
		bus.writeWord(0x11111, 0x4e73);
		bus.writeLong(0x74, 0x22222); // irq5
		bus.writeWord(0x22222, 0x4278); // irq1
		bus.writeWord(0x22222, 0x850); // irq1
		bus.writeWord(0x22222, 0x4e73); // irq1
		cpu.setPC(0x30);
		cpu.setSR(0x04);
		cpu.setAddrRegisterLong(7, 0x0100);
		assertEquals(0x0100, cpu.getUSP());
		cpu.setSSP(0x0300);
		cpu.execute();
		assertEquals("pc", 0x11111, cpu.getPC());
		assertEquals("a7", 0x02fa, cpu.getAddrRegisterLong(7)); // 0x0200 - push pc + push sr (6 bytes)
		assertEquals("usp", 0x0100, cpu.getUSP());
		assertTrue("supervisor", cpu.isSupervisorMode());

		cpu.raiseInterrupt(5);
		System.out.println(Integer.toHexString(cpu.getPC()));
		assertEquals("pc", 0x22222, cpu.getPC());
		assertTrue("supervisor", cpu.isSupervisorMode());

		//rte
		cpu.execute();
		cpu.execute();

		assertFalse(cpu.isSupervisorMode());
		assertEquals(0x32, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertEquals(0x0100, cpu.getUSP());
		assertEquals(0x0100, cpu.getAddrRegisterLong(7));
		assertEquals(0x0300, cpu.getSSP());

		bus.writeWord(0x34, 0x851);
		cpu.execute();
		assertEquals(0x36, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertFalse(cpu.isSupervisorMode());
	}

	public void testInterrupt()
	{
		bus.writeWord(0x6e0c, 0x41f9);
		bus.writeLong(0x6e0e, 0x000805f0);
		bus.writeLong(0x74, 0xfffbc);
		bus.writeWord(0xfffbc, 0x4ef9);
		bus.writeLong(0xfffbe, 0x560e);
		bus.writeWord(0x560e, 0x4e73);
		cpu.setPC(0x6e0c);
		cpu.setSR(0x04);
		cpu.setAddrRegisterLong(7, 0x0100);
		assertEquals(0x0100, cpu.getUSP());
		cpu.setSSP(0x0300);
		cpu.execute();
		assertEquals("pc", 0x6e12, cpu.getPC());
		assertEquals("a7", 0x0100, cpu.getAddrRegisterLong(7)); // 0x0200 - push pc + push sr (6 bytes)
		assertEquals("usp", 0x0100, cpu.getUSP());
		assertFalse("supervisor", cpu.isSupervisorMode());

		cpu.raiseInterrupt(5);
		assertEquals("pc", 0xfffbc, cpu.getPC());
		cpu.execute();
		System.out.println(Integer.toHexString(cpu.getPC()));
		assertEquals("pc", 0x560e, cpu.getPC());
		assertTrue("supervisor", cpu.isSupervisorMode());

		//rte
		cpu.execute();
		cpu.execute();

		assertFalse(cpu.isSupervisorMode());
		assertEquals(0x6e16, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertEquals(0x0100, cpu.getUSP());
		assertEquals(0x0100, cpu.getAddrRegisterLong(7));
		assertEquals(0x0300, cpu.getSSP());

		bus.writeWord(0x6e16, 0x851);
		cpu.execute();
		assertEquals(0x6e1a, cpu.getPC());
		assertEquals(0x04, cpu.getSR());
		assertFalse(cpu.isSupervisorMode());
	}
}
