	.data
n:
	5
l:
	2
	-1
	7
	5
	3
	.text
main:
	load %x0, $n, %x3
	add %x0, %x0, %x10
loop:
	beq %x3, %x0, endl
	subi %x3, 1, %x3
	load %x3, $l, %x4
	blt %x4, %x0, loop
	andi %x4, 1, %x5
	beq %x5, %x0, increment
	jmp loop
increment:
	addi %x10, 1, %x10		
	jmp loop	
endl:
	end
