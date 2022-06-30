package processor.pipeline;

import processor.Processor;

import java.util.Arrays;

import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;

public class Execute {
	Processor containingProcessor;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	EX_IF_LatchType EX_IF_Latch;
	IF_OF_LatchType IF_OF_Latch;
	IF_EnableLatchType IF_EnableLatch;

	public Execute(Processor containingProcessor, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, EX_IF_LatchType eX_IF_Latch, IF_OF_LatchType iF_OF_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performEX()
	{
	System.out.println("EX");
		//TODO
		if(OF_EX_Latch.getIsNOP())//proceed further if nop
		{	
			System.out.println("was nop");
			EX_MA_Latch.setMA_enable(true);
			EX_MA_Latch.setIsNOP(true);
		}
		else if(OF_EX_Latch.isEX_enable())
		{
			//to get the instruction we stored in ofex latch
			Instruction instruction = OF_EX_Latch.getInstruction();
			//for debbugging purpose
			//System.out.println(instruction);
			//to pass the same instruction in exma latch
			EX_MA_Latch.setInstruction(instruction);
			//to know the operation type so we can solve accordinglly
			OperationType op_type = instruction.getOperationType();
			//get op1 , op2 to calculate aluresult 
			int op1=OF_EX_Latch.getop1();
			int op2=OF_EX_Latch.getop2();
			System.out.
			printf("op1 -%d op2-%d\n",op1,op2);
			long ov1=op1;//2147483647
			long ov2=op2;//-2147483648

			long lowerint=-2147483648;
			long higherint=2147483647;
			long vari=lowerint<<32;
			
			int alu_result = 0;
			int immeVal = 0;
			int rem = 0;
			
			
			if (op_type == null) 
			{
				EX_MA_Latch.setMA_enable(true);
				System.out.println("NULL");
			}
			else
			{
				System.out.println(op_type);
				switch(op_type)
				{
					case add:
						alu_result = op1 + op2;
						ov1 = ov1 +ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case addi:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 + immeVal;
						ov2= immeVal;
						ov1 = ov1 +ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case sub:
						alu_result = op1 - op2;
						ov1 = ov1 - ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case subi:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 - immeVal;
						ov2=immeVal;
						ov1 = ov1 -ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case mul:
						alu_result = op1 * op2;
						ov1 = ov1 * ov2 ;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case muli:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 * immeVal;
						ov2=immeVal;
						ov1 = ov1 * ov2 ;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case div:
						rem = op1 % op2;
						 
						//containingProcessor.getRegisterFile().setValue(31, rem);
						alu_result = op1 / op2;
						// remainder is stored in last register
						EX_MA_Latch.setx31_result(rem);
						break;
					case divi:
						immeVal=OF_EX_Latch.getimmediate();
						rem = op1 % immeVal;
						 
						//containingProcessor.getRegisterFile().setValue(31, rem);
						alu_result = op1 / immeVal;
						// remainder is stored in last register
						EX_MA_Latch.setx31_result(rem);
						break;
					case and:
						alu_result = op1 & op2;
						break;
					case andi:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 & immeVal;
						break;
					case or:
						alu_result = op1 | op2;
						break;
					case ori:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 | immeVal;
						break;
					case xor:
						alu_result = op1 ^ op2;
						break;
					case xori:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 ^ immeVal;
						break;
					case slt:
						if(op1 < op2)
							alu_result = 1;
						else
							alu_result = 0;
						break;
					case slti:
						immeVal=OF_EX_Latch.getimmediate();
						if(op1 < immeVal)
							alu_result = 1;
						else
							alu_result = 0;
						break;
					case sll:
						alu_result = op1 << op2;
						ov1 = ov1 << ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case slli:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 << immeVal;
						ov2=immeVal;
						ov1 = ov1 << ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case srl:
						alu_result = op1 >>> op2;
						ov1 = ov1 >>> ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case srli:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 >>> immeVal;
						ov2 = immeVal;
						ov1 = ov1 >>> ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case sra:
						alu_result = op1 >> op2;
						ov1 = ov1 >> ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case srai:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 >> immeVal;
						ov2 = immeVal;
						ov1 = ov1 >> ov2;
						if(ov1<lowerint||ov1>higherint)
						{
							ov1&=vari;
							ov1>>=32;
							EX_MA_Latch.setx31_result((int)(ov1));
						}
						break;
					case load:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op1 + immeVal;
						break;
					case store:
						immeVal=OF_EX_Latch.getimmediate();
						alu_result = op2 + immeVal;
						break;
					case jmp:
						EX_IF_Latch.setbranchPC(OF_EX_Latch.getbranchtarget());
						EX_IF_Latch.setIS_enable(true);
						break;
					case beq:
						if (op1 == op2)
						{
							EX_IF_Latch.setbranchPC(OF_EX_Latch.getbranchtarget());
							EX_IF_Latch.setIS_enable(true);
						}
						break;
					case bne:
						if (op1 != op2)
						{
							EX_IF_Latch.setbranchPC(OF_EX_Latch.getbranchtarget());
							EX_IF_Latch.setIS_enable(true);
						}
						break;
					case blt:
						if (op1 < op2)
						{
							EX_IF_Latch.setbranchPC(OF_EX_Latch.getbranchtarget());
							EX_IF_Latch.setIS_enable(true);
						}
						break;
					case bgt:
						if (op1 > op2)
						{
							EX_IF_Latch.setbranchPC(OF_EX_Latch.getbranchtarget());
							EX_IF_Latch.setIS_enable(true);
						}
						break;
					case end:
						break;
				}
				EX_MA_Latch.setALU_result(alu_result);
				//OF_EX_Latch.setEX_enable(false);
				EX_MA_Latch.setMA_enable(true);
				EX_MA_Latch.setIsNOP(false);
			}
		}
	}

}
