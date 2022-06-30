package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class MemoryAccess {
	Processor containingProcessor;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	
	public MemoryAccess(Processor containingProcessor, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
	}
	
	public void performMA()
	{
		//TODO
		if(EX_MA_Latch.isMA_enable())
		{
			//to get the instruction we stored in ex_ma latch 
		 	Instruction instruction = EX_MA_Latch.getInstruction();
		 	// get alu result from ex_ma latch 
			int aluresult = EX_MA_Latch.getALU_result();
			// set alu result in ma_rw latch 
			MA_RW_Latch.setalu(aluresult);
			// get operation type so that we know if it's load or store
			OperationType op_type = instruction.getOperationType();
			switch(op_type)
			{
				// in case of load , we search location at aluresult so that we can use it in register write
				case load:
					int load_result = containingProcessor.getMainMemory().getWord(aluresult);
					MA_RW_Latch.setloadresult(load_result);
					break;
				// in this case we just store the particular value at aluresult
				case store:
					int val_store = containingProcessor.getRegisterFile().getValue(instruction.getSourceOperand1().getValue());
					containingProcessor.getMainMemory().setWord(aluresult, val_store);
					break;
				default:
					break;
			}
			MA_RW_Latch.setx31_result(EX_MA_Latch.getx31_result());
			MA_RW_Latch.setinstruction(instruction);
			MA_RW_Latch.setRW_enable(true);
			EX_MA_Latch.setMA_enable(false);
		}
	}

}
