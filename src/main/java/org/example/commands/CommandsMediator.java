package org.example.commands;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CommandsMediator {
    private CommandFactory commandFactory;
    private static volatile CommandsMediator instance;
    private CommandsMediator(){
        commandFactory=new CommandFactory();
    }
    public JSONArray execute(JSONObject commandJson){
        CommandTypes commandType= CommandUtils.getCommandType(commandJson);
        Command command=commandFactory.createCommand(commandType);
        return command.execute(commandJson);
    }
    public static CommandsMediator getInstance() {
        CommandsMediator result = instance;
        if (result != null) {
            return result;
        }
        synchronized(CommandsMediator.class) {
            if (instance == null) {
                instance = new CommandsMediator();
            }
            return instance;
        }
    }
}
