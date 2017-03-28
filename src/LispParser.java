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
	static boolean dd;
	
	public void init() throws Exception {
		scanner = new LispScanner();
		scanner.init();
	}
	
	public static Node parseExpr(boolean firstcall) throws Exception {
		
		Node root = null;
		
		//Create a leaf node corresponding to the atom and return it
		if(scanner.getCurrent().type==LispScanner.LITERAL_ATOM || 
				scanner.getCurrent().type==LispScanner.NUMERIC_ATOM) {
			root = new Node(false,scanner.getCurrent().value);
			dd = scanner.MoveToNext();
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
					dd=scanner.MoveToNext();
					throw new Exception("> **error: unexpected dollar**");
				}else{
					if(scanner.getCurrent().type == LispScanner.DOT){
						dd=scanner.MoveToNext();
						Node subtree = parseExpr(false);
						if(scanner.getCurrent().type == LispScanner.DOT){
							throw new Exception("> **error: unexpected dot**");
						}else
							cur.right = subtree;
					}else{
						if(cur!=null){
							cur.right = new Node(true, "");
							cur = cur.right;
							Node subtree = parseExpr(false);
							cur.left = subtree;
						}
						else {
							root = new Node(true, "");
							cur = root;
							cur.left = parseExpr(false);
						}
					}
				}
				
			}
			
			dd=scanner.MoveToNext();
		}
		
		//If token is neither an atom nor an Open parenthesis, it is an error
		else if(scanner.getCurrent().type==LispScanner.EOF){
			System.exit(0);
		}
		
		else if(scanner.getCurrent().type == LispScanner.EOS){
			dd=scanner.MoveToNext();
			return parseExpr(true);
		}
		
		else if(scanner.getCurrent().type == LispScanner.CLOSING_PARENTHESIS)
			throw new Exception("> **error: unexpected closing paranthesis without opening paranthesis**");
		else if(scanner.getCurrent().type == LispScanner.DOT)
			throw new Exception("> **error: unexpected dot**");

        if(firstcall && scanner.getCurrent().type!=LispScanner.EOF && scanner.getCurrent().type!=LispScanner.EOS) {
            throw new Exception("> **error: $ or $$ expected**");
        }
		//returning the node
		if(root!=null)
			return root;
		else
			return new Node(false,"NIL");
	}
}
