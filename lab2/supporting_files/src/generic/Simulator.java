package generic;

import java.io.FileInputStream;
import java.io.File;				//Import File class
import java.io.FileOutputStream;	//For writing in output file
import java.io.DataOutputStream;
import java.io.IOException;			//For file exception handling
import generic.Operand.OperandType;


public class Simulator {
		
	static FileInputStream inputcodeStream = null;
	static int codeStartingAddress;
	static String objectFile;
	public static void setupSimulation(String assemblyProgramFile, String objectProgramFile)
	{
		objectFile = objectProgramFile;
		int firstCodeAddress = codeStartingAddress = ParsedProgram.parseDataSection(assemblyProgramFile);
		ParsedProgram.parseCodeSection(assemblyProgramFile, firstCodeAddress);
		ParsedProgram.printState();
	}
	
	public static void assemble()
	{
		try
		{
			// FILE CREATION FOR INPUT OUTPUT
			File file = new File(objectFile);
			if (!file.exists())
			{
				file.createNewFile();
			}
			FileOutputStream objectFileFOS = new FileOutputStream(file);
			DataOutputStream objectFileDOS = new DataOutputStream(objectFileFOS);
			//STARTING ADDRESS TO BE WRITTEN ON HEADER
			objectFileDOS.writeInt(codeStartingAddress);
			//ITERATING THROUGH DATA FOR VALUES
			for(Integer d : ParsedProgram.data)
			{
				objectFileDOS.writeInt(d);
			}
			//CREATING REQUIRED VARIABLES , LIKE STARTINGADDRESS ,ENDADDRESS,INSTRUCTION READING AND VARIABLES FOR REGISTER/IMMEDIATE VALUES
			int addr = codeStartingAddress;
			int lastAddr = codeStartingAddress + ParsedProgram.code.size() - 1;
			int val, opcode, rs1, rs2, rd, imm, pc;
			//IF ERROR err will be true
			boolean err = false;
			Instruction i;
			//ITERATING ON ALL ADDRESSES IN CODE SECTION
			while(addr <= lastAddr)
			{
				
				if (err)
				{
					//END ITERATION IF ERROR
					break;
				}
				//TO GET INSTRUCTION AT PARTICULAR ADDRESSS
				i = ParsedProgram.getInstructionAt(addr);
				// REQUIRED INITIALISATION
				val = opcode = rs1 = rs2 = rd = imm = pc = 0;
				// SWITCH CASES FOR ALL POSSIBLE INSTRUCTIONS
				switch(i.getOperationType())
				{
					//R3 TYPE
					case add : 
					case sub : 
					case mul : 
					case div : 
					case and : 
					case or : 
					case xor : 
					case slt : 
					case sll : 
					case srl : 
					case sra :	{
								//GET OPCODE
								opcode = i.getOperationType().ordinal();
								//GET REG VALUES ACCORDINGLY
								rs1=i.getSourceOperand1().getValue();
								rs2=i.getSourceOperand2().getValue();
								rd=i.getDestinationOperand().getValue();
								//OUT OF BOUND CHECKING
								if(rs1<0)
								{
									rs1 = rs1 & 31;
									
								}
								if(rs2<0)
								{
									rs2 = rs2 & 31;
									
								}
								if(rd<0)
								{
									rd = rd & 31;
									
								}
								//SHIFTING BITS TO COMBINE AND STORE FINALLY IN VAL
								opcode<<=27;
								rs1 <<=22;
								rs2 <<=17;
								rd <<= 12;
								val = opcode | rs1 | rs2 | rd ;
									break;
							} 
					//R21 TYPE
					case addi :
					case subi :
					case muli :
					case divi :
					case andi :
					case ori :
					case xori :
					case slti :	{
								//GET OPCODE
								opcode = i.getOperationType().ordinal();
								//GET REG VALUES ACCORDINGLY
								rs1 = i.getSourceOperand1().getValue();
								imm = i.getSourceOperand2().getValue();
								//OUT OF BOUND CHECKING
								if(imm > 65365 || imm < -65366)
								{
									err = true;
									System.out.printf("error at PC %d : Immediate out of range of 17 bits signed integer\n",i.getProgramCounter());
									break;
								}
								rd = i.getDestinationOperand().getValue();
								val = 0;
								opcode <<= 27;
								rs1 <<= 22;
								rd <<= 17;
								//CONVERTING TO 17 BITS SIGNED INTEGER
								if (imm < 0)
								{
									imm = imm & 131071;
								}
								val = opcode | rs1 | rd | imm;
								break;
								}
					//R2I TYPE			
					case slli :
					case srli :
					case srai :
								//GET OPCODE
								opcode = i.getOperationType().ordinal();
								//GET REG VALUES ACCORDINGLY
								rs1 = i.getSourceOperand1().getValue();
								imm = i.getSourceOperand2().getValue();
								//OUT OF BOUND CHECKING
								if(imm > 65365 || imm < -65366)
								{
									err = true;
									System.out.printf("error at PC %d : Immediate out of range of 17 bits signed integer\n",i.getProgramCounter());
									break;
								}
								if (imm < 0)
								{
									err = true;
									System.out.printf("error at PC %d : Immediate cannot be less than zero in bit shift\n",i.getProgramCounter());
									break;
								}
								rd = i.getDestinationOperand().getValue();
								val = 0;
								opcode <<= 27;
								rs1 <<= 22;
								rd <<= 17;
								val = opcode | rs1 | rd | imm;
								break;
					//RI TYPE
					case jmp :
							{
								if (i.getDestinationOperand().getOperandType() == OperandType.Label)
								{
									//SET PROGRAM COUNTER
									pc = i.getProgramCounter();
									imm =  ParsedProgram.symtab.get(i.getDestinationOperand().getLabelValue()) - pc;
									//OUT OF BOUND CHECKING
									if(imm > 2097151 || imm < -2097152)
									{
										err = true;
										System.out.printf("error at PC %d : Immediate out of range of 22 bits signed integer\n",i.getProgramCounter());
										break;
									}
									//CONVERTING TO 22 BITS SIGNED INTEGER
									if(imm<0)
									{
										imm = imm & 4194303;
									}
									//GET OPCODE
									opcode = i.getOperationType().ordinal();
									opcode <<= 27;
									val = imm | opcode;
								}
								else
								{
										
									rd =  i.getDestinationOperand().getValue();
									//OUT OF BOUND CHECKING
									if(rd<0)
									{
										rd = rd & 31;
									}
									opcode = i.getOperationType().ordinal();
									opcode <<= 27;
									rd <<=22;
									val = rd | opcode;
								}
								break;
									
							}
					// R2I TYPE CONTROL FLOW			
					case beq :
					case bgt :
					case blt :
					case bne :
						 	{
						 		if (i.getDestinationOperand().getOperandType() == OperandType.Label)
						 		{
						 			//GET OPCODE
							 		opcode = i.getOperationType().ordinal();
							 		//GET REG VALUES ACCORDINGLY
									rs1=i.getSourceOperand1().getValue();
									rd=i.getSourceOperand2().getValue();
									//SET PROGRAM COUNTER
									pc = i.getProgramCounter();
									imm=ParsedProgram.symtab.get(i.getDestinationOperand().getLabelValue())-pc;
									//OUT OF BOUND CHECKING
									if(imm > 65365 || imm < -65366)
									{
										err = true;
										System.out.printf("error at PC %d : Immediate out of range of 17 bits signed integer\n",i.getProgramCounter());
										break;
									}
							 		if(rs1<0)
									{
										rs1 = rs1 & 31;
									}
									if(rd<0)
									{
										rd = rd & 31;
									}
									//CONVERTING TO 17 BITS SIGNED INTEGER
									if(imm<0)
									{
										imm = imm & 131071;
									}
									rs1 <<= 22;
									opcode <<= 27;
									rd <<= 17;
									val = opcode | rs1| rd | imm;
								}
								break;
						 	}
					//R2I TYPE MEMORY INSTRUCTION
					case load :
					case store :
							{
								//GET OPCODE
								opcode = i.getOperationType().ordinal();
								rs1 = i.getSourceOperand1().getValue();
								//OUT OF BOUNDS CHECKING
								if(rs1<0)
									{
										rs1 = rs1 & 31;
									}
									
								rd = i.getDestinationOperand().getValue();
								if(rd<0)
									{
										rd = rd & 31;
									}

								rs2 = i.getSourceOperand2().getValue();
								//CONVERTING TO 17 BITS SIGNED INTEGER
								if(rs2<0)
									{
										rs2 = rs2 & 131071;
									}
								rs1 <<= 22;
								opcode <<= 27;	
								rd <<= 17;
								val = opcode | rs1 | rd | rs2 ;
								break;
							}	 	
   					//RI TYPE
					case end:
							//GET OPCODE
							opcode = i.getOperationType().ordinal();
							opcode <<= 27;
							val = opcode;
							break;
				}
				//FOR WRITING ON FILE
				objectFileDOS.writeInt(val);
				//INCREMENTING ADDRESS FOR ITERATION
				addr++;
			}
			//FLUSH AND CLOSE STREAMS
			objectFileDOS.flush();
			objectFileDOS.close();
			objectFileFOS.close();
			if (err)
			{
				//No output file if error
				file.delete();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
