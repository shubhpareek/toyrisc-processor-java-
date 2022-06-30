	.data
n:
	10
	.text
main:
	subi %x0, 1, %x3
	load %x0, $n, %x4
	beq %x4, %x0, endl
	store %x0, 65535, %x0
	addi %x0, 1, %x5
	beq %x4, %x5, endl
	store %x5, 65535, %x3
	subi %x4, 2, %x4
	beq %x4, %x0, endl
loop:
	beq %x0, %x4, endl
	subi %x3, 1, %x3
	addi %x3, 1, %x7
	addi %x3, 2, %x6
	load %x7, 65535, %x14
	load %x6, 65535, %x12
	add %x12, %x14, %x5
	store %x5, 65535, %x3
	subi %x4, 1, %x4
	jmp loop
endl:
	end
