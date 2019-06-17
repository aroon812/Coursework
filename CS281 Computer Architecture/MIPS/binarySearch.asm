 #Aaron Thompson
 #an implementation of binary search in MIPS
 .data
nums:   .word 5, 8, 12, 14, 16, 19, 23, 28, 35, 39, 41, 43, 44, 52, 55, 58, 66, 72, 74, 76, 81
length: .word 21
str:    .asciiz "Searching from index "
to:     .asciiz " to "
endl:   .asciiz "\n"

        .text
        #
        # This code loads arguments into $a registers and calls the search routine.
        # Once we get back from sorting it prints the returned value and exits.
        #
main:
        li $a0, 0       # left index value
        la $t0, length  # size is right index value
        lw $a1, 0($t0)  
        la $a2, nums    # pass array's base address in $a2
        li $a3, 19      # value to search for in array
        # Args are all loaded into $a register -- time to jump to search procedure
        jal binary_search       
        # Now we're back, with the return value in $v0
        move $a0, $v0   # Move result into $a0 to print
        li $v0, 1
        syscall         # Print the result
        li $v0, 10      # syscall 10 is exit
        syscall         
    
        
# PROCEDURE:  print_status
#  Prints a line of output that describes current search region.  
#  NOTE: This routine alters the $a register values.
#
# Inputs:
#  $a0  Low index of search region
#  $a1  High index of search region
# 
# Outputs:
#  None
        
print_status:
        addi $sp, $sp, -8   # Make room for two words on stack
        sw   $a0, 0($sp)    # Store initial $a0 value on stack
        sw   $a1, 4($sp)    # Store initial $a1 value on stack
        # Get on with the printing
        la $a0, str     # put address of str in $a0 for syscall
        li $v0, 4       # print string syscall #
        syscall         # print the bulk of the string
        lw $a0, 0($sp)  # bring $a0 (low) in from stack
        li $v0, 1       # print integer syscall #
        syscall
        la $a0, to      # put address of " to " string in $a0
        li $v0, 4       # print string syscall #
        syscall
        lw $a0, 4($sp)  # bring in $a1 (high) from stack, put in $a0
        li $v0, 1       # print integer syscall #
        syscall
        la $a0, endl    # put address of newline string in $a0
        li $v0, 4
        syscall
        addi $sp, $sp, 8
        jr $ra
        

# PROCEDURE:  binary_search
#  Searches for a specific value in an array using binary search.
#
# Inputs:
#  $a0  Index where search begins (inclusive)
#  $a1  Index where search ends (exclusive)
#  $a2  Base address of array
#  $a3  Value to search for
# 
# Outputs:
#  $v0  Contains the position within the array at which value occurs, or
#       where it *would* be located if it's not in the array currently.

binary_search:

       li $s0, 0 #"mid" is $s0
       addi $sp, $sp, -16 #adjust the stack pointer se we can save our registers to main memory
       sw $ra, 0($sp) #put $ra on the stack
       sw $s0, 4($sp) #put $s0 on the stack
       sw $a0, 8($sp) #put $a0 on the stack
       sw $a1, 12($sp)#put $a1 on the stack
       
       jal print_status #execute the print_status procedure 
       
       lw $a0, 8($sp)#take $a0 off of the stack
       lw $a1, 12($sp)#take $a1 off of the stack
       lw $ra 0($sp)#take $ra off of the stack
       lw $s0 4($sp)#take #s0 off of the stack
       addi $t1, $a0, 1 #setting $t1 to low + 1
       
       bne $t1, $a1, else #if low+1 == high, return low and exit the method
       move $v0, $a0 #copy "low" to the return register, which is $v0
       
       addi $sp, $sp, 16 #adjust the stack pointer back to the way we found it
       jr $ra #jump back to procedure which called binary_search

else: 
       add $t2, $a0, $a1 #$t2 is low + high
       srl $s0, $t2, 1 #set "mid" to (low+high)/2
       
       sll $t3, $s0, 2 #multiply the index(mid) by 4
       add $t3, $t3, $a2 #add the base address to get to the correct position in the array, so we get nums[mid] as $t3
       lw $t3, 0($t3)#turn the memory address of nums[mid] into the actual value
       
       #nested if statement to check which half the value would be in
       slt $t4, $a3, $t3 #if the value we are searching for is less than nums[mid], put a 1 in $t4
       bne $t4, 1, nestedElse #if the value is not equal to 1, execute the the nested else statement
       
       move $a1, $s0 #search left half by setting "high" to "mid"
       j binary_search #run binary_search again with the updated parameters
nestedElse: 
	move $a0, $s0 #search right half by setting "low" to "mid"
	j binary_search #run binary_search again with the updated parameters
       
	
        
        
