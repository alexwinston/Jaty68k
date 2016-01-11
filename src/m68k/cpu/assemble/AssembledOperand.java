package m68k.cpu.assemble;

import m68k.cpu.DisassembledOperand;
import m68k.cpu.Size;

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
public class AssembledOperand extends DisassembledOperand
{
    public final AddressingMode mode;
    public final int register;

    public final int ext_register;
    public final int ext_data;
    public final Size ext_size;

    public final Conditional conditional;

    @Override
    public String toString() {
        return "DisassembledOperand{" +
                "bytes=" + bytes +
                ", memory_read=" + memory_read +
                ", operand='" + operand + '\'' +
                ", mode=" + mode +
                ", conditional=" + conditional +
                '}';
    }

    public AssembledOperand(String operand)
	{
        super(operand);

        mode = AddressingMode.NA;
        conditional = Conditional.NA;
        register = 0;
        ext_register = 0;
        ext_data = 0;
        ext_size = Size.Unsized;
	}

	public AssembledOperand(String operand, int bytes, int memory_read)
	{
        super(operand, bytes, memory_read);

        mode = AddressingMode.NA;
        conditional = Conditional.NA;
        register = 0;
        ext_register = 0;
        ext_data = 0;
        ext_size = Size.Unsized;

		if(bytes > 8)
			throw new IllegalArgumentException("Are these the wrong way around ?");
	}

    public AssembledOperand(String operand, int bytes, int memory_read,
                            AddressingMode mode, Conditional conditional, int register)
    {
        super(operand, bytes, memory_read);

        this.mode = mode;
        this.conditional = conditional;
        this.register = register;
        this.ext_register = 0;
        this.ext_data = 0;
        ext_size = Size.Unsized;

        if(bytes > 8)
            throw new IllegalArgumentException("Are these the wrong way around ?");
    }

    public AssembledOperand(String operand, int bytes, int memory_read,
                            AddressingMode mode, Conditional conditional, int register,
                            int ext_register, int ext_data, Size ext_size)
    {
        super(operand, bytes, memory_read);

        this.mode = mode;
        this.conditional = conditional;
        this.register = register;
        this.ext_register = ext_register;
        this.ext_data = ext_data;
        this.ext_size = ext_size;

        if(bytes > 8)
            throw new IllegalArgumentException("Are these the wrong way around ?");
    }
}
