package generic;

import java.io.PrintWriter;

public class Statistics {
	
	// TODO add your statistics here
	static int numberOfInstructions;
	static int numberOfCycles;
	static int numberOfStalls;
	static int numberOfWrongBranches;
	

	public static void printStatistics(String statFile)
	{
		try
		{
			PrintWriter writer = new PrintWriter(statFile);
			
			writer.println("Number of instructions executed = " + numberOfInstructions);
			writer.println("Number of cycles taken = " + numberOfCycles);
			writer.println("Number of stalls because of data hazard taken = " + numberOfStalls);
			writer.println("Number of wrong branches taken = " + numberOfWrongBranches);
			
			// TODO add code here to print statistics in the output file
			
			writer.close();
		}
		catch(Exception e)
		{
			Misc.printErrorAndExit(e.getMessage());
		}
	}
	
	// TODO write functions to update statistics
	public static void setNumberOfInstructions(int numberOfInstructions) {
		Statistics.numberOfInstructions = numberOfInstructions;
	}
	public static int getNumberOfInstructions() {
		return Statistics.numberOfInstructions;
	}

	public static void setNumberOfCycles(int numberOfCycles) {
		Statistics.numberOfCycles = numberOfCycles;
	}
	public static void setNumberOfStalls(int x) {
		Statistics.numberOfStalls = x;
	}
	public static int getNumberOfStalls() {
		return Statistics.numberOfStalls;
	}
	public static void setNumberOfWrongBranches(int x) {
		Statistics.numberOfWrongBranches = x;
	}
	public static int getNumberOfWrongBranches() {
		return Statistics.numberOfWrongBranches;
	}
}
