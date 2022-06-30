package generic;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import processor.Clock;
import processor.Processor;
import generic.Statistics;

public class Simulator {
		
	static Processor processor;
	static boolean simulationComplete;
	static EventQueue eventQueue;
		
	public static void setupSimulation(String assemblyProgramFile, Processor p)
	{
		Simulator.processor = p;
		loadProgram(assemblyProgramFile);
		eventQueue = new EventQueue();	
		simulationComplete = false;
	}
	
	static void loadProgram(String assemblyProgramFile)
	{
		/*
		 * TODO
		 * 1. load the program into memory according to the program layout described
		 *    in the ISA specification
		 * 2. set PC to the address of the first instruction in the main
		 * 3. set the following registers:
		 *     x0 = 0
		 *     x1 = 65535
		 *     x2 = 65535
		 */
		processor.getRegisterFile().setValue(0, 0);	//setting register values as asked in question
		processor.getRegisterFile().setValue(1, 65535);
		processor.getRegisterFile().setValue(2, 65535);
		InputStream inp = null;// input stream file to store assembly program contents 
		try
		{
			inp = new FileInputStream(assemblyProgramFile);
			DataInputStream obj = new DataInputStream(inp);// file for reading data of assembly program
			int location = -3;// variable for location in main memory at which we will store integer instructions from assembly program
			
			while(obj.available() > 0)// we will store till there is content in assembly program file
			{
				int inst = obj.readInt();//for reading integer instruction from assemblly program file 
				
				if(location == -3)//for setting program counter to code location in assembly program file
				{
					processor.getRegisterFile().setProgramCounter(inst);
					location = 0;//initialization
					continue;
				}
				else
				{
					processor.getMainMemory().setWord(location, inst);// storing integer instruction in main memory
				}
				
				location ++;// increment after storing 
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void simulate()
	{
		int cycle=0;
		int numofinst=0;
		
		while(simulationComplete == false)
		{
		
			processor.getRWUnit().performRW();
			//System.out.printf("sim pc %d \n",processor.getRegisterFile().getProgramCounter());
			processor.getMAUnit().performMA();
			//System.out.printf("sim pc %d \n",processor.getRegisterFile().getProgramCounter());
			processor.getEXUnit().performEX();
			//System.out.printf("sim pc %d \n",processor.getRegisterFile().getProgramCounter());			
			
			eventQueue.processEvents();
			
			processor.getOFUnit().performOF();
			//System.out.printf("sim pc %d \n",processor.getRegisterFile().getProgramCounter());			
			processor.getIFUnit().performIF();
			//System.out.printf("sim pc %d \n",processor.getRegisterFile().getProgramCounter());			
			Clock.incrementClock();

			cycle++;
			/*System.out.println("");
			System.out.println(processor.getRegisterFile().getValue(3));
			System.out.println(processor.getRegisterFile().getValue(4));
			System.out.println(processor.getRegisterFile().getValue(5));
			System.out.println(processor.getRegisterFile().getValue(6));
			System.out.println(processor.getRegisterFile().getValue(7));
			System.out.println(processor.getRegisterFile().getValue(8));
			System.out.println("cycle");
			System.out.println(cycle);*/
		}
		
		// TODO
		// set statistics
		Statistics.setNumberOfCycles(cycle);
	}
	
	public static void setSimulationComplete(boolean value)
	{
		simulationComplete = value;
	}
	
	public static EventQueue getEventQueue()
	{
		return eventQueue;
	}
}
