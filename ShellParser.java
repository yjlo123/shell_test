import java.util.*;

class ShellParser {
	
	public static final String SEQUENCE_DELIMITER = ";";
	public static final String PIPE_DELIMITER = "|";
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		String command;
		
		do{
			System.out.print(">");
			command = sc.nextLine();
		}while(command.isEmpty());
		
		ShellParser sp = new ShellParser();
		sp.parseCommand(command);
			
		System.out.println("[Done]");
	}
	
	public void parseCommand(String line){
		assert line.length() > 0;
		checkUnexpectedToken(line);
		
		// TODO find quotation
		
		StringBuilder parsingResult = new StringBuilder();
		// sequence
		String[] commands = line.split(SEQUENCE_DELIMITER);
		for(int i = 0; i < commands.length; i++){
			String currentCmd = commands[i];
			// pipe
			parsingResult.append(parsePipe(currentCmd));
			if (i < commands.length-1){
				parsingResult.append(", ");
			}
		}
		System.out.println(parsingResult.toString());
		
	}
	
	private String parsePipe(String command){
		checkUnexpectedToken(command);
		if (command.contains(PIPE_DELIMITER)){
			int lastIndex = command.lastIndexOf(PIPE_DELIMITER);
			String pipeCommand = command.substring(0, lastIndex);
			String lastCommand = command.substring(lastIndex+1);
			if(lastCommand.length() == 0){
				// TODO Wait for more input.
				System.out.println("[WARN] pipe command incomplete");
			}
			return "Pipe(" + parsePipe(pipeCommand) + ", " + parseCall(lastCommand) + ")";
		}else{
			return parseCall(command);
		}
	}

	private String parseCall(String command){
		String[] cmd = command.trim().split(" ",2);
		String[] args;
		if(cmd.length > 1){
			args = cmd[1].split(" ");
		}else{
			args = new String[1];
		}
		return "Call("+cmd[0] + "() " + Arrays.toString(args)+")";
	}
	
	private void checkUnexpectedToken(String command){
		String firstChar = Character.toString(command.charAt(0));
		if(firstChar.equals(SEQUENCE_DELIMITER) || firstChar.equals(PIPE_DELIMITER)){
			// TODO raise exception
			System.out.println("[ERROR] syntax error near unexpected token");
			System.exit(-1);
		}
	}

}