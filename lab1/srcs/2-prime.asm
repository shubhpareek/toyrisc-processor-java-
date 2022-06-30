	.data
a:
	3
	.text
main:
	load %x0, $a, %x3
	addi %x0, 2, %x4
	blt %x3, %x4, notPrime
	srli %x3, 1, %x5
loop:
	bgt %x4, %x5, prime
	div %x3, %x4, %x6
	beq %x31, %x0, notPrime
	addi %x4, 1, %x4
	jmp loop
prime:
	addi %x0, 1, %x10
	end
notPrime:
	subi %x0, 1, %x10
	end
