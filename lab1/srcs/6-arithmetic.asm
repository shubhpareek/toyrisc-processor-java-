	.data
a:
	1
d:
	3
n:
	7
	.text
main:
	load %x0, $a, x3
	load %x0, $d, x4
	load %x0, $n, x5
	addi %x0, 1, %x6
	bgt %x6, %x5 endl
	store %x3, 65535, %x0
loop:
	beq %x6, %x5, endl
	add %x3, %4, %x3
	sub %x0, %x6, %x7
	store %x3, 65535, %x7
	addi %x6, 1, %x6
	jmp loop
endl:
	end
