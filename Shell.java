import java.util.*;

class Shell {
	public static void main(String[] args) {
		String A = "echo a;ls;;";
		String B = "echo a b; ls|grep a |    grep e;echo   'abc \"jn\" \"cba'; echo 123;";
		String C = "echo a\"b c\"";
		String D = "";
		
		// check back quote before tokenization
		// e.g. echo "this is space: `echo " "`" -> echo "this is space:  "
		
		ArrayList<String> tokens = tokenize(B);
		System.out.println(tokens.toString());
		
		syn1(tokens, 0, tokens.size());
	}
	
	public static ArrayList<String> tokenize(String cmd){
		char c;
		ArrayList<String> tokens = new ArrayList<String>();
		StringBuilder currentWord = new StringBuilder();
		for(int i = 0; i < cmd.length(); i++){
			c = cmd.charAt(i);
			switch(c){
				case ' ':
				case '\t':
					if(currentWord.length() > 0){
						tokens.add(currentWord.toString());
						currentWord = new StringBuilder();
					}
					continue;
				case '\'':
				case '"':
					char c1 = c;
					char c2 = cmd.charAt(++i);
					while(c2 != c1){
						if(i == cmd.length()-1){
							System.exit(1);
						}
						currentWord.append(c2);
						c2 = cmd.charAt(++i);
					}
					tokens.add(currentWord.toString());
					currentWord = new StringBuilder();
					continue;
				case ';':
				case '<':
				case '>':
				case '|':
				case '\n':
					if(currentWord.length() > 0){
						tokens.add(currentWord.toString());
						currentWord = new StringBuilder();
					}
					tokens.add(Character.toString(c));
					currentWord = new StringBuilder();
					continue;
				default:
					currentWord.append(c);
			}
		}
		if(currentWord.length() > 0){
			tokens.add(currentWord.toString());
		}
		return tokens;
	}
	
	
	
	/*
	 * syn1 : sequence
	 * 	syn2
	 * 	syn2 ; syn1
	 */
	public static void syn1(ArrayList<String> tokens, int start, int end){
		if(start == end){
			return;
		}
		for(int i = start; i < end; i++){
			if(tokens.get(i).equals(";")){
				syn2(tokens, start, i);
				syn1(tokens, i+1, end);
				return;
			}
		}
		syn2(tokens, start, end);
	}
	
	/*
	 * syn2 : pipe
	 * 	syn3
	 * 	syn3 | syn2
	 */
	public static void syn2(ArrayList<String> tokens, int start, int end){
		if(start == end){
			return;
		}
		for(int i = start; i < end; i++){
			if(tokens.get(i).equals("|")){
				syn3(tokens, start, i);
				syn2(tokens, i+1, end);
				return;
			}
		}
		syn3(tokens, start, end);
		System.out.println();
	}
	
	/*
	 * syn3 : call
	 * 	?
	 * word [ < in ] [ > out ]
	 */
	public static void syn3(ArrayList<String> tokens, int start, int end){
		System.out.print("{");
		for(int i = start; i < end; i++){
			System.out.print(tokens.get(i)+" ");
		}
		System.out.print("}");
	}
	
}