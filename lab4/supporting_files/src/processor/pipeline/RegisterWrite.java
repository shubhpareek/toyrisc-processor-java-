package processor.pipeline;

import generic.Simulator;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Statistics;

public class RegisterWrite {
	Processor containingProcessor;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	
	public RegisterWrite(Processor containingProcessor, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
	}
	
	public void performRW()
	{	
	System.out.println("RW");
		if (MA_RW_Latch.getIsNOP()) {//proceed further if nop instruction
			MA_RW_Latch.setIsNOP(false);
			System.out.println("got in nop in RW");
			//IF_EnableLatch.setIF_enable(true);
			MA_RW_Latch.conflictresolved(true);
		} 
		else if(MA_RW_Latch.isRW_enable())
		{
			//TODO
			
			Instruction instruction = MA_RW_Latch.getinstruction();
			int loadResult=0, destinationReg=0, aluResult=0, x31Result=0;
			Statistics.setNumberOfInstructions(Statistics.getNumberOfInstructions() + 1);
			// if instruction being processed is an end instruction, remember to call Simulator.setSimulationComplete(true);
			
			System.out.println(instruction.getOperationType());
			switch(instruction.getOperationType())
			{
				case add:
				case addi:
				case sub:
				case subi:
				case mul:
				case muli:
				case div:
				case divi:
				case sll:
				case slli:
				case srl:
				case srli:
				case sra:
				case srai:
					//Get the register number
					destinationReg = instruction.getDestinationOperand().getValue();
					//Get the value calculated by alu
					aluResult = MA_RW_Latch.getalu();
					//Get the value calculated by alu for x31
					x31Result = MA_RW_Latch.getx31_result();
					//Write the values in the registers
					containingProcessor.getRegisterFile().setValue(destinationReg, aluResult);
					containingProcessor.getRegisterFile().setValue(31, x31Result);
					IF_EnableLatch.setIF_enable(true);
					break;
				case end:
					// simulation completed
					Simulator.setSimulationComplete(true);
					int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
					containingProcessor.getRegisterFile().setProgramCounter(currentPC-1);
					break;
				case load:
					// load the data from memory which will go to the destination register
					loadResult = MA_RW_Latch.getloadresult();
					//Get the register number
					destinationReg = instruction.getDestinationOperand().getValue();
					//Write the value in the register
					containingProcessor.getRegisterFile().setValue(destinationReg, loadResult);
					IF_EnableLatch.setIF_enable(true);
					break;
				case and:
				case andi:
				case or:
				case ori:
				case xor:
				case xori:
				case slt:
				case slti:
					//Get the register number
					destinationReg = instruction.getDestinationOperand().getValue();
					//Get the value calculated by alu
					aluResult = MA_RW_Latch.getalu();
					//Write the value in the register
					containingProcessor.getRegisterFile().setValue(destinationReg, aluResult);
					IF_EnableLatch.setIF_enable(true);
					break;
								//Nothing to do in these cases
				//store, jmp, beq, bne, blt, bgt Nothing to do
				default:
					IF_EnableLatch.setIF_enable(true);
					break;
			}
			MA_RW_Latch.conflictresolved(false);
			//MA_RW_Latch.setRW_enable(false);
			
				
		}
	}

}
