import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LispInterpreter {
	
	private static LispParser parser;
	private static HashSet<String> numericFunctionSet = new HashSet<String>(Arrays.asList("PLUS", "MINUS", "TIMES", "LESS", "GREATER", "QUOTIENT", "REMAINDER"));
	private static HashSet<String> keywords = new HashSet<String>(Arrays.asList("T", "NIL", "CAR", "CDR", "CONS", "ATOM", "EQ", "NULL", "INT", "PLUS", 
																					"MINUS", "TIMES", "LESS", "GREATER", "QUOTIENT", "REMAINDER","COND", "QUOTE", "DEFUN"));
	private static LinkedList<HashMap<String,Node>> aList;
	private static LinkedList<Node> dList = new LinkedList<Node>();
	
	public static void main(String[] args) throws Exception {
		while(LispParser.dd==false){
			try {
				parser = new LispParser();
				parser.init();
				startInterpreting();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}
		
	}
	
	//Start parsing the input
	private static void startInterpreting() throws Exception {
		
		if(LispParser.dd==false){
			//Construct parse tree for each top level expression
			Node root = LispParser.parseExpr(true);
			//Pretty-print the parse tree constructed above
			aList = new LinkedList<HashMap<String,Node>>();
			root = eval(root, aList, dList);
			if(root!=null) {
				System.out.print("> ");
				printExpr(root);
				System.out.println("\n");
			}
		}
		
	}
	
	//Returning the parse tree in string for top-level expression
	private static String getExpr(Node root) throws Exception {
		String res = "";
		if(root!=null) {
			if(root.isInner) {
				res += "(" + getExpr(root.left) + "."+ getExpr(root.right) + ")";
			}
			else
				return root.val ;
		}
		
		return res;
	}
		
	//Pretty printing the parse tree for top-level expression
	private static void printExpr(Node root) throws Exception {
		
		if(root!=null) {
			if(root.isInner) {
				System.out.print("(");
				printExpr(root.left);
				System.out.print(".");
				printExpr(root.right);
				System.out.print(")");
			}
			else
				System.out.print(root.val);
		}
	}
	
	
	private static boolean isList(Node node) {
		
		if(!node.isInner && !node.val.equals("NIL"))
			return false;
		
		if(null_(node).val.equals("T") || isList(node.right))
			return true;
		
		return false;
	}
	
	private static int length(Node node) {
		
		if(node.val.equals("NIL")&& !node.isInner)
			return 0;
		
		return 1+length(node.right);
	}
	
	private static int numNodes(Node node) {
		if(!node.isInner) {
			return 1;
		}
		
		return 1+numNodes(node.left)+numNodes(node.right);
	}
	
	private static Node car(Node s) throws Exception {
		if(numNodes(s)==1) {
			throw new Exception("> error in car : " + s.val + " has only one node");
		}
		
		return s.left;
	}
	
	private static Node cdr(Node s) throws Exception {
		if(numNodes(s)==1) {
			throw new Exception("> error in cdr :  " + s.val + " has only one node");
		}
		
		return s.right;
	}
	
	private static Node cons(Node s1, Node s2) {
		Node s = new Node(true, "");
		s.left=s1;
		s.right=s2;
		return s;
	}
	
	private static Node atom(Node s) {
		if(numNodes(s)==1) 
			return new Node(false, "T");
		return new Node(false,"NIL");
	}
	
	private static boolean isLiteralAtom(Node s) {
		if(numNodes(s)==1) {
			if(s.val.charAt(0)>='A' && s.val.charAt(0)<='Z')
				return true;
		}
		return false;
	}
	
	private static Node int_(Node s) {
		if(numNodes(s)==1) {
			try  
			{  
				Integer.parseInt(s.val);  
				return new Node(false, "T"); 
			}  
			catch( Exception e )  
			{  
				return new Node(false,"NIL");
			}  
		}
		return new Node(false,"NIL");
	}
	
	private static Node null_(Node s) {
		if(numNodes(s)==1) {
			if(s.val.equals("NIL")) {
				return new Node(false, "T"); 
			}
		}
		return new Node(false,"NIL");
	}
	
	private static Node eq(Node s1, Node s2) throws Exception {
		if(numNodes(s1)>1 || numNodes(s2)>1) {
			throw new Exception("> error in eq function: " + s1.val  + " or/and "+s2.val+" have more than one node");
		}
		
		if(s1.val.equals(s2.val))
			return new Node(false, "T"); 
		
		return new Node(false,"NIL");
	}
	
	private static Node plus(Node s1, Node s2) throws Exception {
		if(numNodes(s1)>1 || numNodes(s2)>1) {
			throw new Exception("> error in  plus function: " + s1.val + " or/and " + s2.val + " have more than one node");
		}
		
		return new Node(false, String.valueOf(Integer.valueOf(s1.val)+Integer.valueOf(s2.val)));
	}
	
	private static Node minus(Node s1, Node s2) throws Exception {
		if(numNodes(s1)>1 || numNodes(s2)>1) {
			throw new Exception("> error in minus function: " + s1.val + " or/and " + s2.val + " have more than one node");
			
		}
		
		return new Node(false, String.valueOf(Integer.valueOf(s1.val)-Integer.valueOf(s2.val)));
	}
	
	private static Node times(Node s1, Node s2) throws Exception {
		if(numNodes(s1)>1 || numNodes(s2)>1) {
			throw new Exception("> error in times function: " + s1.val + " or/and " + s2.val + " have more than one node");
		}
		
		return new Node(false, String.valueOf(Integer.valueOf(s1.val)*Integer.valueOf(s2.val)));
	}
	
	private static Node quotient(Node s1, Node s2) throws Exception {
		if(numNodes(s1)>1 || numNodes(s2)>1) {
			throw new Exception("> error in quotient function: " + s1.val + " or/and " + s2.val + " have more than one node");
		}
		
		if(Integer.valueOf(s2.val) != 0)
			return new Node(false, String.valueOf((Integer.valueOf(s1.val))/(Integer.valueOf(s2.val))));
		else 
			throw new Exception("> error in  quotient function: denominator is zero");
			
	}
	
	private static Node remainder(Node s1, Node s2) throws Exception {
		if(numNodes(s1)>1 || numNodes(s2)>1) {
			throw new Exception("> error in remainder function: " + s1.val + " or/and " + s2.val + " have more than one node");
		}
		
		if(Integer.valueOf(s2.val) != 0)
			return new Node(false, String.valueOf((Integer.valueOf(s1.val))%(Integer.valueOf(s2.val))));
		else 
			throw new Exception(" > error in remainder function: " + s2.val + " is zero");
			
	}
	
	private static Node less(Node s1, Node s2) throws Exception {
		if(numNodes(s1)>1 || numNodes(s2)>1) {
			throw new Exception("> error in less function: " + s1.val + " or/and " + s2.val + " have more than one node");
		}
		
		if(Integer.valueOf(s1.val)<Integer.valueOf(s2.val))
			return new Node(false, "T");
		
		return new Node(false, "NIL");
	}
	
	private static Node greater(Node s1, Node s2) throws Exception {
		if(numNodes(s1)>1 || numNodes(s2)>1) {
			throw new Exception("> error in greater function: " + s1.val + " or/and " + s2.val + " have more than one node");
		}
		
		if(Integer.valueOf(s1.val)>Integer.valueOf(s2.val))
			return new Node(false, "T");
		
		return new Node(false, "NIL");
	}
	
	private static boolean bound(String key, LinkedList<HashMap<String, Node>> aList) {
		
		for(HashMap<String, Node> pair : aList) {
			if(pair.containsKey(key))
				return true;
		}
		return false;
	}
	
	private static Node getVal(String key, LinkedList<HashMap<String, Node>> aList) {
		
		for(HashMap<String, Node> pair : aList) {
			if(pair.containsKey(key))
				return pair.get(key);
		}
		return null;
	}
	
	private static Node getValFromDList(String functionName, LinkedList<Node> dList) throws Exception {
		
		try{
			for(Node funDef : dList) {
				if(car(funDef).val.equals(functionName))
					return cdr(funDef);
			}
		}catch(Exception e){
			throw new Exception("> error: undefined function - " + functionName);
		}
		
		throw new Exception(" > error: undefined function - " + functionName + ", not present in dList");
	}
	
	private static Node apply(Node funName, Node actualParamList, LinkedList<HashMap<String, Node>> aList, LinkedList<Node> dList) throws Exception {
		
		addPairs(car(getValFromDList(funName.val, dList)), actualParamList, aList);
		Node retValue = eval(cdr(getValFromDList(funName.val, dList)), aList, dList);
		removePairs(car(getValFromDList(funName.val, dList)), actualParamList, aList);
		return retValue;
	}
	
	private static void addPairs(Node formalParamList, Node actualParamList, LinkedList<HashMap<String, Node>> aList) throws Exception {
		
		if(length(formalParamList) < length(actualParamList)) {
			throw new Exception("> too many arguments");
		}
		else if(length(formalParamList) > length(actualParamList)){
			throw new Exception("> too few arguments");
		}
		while(null_(formalParamList).val.equals("NIL")) {
			
			HashMap<String, Node> pair = new HashMap<String, Node>();
			pair.put(formalParamList.left.val, actualParamList.left);
			aList.addFirst(pair);
			
			formalParamList = formalParamList.right;
			actualParamList = actualParamList.right;
		}		
	}
	
	private static void removePairs(Node formalParamList, Node actualParamList, LinkedList<HashMap<String, Node>> aList) throws Exception {
		
		if(length(formalParamList) != length(actualParamList)) {
			throw new Exception("> error in apply of function : number of actual parameter expressions do not match the number of formal paramters");

		}
		
		while(null_(formalParamList).val.equals("NIL")) {
			
			HashMap<String, Node> pair = new HashMap<String, Node>();
			pair.put(formalParamList.left.val, actualParamList.left);
			aList.removeFirstOccurrence(pair);
			
			formalParamList = formalParamList.right;
			actualParamList = actualParamList.right;
		}		
	}
	
	private static Node evList(Node actualParamList, LinkedList<HashMap<String, Node>> aList, LinkedList<Node> dList) throws Exception {
		try{
			if(null_(actualParamList).val.equals("T")) {
				return actualParamList;
			}
			
			return cons(eval(car(actualParamList), aList, dList), evList(cdr(actualParamList), aList, dList));
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}
	}
	
	private static Node eval(Node s, LinkedList<HashMap<String, Node>> aList, LinkedList<Node> dList) throws Exception {
		
		if(numNodes(s)==1 && (s.val.equals("T") || s.val.equals("NIL") || int_(s).val.equals("T")))
			return s;
		
		if(numNodes(s)==1 && bound(s.val, aList))
			return getVal(s.val, aList);
		
		if(numNodes(s)==1) {
			throw new Exception("> error in eval function: " + getExpr(s) + " is atom of undefined type");
		}
		
		if(!isList(s)) {
			throw new Exception(" > error in eval function : " + getExpr(s) + " is not a list");
			
		}
		
		if(numericFunctionSet.contains(car(s).val)) {
			if(length(s) < 3) {
				throw new Exception("> error: too few arguments in "+ (car(s).val) + " function");
			}
			
			if(length(s) > 3) {
				throw new Exception(" > error: too many arguments in "+ (car(s).val) + " function");
			}
			
			Node s1 = eval(car(cdr(s)), aList, dList);
			Node s2 = eval(car(cdr(cdr(s))), aList, dList);
			
			if(!int_(s1).val.equals("T") || !int_(s2).val.equals("T") || isLiteralAtom(s1) || isLiteralAtom(s2)) {
				throw new Exception("> error in " + car(s).val + " function: function can be performed only on numeric atoms");
			}
			
			switch(car(s).val) {
				
				case "PLUS":
					return plus(s1, s2);
				
				case "MINUS":
					return minus(s1, s2);
					
				case "TIMES":
					return times(s1, s2);
					
				case "LESS":
					return less(s1, s2);
				
				case "GREATER":
					return greater(s1, s2);	
					
				case "QUOTIENT":
					return quotient(s1,s2);
					
				case "REMAINDER":
					return remainder(s1,s2);
			}
		}
		
		if(car(s).val.equals("EQ")) {
			if(length(s)!=3) {
				throw new Exception(" > error in " + car(s).val + " function: length of the lisp-expression is not equal to 3");
				
			}
			
			Node s1 = eval(car(cdr(s)), aList, dList);
			Node s2 = eval(car(cdr(cdr(s))), aList, dList);
			
			if(!atom(s1).val.equals("T") || !atom(s2).val.equals("T")) {
				throw new Exception("> error in " + car(s).val + " function: function can be performed only on numeric atoms");
				
			}
			
			return eq(s1, s2);
		}
		
		if(car(s).val.equals("ATOM") || car(s).val.equals("INT") || car(s).val.equals("NULL")) {
			if(length(s)!=2) {
				throw new Exception("> error in " + car(s).val + " function: length of the lisp-expression is not equal to 2");
				
			}
			
			Node s1 = eval(car(cdr(s)), aList, dList);
			
			switch(car(s).val) {
			
				case "ATOM":
					return atom(s1);
				
				case "INT":
					return int_(s1);
					
				case "NULL":
					return null_(s1);
			}
		}
		
		if(car(s).val.equals("CAR") || car(s).val.equals("CDR")) {
			if(length(s)!=2) {
				throw new Exception(" > error in " + car(s).val + " function: length of the lisp expression is not equal to 2");
				
			}
			
			Node s1 = eval(car(cdr(s)), aList, dList);
			
			switch(car(s).val) {
			
				case "CAR":
					return car(s1);
				
				case "CDR":
					return cdr(s1);
			}
		}
		
		if(car(s).val.equals("CONS")) {
			if(length(s)!=3) {
				throw new Exception("> error in " + car(s).val + " function: length of the lisp-expression is not equal to 3");
				
			}
			
			Node s1 = eval(car(cdr(s)), aList, dList);
			Node s2 = eval(car(cdr(cdr(s))), aList, dList);
			
			return cons(s1, s2);
		}
		
		if(car(s).val.equals("QUOTE")) {
			if(length(s)!=2) {
				throw new Exception("> error in " + car(s).val + " function: length of the lisp-expression is not equal to 2");
				
			}
			return car(cdr(s));
		}
		
		if(car(s).val.equals("COND")) {
			
			Node remaining = cdr(s);
			if(remaining.val.equals("NIL")){
				throw new Exception("> error in " + car(s).val + " too few arguments");
			}
			
			Node s1 = car(remaining);
			
			while(true) {
				if(!isList(s1)) {	
					throw new Exception("> error in  " + car(s).val + " : one of lisp-expressons is not a list");
					
				}
				
				if(length(s1)!=2) {
					throw new Exception(" > error in " + car(s).val + " : length of one of the lisp-expressions is not equal to 2");
					
				}
				
				Node b = car(s1);
				b = eval(b, aList, dList);
				
				if(!b.val.equals("NIL")) {
					Node e = car(cdr(s1));
					return eval(e, aList, dList);
				}
				
				if(null_(cdr(remaining)).val.equals("NIL")) {
					s1 = car(cdr(remaining));
					remaining = cdr(remaining);
				}
				
				else {
					throw new Exception(" > error in " + car(s).val + " : all conditions evaluating to NIL");
				
				}
			}
		}
		
		if(car(s).val.equals("DEFUN")) {
			if(length(s)!=4) {
				throw new Exception(" > error in " + car(s).val + " : length of list is not equal to 4");
			}
			
			Node funName = car(cdr(s));
			
			if(!isLiteralAtom(funName)) {
				throw new Exception(" > error in " + car(s).val + " : function name is not a literal atom");
				
			}
			
			if(keywords.contains(funName.val)) {
				throw new Exception("> error in " + car(s).val + " : function name cannot be " + funName.val);
				
			}
			
			Node paramList = car(cdr(cdr(s)));
			Node paramListCurr = paramList;
			Node body = car(cdr(cdr(cdr(s))));
			
			if(!isList(paramList)) {
				throw new Exception(" > error in " + car(s).val + " : formal parameter list is not a list");
				
			}
			
			HashSet<String> paramNameSet = new HashSet<String>();
			
			while(null_(paramListCurr).val.equals("NIL")) {
				
				if(!isLiteralAtom(car(paramListCurr))) {
					throw new Exception("> error in " + car(s).val + " : formal paramter is not a literal atom");
					
				}
				
				if(keywords.contains(car(paramListCurr).val)) {
					throw new Exception("> error in " + car(s).val + " : formal paramter name cannot be " + car(paramListCurr).val);
					
				}
				
				if(paramNameSet.contains(car(paramListCurr).val)) {
					throw new Exception("> error in " + car(s).val + " : duplicate formal paramter name found");
					
				}
				
				paramNameSet.add(car(paramListCurr).val);
				paramListCurr = paramListCurr.right;
			}
			
			
			Node funDefn = new Node(true, "");
			funDefn.left = funName;
			funDefn.right = new Node(true, "");
			funDefn.right.left = paramList;
			funDefn.right.right = body;
			dList.add(funDefn);
			
			return new Node(false,funName.val);

		}
		
		return apply(car(s), evList(cdr(s), aList, dList), aList, dList);
	}
}
