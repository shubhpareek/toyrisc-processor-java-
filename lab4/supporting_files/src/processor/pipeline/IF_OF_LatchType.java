package processor.pipeline;
import generic.Instruction;
public class IF_OF_LatchType {
	
	boolean OF_enable;
	int instruction;
	int pc;
	int counter;
	
	public IF_OF_LatchType()
	{
		OF_enable = false;
		counter = -2;
	}

	public boolean isOF_enable() {
		return OF_enable;
	}

	public void setOF_enable(boolean oF_enable) {
		OF_enable = oF_enable;
	}

	public int getInstruction() {
		return this.instruction;
	}

	public void setInstruction(int instruction) {
		this.instruction = instruction;
	}
	
	public int getpc()
	{
		return pc;
	}
	
	public void setpc(int x)
	{
		pc=x;
	}
	
	public int getcounter()
	{
		return counter;
	}
	
	public void setcounter(int x)
	{
		counter=x;
	}
	
}
