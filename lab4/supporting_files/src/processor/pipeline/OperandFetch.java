package processor.pipeline;

import processor.Processor;
import generic.Instruction;
import generic.Instruction.OperationType;
import generic.Operand;
import generic.Operand.OperandType;
import generic.Statistics;

public class OperandFetch {
	Processor containingProcessor;
	IF_OF_LatchType IF_OF_Latch;
	OF_EX_LatchType OF_EX_Latch;
	EX_MA_LatchType EX_MA_Latch;
	MA_RW_LatchType MA_RW_Latch;
	IF_EnableLatchType IF_EnableLatch;
	EX_IF_LatchType EX_IF_Latch;
	//int counter=-2;
	
	public OperandFetch(Processor containingProcessor, IF_OF_LatchType iF_OF_Latch, OF_EX_LatchType oF_EX_Latch, EX_MA_LatchType eX_MA_Latch, MA_RW_LatchType mA_RW_Latch, IF_EnableLatchType iF_EnableLatch, EX_IF_LatchType eX_IF_Latch)
	{
		this.containingProcessor = containingProcessor;
		this.IF_OF_Latch = iF_OF_Latch;
		this.OF_EX_Latch = oF_EX_Latch;
		this.EX_MA_Latch = eX_MA_Latch;
		this.MA_RW_Latch = mA_RW_Latch;
		this.IF_EnableLatch = iF_EnableLatch;
		this.EX_IF_Latch = eX_IF_Latch;
		
	}
	public static boolean ishazard(Instruction instruction, int register1, int register2) {//this function checks for hazards
		if (instruction != null && instruction.getOperationType() != null) 
		{
			int opcode = instruction.getOperationType().ordinal();
			//control hazards
			if (opcode == 6 || opcode == 7   ){
				System.out.println("jmpcase");
				return true;
			}
			//data hazard checking
			if (opcode <= 28) 
			{
				if (instruction != null){
					
					int destinationRegister = instruction.getDestinationOperand().getValue();
					if (register1 == destinationRegister || register2 == destinationRegister) {
						System.out.println("realconflict");
						return true;
					}
				}
			}
		}
		return false;
	}
	public void performOF()
	{
		
		System.out.printf("of \n");
		if(IF_OF_Latch.isOF_enable())
		{
			//TODO
			//to get all enum values of all operation types in this array
			OperationType[] operationType = OperationType.values();	
			// integer instruction on which we will work on
			int instruction;
			if(EX_IF_Latch.getIS_enable())//means we are jumping program counter ,so that we don't get program hazard 
			{
				Statistics.setNumberOfWrongBranches(Statistics.getNumberOfWrongBranches() + 1);
				instruction = 0;// this is a nop instruction it adds x0 register to itself
			}
			else{ instruction = IF_OF_Latch.getInstruction();}
			
			int counter= IF_OF_Latch.getcounter(); 
			System.out.printf("of counter %d\n",counter);
			// just for debbugging
			//System.out.println(instruction); 
			
			// to get opcode , in unsigned because that's how it's used in "instruction.java" enum
			int opcode = instruction >>> 27; 
			// to get the type of operation
			OperationType operation = operationType[opcode];
			
			// temporary variable
			int value;
			
			//for returning the final instruction to of_ex_latch
			Instruction inst = new Instruction();
			
		/*	if (opcode == 24 || opcode == 25 || opcode == 26 || opcode == 27 || opcode == 28 ) {
				IF_EnableLatch.setIF_enable(false);
				print
			}*/
			
			boolean did_conflict = false;
			//these values will be used to check for hazards
			Instruction inst_ex = OF_EX_Latch.getInstruction();
			Instruction inst_ma = EX_MA_Latch.getInstruction();
			Instruction inst_rw= MA_RW_Latch.getinstruction();

			System.out.println(inst_ex);
			System.out.println(inst_ma);
			System.out.println(inst_rw);

			int registerno,registerno2;
			int pc = IF_OF_Latch.getpc();
			System.out.println("op fetch ghus gaye");
			System.out.printf("pc=%d\n",pc);
			switch(operation)
			{
				//R3-Type 
				case add:
				case sub:
				case mul:
				case div:
				case and:
				case or:
				case xor:
				case slt:
				case sll:
				case srl:
				case sra:
				
					//for storing rs1 register
					Operand rs1 = new Operand();
					rs1.setOperandType(OperandType.Register);
					// rs1 will be present in bit number 27 to 23 in instruction
					value=instruction << 5;
					value >>>= 27;
					rs1.setValue(value);
					registerno = value;

					// for storing rs2 register
					Operand rs2 = new Operand();
					rs2.setOperandType(OperandType.Register);
					// rs2 will be present in bit number 22 to 18 in instruction
					value=instruction << 10;
					value >>>= 27;
					rs2.setValue(value);
					registerno2=value;
					
					// for storing rd register 
					Operand rd = new Operand();
					rd.setOperandType(OperandType.Register);
					// rd will be present in bit number 17 to 13 in instruction
					value=instruction << 15;
					value >>>= 27;					
					rd.setValue(value);
					//hazard checking 
					if (ishazard(inst_ex, registerno, registerno2))
						did_conflict = true;
					if (ishazard(inst_ma, registerno, registerno2))
						did_conflict = true;
					if (ishazard(inst_rw, registerno, registerno2))
						did_conflict = true;
						
					if (did_conflict && counter == -2 ) {// counter implements bubbles
						Statistics.setNumberOfStalls(Statistics.getNumberOfStalls() + 1);
						IF_OF_Latch.setcounter(3);  ;
						IF_EnableLatch.setIF_enable(false);
						//IF_OF_Latch.setOF_enable(false);
						OF_EX_Latch.setIsNOP(true);
						//break;
					}
					else if (counter>0)
					{
						counter-=1;
						IF_OF_Latch.setcounter(counter);
						IF_EnableLatch.setIF_enable(false); 
					}
					else if (counter == 0)
					{	
						IF_OF_Latch.setcounter(-2);
						IF_EnableLatch.setIF_enable(true);
						OF_EX_Latch.setIsNOP(false);
					}

					
					//setting instruction class values accordingly
					inst.setOperationType(operationType[opcode]);
					inst.setSourceOperand1(rs1);
					inst.setSourceOperand2(rs2);
					inst.setDestinationOperand(rd);
					
					//op1 for aluresult
					OF_EX_Latch.setop1(containingProcessor.getRegisterFile().getValue(inst.getSourceOperand1().getValue()));
					//op2 for aluresult
					OF_EX_Latch.setop2(containingProcessor.getRegisterFile().getValue(inst.getSourceOperand2().getValue()));
					break;
					
				//RECHECK FOR CORRECTNESS OF BELOW CODE
				//RI-Type
				case jmp:
					Operand operand = new Operand();
					int immediiateValue = instruction << 10;
					immediiateValue >>= 10;
					int register = instruction << 5;
					register >>>= 27;
					if (register == 0)
					{
						operand.setOperandType(OperandType.Immediate);
						operand.setValue(immediiateValue);
					}
					else
					{
						operand.setOperandType(OperandType.Register);
						operand.setValue(register);
					}
					OF_EX_Latch.setbranchtarget(pc + register + immediiateValue);
					//setting instruction class values accordingly
					inst.setOperationType(operationType[opcode]);
					inst.setDestinationOperand(operand);
					break;
				case end:
					//setting instruction class values accordingly
					inst.setOperationType(operationType[opcode]);
					break;
				//R2I-Type
				case beq:
				case bne:
				case blt:
				case bgt:
					
					//Source operand 1 is rs1
					Operand rs1111 = new Operand();
					int registeer = instruction << 5;
					registeer >>>= 27;
					rs1111.setOperandType(OperandType.Register);
					rs1111.setValue(registeer);
					registerno = registeer;
					OF_EX_Latch.setop1(containingProcessor.getRegisterFile().getValue(registeer));
					
					//Source operand 2 is rd 
					Operand rdd = new Operand();
					registeer = instruction << 10;
					registeer >>>= 27;
					rdd.setOperandType(OperandType.Register);
					rdd.setValue(registeer);
					registerno2 = registeer;
					OF_EX_Latch.setop2(containingProcessor.getRegisterFile().getValue(registeer));
					//hazard checking 
					if (ishazard(inst_ex, registerno, registerno2))
						did_conflict = true;
					if (ishazard(inst_ma, registerno, registerno2))
						did_conflict = true;
					if (ishazard(inst_rw, registerno, registerno2))
						did_conflict = true;
					if (did_conflict && counter == -2 ) {// counter implements bubbles
						Statistics.setNumberOfStalls(Statistics.getNumberOfStalls() + 1);
						IF_OF_Latch.setcounter(3);  ;
						IF_EnableLatch.setIF_enable(false);
						//IF_OF_Latch.setOF_enable(false);
						OF_EX_Latch.setIsNOP(true);
						//break;
					}
					else if (counter>0)
					{
						counter-=1;
						IF_OF_Latch.setcounter(counter);
						IF_EnableLatch.setIF_enable(false);  
					}
					else if (counter == 0)
					{	
						IF_OF_Latch.setcounter(-2);
						IF_EnableLatch.setIF_enable(true);
						OF_EX_Latch.setIsNOP(false);
					}
					// Destination operand is immediate
					Operand immidiate = new Operand();
					int immediateValue = instruction << 15;
					immediateValue >>= 15;
					immidiate.setValue(immediateValue);
					immidiate.setOperandType(OperandType.Immediate);
					
					//System.out.println(inst);
					
					//setting instruction class values accordingly
					inst.setOperationType(operationType[opcode]);
					inst.setSourceOperand1(rs1111);
					inst.setSourceOperand2(rdd);
					inst.setDestinationOperand(immidiate);
					//Calculating and setting branch target
					OF_EX_Latch.setbranchtarget(pc + immediateValue);
					break;
				//addi subi muli divi andi ori xori slti slli srli srai load store
				default:
					//Source operand 1 is rs1
					Operand rs111 = new Operand();
					int registerr = instruction << 5;
					registerr >>>= 27;
					rs111.setOperandType(OperandType.Register);
					rs111.setValue(registerr);
					registerno = registerr;
					OF_EX_Latch.setop1(containingProcessor.getRegisterFile().getValue(registerr));
					if (ishazard(inst_ex, registerno, registerno))
						did_conflict = true;
					if (ishazard(inst_ma, registerno, registerno))
						did_conflict = true;
					if (ishazard(inst_rw, registerno, registerno))
						did_conflict = true;
					// hazard checking
					if (did_conflict && counter == -2 ) {// counter implements bubbles
						Statistics.setNumberOfStalls(Statistics.getNumberOfStalls() + 1);
						IF_OF_Latch.setcounter(6);  ;
						IF_EnableLatch.setIF_enable(false);
						//IF_OF_Latch.setOF_enable(false);
						OF_EX_Latch.setIsNOP(true);
						//break;
					}
					else if (counter>0)
					{
						counter-=1;
						IF_OF_Latch.setcounter(counter); 
						IF_EnableLatch.setIF_enable(false); 
					}
					else if (counter == 0)
					{	
						IF_OF_Latch.setcounter(-2);
						IF_EnableLatch.setIF_enable(true);
						OF_EX_Latch.setIsNOP(false);
					}
					//Source operand 2 is immediate
					Operand immediate = new Operand();
					int immeediateValue = instruction << 15;
					immeediateValue >>= 15;
					immediate.setOperandType(OperandType.Immediate);
					immediate.setValue(immeediateValue);
					OF_EX_Latch.setimmediate(immeediateValue);
					
					//Destination operand is rd
					Operand rde = new Operand();
					registerr = instruction << 10;
					registerr >>>= 27;
					rde.setOperandType(OperandType.Register);
					rde.setValue(registerr);
					OF_EX_Latch.setop2(containingProcessor.getRegisterFile().getValue(registerr));
					
					//setting instruction class values accordingly
					inst.setOperationType(operationType[opcode]);
					inst.setSourceOperand1(rs111);
					inst.setSourceOperand2(immediate);
					inst.setDestinationOperand(rde);
					break;
			}
			//IF_OF_Latch.setOF_enable(false);
			OF_EX_Latch.setEX_enable(true);
			OF_EX_Latch.setInstruction(inst);
		}
		System.out.println("ofex enable status");
		System.out.println(OF_EX_Latch.isEX_enable());
		System.out.println("ofex nop status ");
		System.out.println(OF_EX_Latch.getIsNOP());
		
		System.out.println("ifetch status ");
		System.out.println(IF_EnableLatch.isIF_enable());
	}

}
