package processor.pipeline;

import generic.Instruction;

public class OF_EX_LatchType {
	
	boolean EX_enable;
	boolean EX_Lock;
	boolean EX_Busy;
	int branchtarget;
	int op1;
	int op2;
	int immediate;
	Instruction instruction;
	boolean NOP;

	
	public OF_EX_LatchType()
	{
		EX_enable = false;
		EX_Lock = false;
		EX_Busy = false;
		NOP = false;
	}

	public boolean isEX_enable() {
		return EX_enable;
	}

	public void setEX_enable(boolean eX_enable) {
		EX_enable = eX_enable;
	}
	
	public void setbranchtarget(int branch)
	{
		branchtarget=branch;
	}
	
	public int getbranchtarget()
	{
		return branchtarget;
	}
	
	public void setop1(int op)
	{
		op1=op;
	}
	
	public void setop2(int op)
	{
		op2=op;
	}
	
	public int getop1()
	{
		return op1;
	}
	
	public int getop2()
	{
		return op2;
	}
	
	public void setimmediate(int imm)
	{
		immediate=imm;
	}
	
	public int getimmediate()
	{
		return immediate;
	}
	
	public void setInstruction(Instruction instruction) {
		this.instruction = instruction;
	}

	public Instruction getInstruction() {
		return this.instruction;
	}

	public boolean getIsNOP() {
		return NOP;
	}
	
	public void setIsNOP(boolean is_NOP) {
		NOP = is_NOP;
	}

	public void setEX_Busy(boolean ex_busy) {
		EX_Busy = ex_busy;
	}

	public boolean isEX_Busy() {
		return EX_Busy;
	}
	
	public boolean isEX_Locked() { 
		return EX_Lock; 
	}

	public void setEX_Lock(boolean ex_lock) {
		EX_Lock = ex_lock;
	}



}
