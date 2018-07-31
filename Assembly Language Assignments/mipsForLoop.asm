#Aaron Thompson
#takes in an array and prints the sum of all numbers in the array that are less than 10
	.data
nums:	.word 8, 15, 2, 5, 13, 1, 22, 10, 17, 9, 7, 12, 4
size:	.word 13
sum:	.word 0
newLine: .asciiz "\n"
	.text 
	
	la $s0, nums #load the base address of the array into $s0
	la $s1, size #load the address of size into $s1
	lw $s1, 0($s1) #Turn the address of $s1 into the actual value
	
	li $t0, 0 #put a zero in $t0 (which will be i)
	li $s2, 0 #put a zero in $s2 (which will be sum)
	
	#start of the for loop
top:	beq $s1, $t0, bottom
	
	sll $t2, $t0, 2 #multiply index by 4
	add $t3, $t2, $s0 #add base address 
	lw $t6, 0($t3) #load nums[i] into $t3 from it's address
	
	#if statement
	li $t5, 10
	slt $t4, $t6, $t5 #if nums[i] is less than 10, put a 1 in $t4
	bne $t4, 1, nestedBottom #if $t4 contains 0, skip the if statement 
	add $s2, $s2, $t6 #sum = sum + nums[i]
	sw $t6, 0($t3) #store a[i] back into main memory
	
	li $v0, 1 
	la $a0, ($t6)
	syscall #print nums[i]
	li $v0, 4
	la $a0, newLine	
	syscall #skip a line
nestedBottom:
	li $v0, 1
	la $a0, ($s2)
	syscall #print the sum
	li $v0, 4
	la $a0, newLine
	syscall #skip a line
	
	addi $t0, $t0, 1 #increment i
	j top #go back to the top of the loop	
bottom: 
