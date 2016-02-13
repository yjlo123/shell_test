import java.util.*;

class Shell {
	
	public static final char BACK_QUOTE = '`';
	
	public static void main(String[] args) {
		String A = "echo a;ls;;";
		String B = "echo a b; ls|grep a |    grep e;echo   'abc \"jn\" \"cba'; echo 123;";
		String C = "echo a\"b c\"";
		String D = ";;;;;";
		String E = "echo `echo 123`; echo `ls`";
		String F = "echo a";
		String G = "echo a;";
		String H = "echo a; ls;>a";
		
		// check back quote before tokenization
		// e.g. echo "this is space: `echo " "`" -> echo "this is space:  "
		String cmd = backquotedSubstitution(F);
		parseCommand(cmd);
	}
	
	public static void parseCommand(String cmd){
		ArrayList<String> tokens = tokenize(cmd);
		//print tokens
		System.out.println("==token== "+tokens.toString());
		syn1(tokens, 0, tokens.size());
	}
	
	public static String backquotedSubstitution(String cmd){
		if (cmd.indexOf(BACK_QUOTE) == -1){
			return cmd;
		}else{
			int fromIdx = cmd.indexOf(BACK_QUOTE);
			int toIdx = cmd.indexOf(BACK_QUOTE, fromIdx+1);
			if(toIdx != -1){
				// parse backquoted command
				String backquoteCmd = cmd.substring(fromIdx+1, toIdx);
				parseCommand(backquoteCmd);
				
				// TODO execute command and get result
				
				// substitution
				String newCmd = cmd.substring(0,fromIdx)+"[===]"+cmd.substring(toIdx+1);
				//System.out.println("[New command] "+newCmd);
				return backquotedSubstitution(newCmd);
			}else{
				// exception
				System.out.println("Backquote Error!");
			}
		}
		return "Error";
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
					if(tokens.size() > 0 && tokens.get(tokens.size() - 1).equals(";")){
						String unexpectedToken = "";
						if(c == ';' || c == '|'){
							switch(c){
								case ';':
									unexpectedToken = ";;";
									break;
								case '|':
									unexpectedToken = "|";
									break;
							}
							// TODO exception
							System.out.println("[Exception] unexpected token `" + unexpectedToken+"'");
							System.exit(1);
						}
					}
					// append to parsed tokens
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
				System.out.print("  -> ");
				syn3(tokens, start, i);
				syn2(tokens, i+1, end);
				return;
			}
		}
		System.out.print(" => ");
		syn3(tokens, start, end);
	}
	
	/*
	 * syn3 : call
	 * 	?
	 * word [ < in ] [ > out ]
	 */
	public static void syn3(ArrayList<String> tokens, int start, int end){
		int numOfArgs = end-start-1;

		// temp print {} as execution
		String app = tokens.get(start);
		String[] args = new String[numOfArgs];
		for(int i = 1; i<= numOfArgs; i++){
			args[i-1] = tokens.get(start+i);
		}
		runApp(app, args);
	}
	
	// runApp for debugging
	public static void runApp(String app, String[] argsArray){
		if(app.equals("echo")){
			echo(argsArray);
		}else{
			System.out.print(app+"[ ");
			for(int i = 0; i < argsArray.length; i++){
				System.out.print(argsArray[i]+" ");
			}
			System.out.println("]");
		}
	}
	
	
	// TEMP APP for debugging
	public static void echo(String args[]){
		StringBuilder str = new StringBuilder();
		for(int i = 0; i < args.length; i++){
			str.append(args[i]+" ");
		}
		System.out.println(str.toString().trim());
	}
	
}