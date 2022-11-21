package org.example.commands;
import org.example.commands.TCP.*;
import org.example.commands.UPD.InitializeCommand;
import org.example.commands.UPD.RedirectCommand;
import org.example.commands.UPD.SyncCommand;

import org.example.commands.UPD.AddUserCommand;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private Map<CommandTypes,Command> commandsMap;
    public CommandFactory(){
        commandsMap=new HashMap<>();
        //TCP COMMANDS
        commandsMap.put(CommandTypes.CREATE_COLLECTION,new CreateCollectionCommand());
        commandsMap.put(CommandTypes.CREATE_DATABASE,new CreateDatabaseCommand());
        commandsMap.put(CommandTypes.CREATE_DOCUMENT,new CreateDocumentCommand());
        commandsMap.put(CommandTypes.CREATE_INDEX,new CreateIndexCommand());
        commandsMap.put(CommandTypes.DELETE_COLLECTION,new DeleteCollectionCommand());
        commandsMap.put(CommandTypes.DELETE_DATABASE,new DeleteDatabaseCommand());
        commandsMap.put(CommandTypes.DELETE_DOCUMENT,new DeleteDocumentCommand());
        commandsMap.put(CommandTypes.FIND_ALL,new FindAllCommand());
        commandsMap.put(CommandTypes.FIND,new FindCommand());
        commandsMap.put(CommandTypes.SYNC_UPDATE_DOCUMENT,new SyncUpdateDocumentCommand());
        commandsMap.put(CommandTypes.UPDATE_DOCUMENT,new UpdateDocumentCommand());
        commandsMap.put(CommandTypes.PING,new PingCommand());
        //UPD COMMANDS
        commandsMap.put(CommandTypes.ADD_USER,new AddUserCommand());
        commandsMap.put(CommandTypes.INITIALIZE,new InitializeCommand());
        commandsMap.put(CommandTypes.REDIRECT,new RedirectCommand());
        commandsMap.put(CommandTypes.SYNC,new SyncCommand());
    }
    public Command createCommand(CommandTypes commandType){
        return commandsMap.get(commandType);
    }
}
