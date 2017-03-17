import java.util.Scanner;

class Token {
	
	String value;
	String type;
	
	Token(String value, String type) {
		this.value = value;
		this.type = type;
	}
}

class LispScanner {
	private static Scanner scanner;
	private static String inputLine = null;
	private static int pos = 0;
	private Token current;
	
	public static final String NUMERIC_ATOM = "NumericAtom";
	public static final String LITERAL_ATOM = "LiteralAtom";
	public static final String OPEN_PARENTHESIS = "OpenParenthesis";
	public static final String CLOSING_PARENTHESIS = "ClosingParenthesis";
	public static final String EOF = "EOF";
	public static final String DOT = "DOT";
	public static final String EOS = "EOS";

	public void init() throws Exception {
		scanner = new Scanner(System.in);
		pos = 0;
		inputLine = scanner.nextLine();
		
		//if inputLine is empty, load
		while(inputLine.isEmpty() && scanner.hasNextLine()) {
			inputLine = scanner.nextLine();
		}
		current = getNextToken(scanner);
	}

	public Token getCurrent() {
		return current;
	}
	
	public void MoveToNext() throws Exception {
		current = getNextToken(scanner);
	}

	private static Token getNextToken(Scanner scanner) throws Exception{
		boolean startsWithNumber = false;
		boolean startsWithChar = false;
		boolean isError = false;
		int i;
		
		//if current line is null (for first call of getNextToken) or current line has been parsed completely 
		
		if(inputLine == null || inputLine.substring(pos).isEmpty()) {
			pos = 0;
			inputLine = scanner.nextLine();
			
			//if inputLine is empty, load
			while(inputLine.isEmpty() && scanner.hasNextLine()) {
				inputLine = scanner.nextLine();
			}
				
		}
		
		if(inputLine.equals("$$"))
			return new Token("$$",EOF);
		if(inputLine.equals("$")){
			if(scanner.hasNextLine())
				inputLine = scanner.nextLine();
			return new Token("$",EOS);
		}
		
		int startPos = pos;
		for(i=startPos; i<inputLine.length();i++) {
			
			if(inputLine.charAt(i)==' ' || inputLine.charAt(i)=='\n') {
				int firstSpaceIndex = i; 
				
				//capture all consecutive ' ', '\n'
				while(i<inputLine.length() && (inputLine.charAt(i)==' ' || inputLine.charAt(i)=='\n'))
					i++;
				
				// if current line has ended and we have found no token
				if(!startsWithChar && !startsWithNumber && i==inputLine.length()) {
					
					if(!scanner.hasNext()) {
						pos = i;
					}
					
					//reset inputLine to next line in input and reset pos/startPos/i to 0
					else {
						startPos = 0;
						pos = 0;
						inputLine = scanner.nextLine();
						
						while(inputLine.isEmpty() && scanner.hasNextLine()) {
							inputLine = scanner.nextLine();
						}
						
						i = -1;
						continue;
					}
				}
				
				else if(startsWithChar) {
					pos=i;
					return new Token(inputLine.substring(startPos, firstSpaceIndex),LITERAL_ATOM);
				}
				
				else if(startsWithNumber) {
					pos=i;
					if(!isError)
						return new Token(inputLine.substring(startPos, firstSpaceIndex),NUMERIC_ATOM);
					else
						throw new Exception("ERROR: Invalid token wrong identifier");
						
				}	
			}
				
			if(inputLine.charAt(i) == '(') {
				
				//if '(' is the first character to be parsed
				if(i==pos) {
					pos++;
					return new Token("(", OPEN_PARENTHESIS);
				}
				
				else {
					pos = i;
					if(startsWithNumber)  {
						if(!isError)
							return new Token(inputLine.substring(startPos, i),NUMERIC_ATOM);
						else
							throw new Exception("ERROR: Invalid token wrong identifier");							
						   
					}
					
					if(startsWithChar)
						return new Token(inputLine.substring(startPos, i),LITERAL_ATOM);
					
					pos = i+1;
					return new Token("(", OPEN_PARENTHESIS);
				}
			}

			else if(inputLine.charAt(i) == '.'){
				if(i==pos){
					pos++;
					return new Token(".", DOT);
				}
				else{
					pos = i;
					if(startsWithNumber) {
						if(!isError)
							return new Token(inputLine.substring(startPos, i),NUMERIC_ATOM);
						else
							throw new Exception("ERROR: Invalid token wrong identifier");
							
					}
					
					if(startsWithChar)
						return new Token(inputLine.substring(startPos, i),LITERAL_ATOM);
					
					pos = i+1;
					return new Token(".", DOT);
				}
			}
			
			else if(inputLine.charAt(i) == ')') {
				if(i==pos) {
					pos++;
					return new Token(")", CLOSING_PARENTHESIS);
				}
				else {
					pos = i;
					if(startsWithNumber) {
						if(!isError)
							return new Token(inputLine.substring(startPos, i),NUMERIC_ATOM);
						else
							throw new Exception("ERROR: Invalid token wrong identifier");
					}
					
					if(startsWithChar)
						return new Token(inputLine.substring(startPos, i),LITERAL_ATOM);
					
					pos = i+1;
					return new Token(")", CLOSING_PARENTHESIS);
				}
			}
			
			else if(inputLine.charAt(i)== '$'){
				if(i==pos) {
					pos++;
					return new Token("$", EOS);
				}
				else {
					pos = i;
					if(startsWithNumber) {
						if(!isError)
							return new Token(inputLine.substring(startPos, i),NUMERIC_ATOM);
						else
							throw new Exception("ERROR: Invalid token wrong identifier");
							
					}
					
					if(startsWithChar)
						return new Token(inputLine.substring(startPos, i),LITERAL_ATOM);
					
					pos = i+1;
					return new Token("$", EOS);
				}
			}
			
			else if((inputLine.charAt(i)>='A' && inputLine.charAt(i)<='Z') || (inputLine.charAt(i)>='a' && inputLine.charAt(i)<='z') ) {
				inputLine = inputLine.toUpperCase();
				if(!startsWithNumber && !startsWithChar) {
					startPos = i;
					startsWithChar = true;
				}
				else if(startsWithNumber) {
					isError = true;
				}
					
			}

			else if((inputLine.charAt(i)>='0' && inputLine.charAt(i)<='9') || inputLine.charAt(i)=='-') {
				if(!startsWithNumber && !startsWithChar) {
					startPos = i;
					startsWithNumber = true;
				}
			}

			else if(inputLine.charAt(i)=='+') {
				if(!startsWithNumber && !startsWithChar) {
					startPos = i+1;
					startsWithNumber = true;
				}
			}
		}

		pos = i;
		
		if(startsWithNumber) {
			if(!isError)
				return new Token(inputLine.substring(startPos, i),NUMERIC_ATOM);
			else
				throw new Exception("ERROR: Invalid token wrong identifier");
		}
		else
			return new Token(inputLine.substring(startPos, i),LITERAL_ATOM);
	}


}