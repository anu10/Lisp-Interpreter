The following are the assumptions about the input:
- The input s-expressions are separated by $ in new line.
- The end of the file is $$ which is also present in a new line.
- If a $ is present in a line containing s-expression then it will throw error
- If we find a $$ in a line containing s-expression, then it will also throw error
- I am converting lower case alphabet to upper case.

Errors can be due to presence of illegal dot and illegal dollar.
- If we find a dollar in between an s-expression it will throw illegal dollar error.
- If we find an extra dot in between a s-expression, it will throw illegal dot error.

Error due to identifier : 
- a literal can be followed by a number, code will throw illegal identifier error
- number can't be followed by alphabets, code will throw illegal identifier error

Note - Result of s-expression can be seen after pressing two enter after $ sign.

Example:
(2 3)
$

> (2.(3.NIL))

