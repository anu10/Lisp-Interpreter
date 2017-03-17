import java.io.IOException;

//Node class representing node of the parse tree
class Node {
	
	boolean isInner;
	String val;
	Node left;
	Node right;

	Node(boolean isInner, String val) {
		this.isInner = isInner;
		this.val = val;
		
		if(!val.equals("NIL")) {
			this.left = new Node(false,"NIL");
			this.right = new Node(false,"NIL");
		}
	}
}

public class LispParser {
	
	static LispScanner scanner;
	
	public static void main(String[] args) throws IOException {
		//scanner = new LispScanner();
		while(true){
			try {
				scanner = new LispScanner();
				scanner.init();
				startParsing();
			} catch (Exception e) {
				System.out.println(e.getMessage().toString());
			}
		}
	}
	
	//Start parsing the input
	private static void startParsing() throws Exception{
		Node root = parseExpr();
		System.out.print("> ");
		printExpr(root);
		
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

	private static Node parseExpr() throws Exception {
		
		Node root = null;
		
		
		
		//Create a leaf node corresponding to the atom and return it
		if(scanner.getCurrent().type==LispScanner.LITERAL_ATOM || 
				scanner.getCurrent().type==LispScanner.NUMERIC_ATOM) {
			root = new Node(false,scanner.getCurrent().value);
			scanner.MoveToNext();
		}
		
		//Parse the list of expressions and construct the subtree corresponding to that
		else if(scanner.getCurrent().type==LispScanner.OPEN_PARENTHESIS) {
			
			scanner.MoveToNext();
			Node cur = root;
			
			while(scanner.getCurrent().type!=LispScanner.CLOSING_PARENTHESIS) {
				if(scanner.getCurrent().type==LispScanner.EOF){
					System.exit(0);
				}
				else if(scanner.getCurrent().type == LispScanner.EOS){
					scanner.MoveToNext();
					throw new Exception("error: unexpected dollar");
				}else{
					if(scanner.getCurrent().type == LispScanner.DOT){
						scanner.MoveToNext();
						Node subtree = parseExpr();
						if(scanner.getCurrent().type == LispScanner.DOT){
							throw new Exception("error: unexpected dot");
						}else
							cur.right = subtree;
					}else{
						if(cur!=null){
							cur.right = new Node(true, "");
							cur = cur.right;
							Node subtree = parseExpr();
							cur.left = subtree;
						}
						else {
							root = new Node(true, "");
							cur = root;
							cur.left = parseExpr();
						}
					}
				}
				
			}
			
			scanner.MoveToNext();
		}
		
		//If token is neither an atom nor an Open parenthesis, it is an error
		else if(scanner.getCurrent().type==LispScanner.EOF){
			System.exit(0);
		}
		
		else if(scanner.getCurrent().type == LispScanner.EOS){
			scanner.MoveToNext();
			return parseExpr();
		}
		
		else if(scanner.getCurrent().type == LispScanner.CLOSING_PARENTHESIS)
			throw new Exception("ERROR: unexpected closing paranthesis without opening paranthesis");
		else if(scanner.getCurrent().type == LispScanner.DOT)
			throw new Exception("error: unexpected dot");
		
		//returning the node
		if(root!=null)
			return root;
		else
			return new Node(false,"NIL");
	}
}
