package processor.pipeline;
import generic.Instruction;

public class MA_RW_LatchType {
	
	boolean RW_enable;
	int alu;
	Instruction instruction;
	int loadresult;
	int x31_result;
	boolean NOP;
	boolean conflict;

	
	public MA_RW_LatchType()
	{
		RW_enable = false;
		NOP = false;
		conflict = false;
	}

	public boolean isRW_enable() {
		return RW_enable;
	}

	public void setRW_enable(boolean rW_enable) {
		RW_enable = rW_enable;
	}
	
	public void setloadresult(int result) {
		loadresult = result;
	}

	public int getloadresult() {
		return loadresult;
	}

	public int getalu() {
		return alu;
	}

	public void setalu(int result) {
		alu = result;
	}
	
	public Instruction getinstruction() {
		return instruction;
	}

	public void setinstruction(Instruction inst) {
		instruction = inst;
	}
	
	public void setx31_result(int result) {
		x31_result = result;
	}
	
	public int getx31_result() {
		return x31_result;
	}

	public boolean getIsNOP() {
		return NOP;
	}
	
	public void setIsNOP(boolean is_NOP) {
		NOP = is_NOP;
	}
	
	public void conflictresolved( boolean f)
	{
		conflict = f;
	}
	public boolean getconflict()
	{
		return conflict;
	}
	
}
