	.data
a:
	70
	80
	40
	20
	10
	30
	50
	60
n:
	8
	.text
main:
	load %x0, $n, %x3
	add %x0, %x0, %x4
iterate:
	addi %x4, 1, %x4
	beq %x4, %x3, endl
	add %x0, %x4, %x5
	add %x0, %x4, %x9
backiter:
	subi %x5, 1, %x5
	load %x9, $a, %x6
	load %x5, $a, %x7
	blt %x7, %x6, switch
	beq %x5, %x0, iterate
	jmp iterate
switch:
	add %x0, %x6, %x8
	add %x0, %x7, %x6
	add %x0, %x8, %x7
	store %x6, $a, %x9
	store %x7, $a, %x5
	add %x0, %x5, %x9
	beq %x5, %x0, iterate
	jmp backiter
endl:
	end
