package processor.pipeline;
import configuration.Configuration;
import generic.*;
import processor.Clock;
import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;

public class MemoryAccess implements Element{
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_IF_LatchType EX_IF_Latch;
	public EX_MA_LatchType EX_MA_Latch;
	public MA_RW_LatchType MA_RW_Latch;
	public Instruction instruction;

	public MemoryAccess(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_IF_LatchType eX_IF_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.IF_OF_Latch = iF_OF_Latch;
	}
	
	public void performMA()
	{
		//TODO
		if(EX_MA_Latch.isMA_Busy()){
			OF_EX_Latch.setEX_Busy(true);
		}
		else 
		{
			OF_EX_Latch.setEX_Busy(false);
			//System.out.println("MA");//would proceed if nop instruction
			if (EX_MA_Latch.isMA_Locked()) {
				//System.out.println("Locked RW");
				MA_RW_Latch.setRW_Lock(true);
				MA_RW_Latch.setinstruction(null);
				EX_MA_Latch.setMA_Lock(false);
			}
			else if(EX_MA_Latch.isMA_enable())
			{
				//to get the instruction we stored in ex_ma latch 
				instruction = EX_MA_Latch.getInstruction();
			 	// get alu result from ex_ma latch 
				int aluresult = EX_MA_Latch.getALU_result();
				// set alu result in ma_rw latch 
				MA_RW_Latch.setalu(aluresult);
				// get operation type so that we know if it's load or store
				OperationType op_type = instruction.getOperationType();
				
				/*if (op_type == null) {
					MA_RW_Latch.setRW_enable(true);
					//System.out.println("NULL");
				}*/
				//else {
				//System.out.println(op_type);
				MA_RW_Latch.setinstruction(instruction);
				switch(op_type)
				{
					// in case of load , we search location at aluresult so that we can use it in register write
					case load:
						Simulator.getEventQueue().addEvent
						(
							new MemoryReadEvent
								(
									Clock.getCurrentTime() + containingProcessor.getL1dCache().getCacheLatency(),
									this,
									containingProcessor.getL1dCache(),
									aluresult
								)
						);
						int load_result = containingProcessor.getMainMemory().getWord(aluresult);
						MA_RW_Latch.setloadresult(load_result);
						EX_MA_Latch.setMA_Busy(true);
						return;
						//break;
					// in this case we just store the particular value at aluresult
					case store:
						int val_store = containingProcessor.getRegisterFile().getValue(instruction.getSourceOperand1().getValue());
						Simulator.getEventQueue().addEvent
						(
							new MemoryWriteEvent
							(
								Clock.getCurrentTime() + containingProcessor.getL1dCache().getCacheLatency(),
								this,
								containingProcessor.getL1dCache(),
								aluresult,
								val_store
							)
						);
						containingProcessor.getMainMemory().setWord(aluresult, val_store);
						EX_MA_Latch.setMA_Busy(true);
						return;
						//break;
					default:
						break;
				}
				MA_RW_Latch.setx31_result(EX_MA_Latch.getx31_result());
				
				if (instruction.getOperationType().ordinal() == 29) {
					IF_EnableLatch.setIF_enable(false);
				} 

				MA_RW_Latch.setinstruction(instruction);
				MA_RW_Latch.setRW_enable(true);
				EX_MA_Latch.setMA_enable(false);
				//}
			}
		}
	}
	@Override
	public void handleEvent(Event e) 
	{
		if(e.getEventType() == Event.EventType.MemoryResponse) {
			MemoryResponseEvent event = (MemoryResponseEvent) e;
			int loadResult = event.getValue();
			MA_RW_Latch.setloadresult(loadResult);
			MA_RW_Latch.setinstruction(instruction);
			EX_MA_Latch.setMA_Busy(false);
			MA_RW_Latch.setRW_enable(true);
			OF_EX_Latch.setEX_Busy(false);
			EX_MA_Latch.setMA_enable(false);
		}
	}

}
