package processor.pipeline;
import configuration.Configuration;
import generic.*;
import processor.Clock;
import processor.Processor;

public class InstructionFetch  implements Element {
	
	Processor containingProcessor;
	IF_EnableLatchType IF_EnableLatch;
	IF_OF_LatchType IF_OF_Latch;
	EX_IF_LatchType EX_IF_Latch;
	
	public InstructionFetch(Processor containingProcessor, IF_EnableLatchType iF_EnableLatch, IF_OF_LatchType iF_OF_Latch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_EnableLatch = iF_EnableLatch;
		this.IF_OF_Latch = iF_OF_Latch;
		this.EX_IF_Latch = eX_IF_Latch;
	}
	
	public void performIF()
	{
		//System.out.println("ifetch");
		if(EX_IF_Latch.getIS_enable())
		{
		///	System.out.println("from ex if");
		}
		
		if(!IF_EnableLatch.isIF_Busy()){	
			if(IF_EnableLatch.isIF_enable() )
			{
			//	System.out.println("ifetch inside");
				//int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
				
				if (EX_IF_Latch.getIS_enable())
				{
					//System.out.println("ifetch exif");
					containingProcessor.getRegisterFile().setProgramCounter(EX_IF_Latch.getbranchPC());
					EX_IF_Latch.setIS_enable(false);
				}
				
				int currentPC = containingProcessor.getRegisterFile().getProgramCounter();
				//System.out.printf("pccurr=%d\n",currentPC);
				IF_OF_Latch.setpc(currentPC);
				int newInstruction = containingProcessor.getMainMemory().getWord(currentPC);
				
				//IF_OF_Latch.setInstruction(newInstruction);
				//System.out.printf("pcafset=%d\n",containingProcessor.getRegisterFile().getProgramCounter());
	//			IF_EnableLatch.setIF_enable(false);
	
				Simulator.getEventQueue().addEvent(
						new MemoryReadEvent(
								Clock.getCurrentTime() + Configuration.mainMemoryLatency,
								this,
								containingProcessor.getMainMemory(),
								currentPC)
				);

				containingProcessor.getRegisterFile().setProgramCounter(currentPC+1);

				IF_EnableLatch.setIF_Busy(true);				

				//IF_OF_Latch.setOF_enable(true);
			}
		}
	}

	@Override
	public void handleEvent(Event e) {
		if(IF_OF_Latch.isOF_Busy()) {

			e.setEventTime(Clock.getCurrentTime() + 1);
			Simulator.getEventQueue().addEvent(e);
		}
		else if (e.getEventType() == Event.EventType.MemoryResponse){
			MemoryResponseEvent event = (MemoryResponseEvent) e ;
			IF_OF_Latch.setInstruction(event.getValue());
			System.out.println("IF happened");
			IF_EnableLatch.setIF_Busy(false);
			IF_OF_Latch.setOF_enable(true);
		}
	}
}
