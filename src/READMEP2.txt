The following are the assumptions about the input:
- The input s-expressions are separated by $ in new line.
- The end of the file is $$ which is also present in a new line.
- Errors occur during the evaluation of lisp-expressions
- Error related to input are handled in part-1 of the project
- I am converting lower case alphabet to upper case.
- For numeric atoms, I am using integer variables. Size of any numeric atom has to fit the size of Integer.


Note - Result of Lisp-expression can be seen after pressing an enter after $ sign.
     - Errors during evaluation are printed after inputing $ sign
     - As few error messages are provided by the professor, rest are my own. We can different error messages for same kind of error. 
     
Example:
(PLUS 2 3)
$
> 5

(PLUS 1)
$
> error: too few arguments

(PLUS 1 A)
$
> error in eval function: A is atom of undefined type

(CONS 4 (A . B))
$
 > error in eval function : (A.B) is not a list (error message here can also be - trying to evaluate (A.B))


