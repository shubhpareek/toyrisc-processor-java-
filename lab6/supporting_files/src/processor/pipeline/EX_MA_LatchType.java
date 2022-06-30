package processor.pipeline;
import generic.Instruction;

public class EX_MA_LatchType {
	
	boolean MA_enable;
	int alu_result;
	int x31_result;
	Instruction instruction;
	boolean NOP;
	boolean MA_Lock;
	boolean MA_Busy;	
	public EX_MA_LatchType()
	{
		MA_enable = false;
		NOP = false;
		MA_Lock = false;
		MA_Busy = false;		
	}


	public boolean isMA_enable() {
		return MA_enable;
	}
	
	public void setInstruction(Instruction inst) {
		instruction = inst;
	}

	public int getALU_result() {
		return alu_result;
	}

	public void setALU_result(int result) {
		alu_result = result;
	}
	
	public void setMA_enable(boolean mA_enable) {
		MA_enable = mA_enable;
	}
	
	public Instruction getInstruction() {
		return instruction;
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

	public boolean isMA_Locked() { return MA_Lock; }

	public void setMA_Lock(boolean ma_lock) { MA_Lock = ma_lock; }

	public void setMA_Busy(boolean ma_busy) { MA_Busy = ma_busy; }

	public boolean isMA_Busy() { return MA_Busy; }

}
