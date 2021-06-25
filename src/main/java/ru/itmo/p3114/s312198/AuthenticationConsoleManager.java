package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.commands.CommandHashMap;
import ru.itmo.p3114.s312198.commands.CommandRecord;
import ru.itmo.p3114.s312198.commands.authentication.AuthenticationCommand;
import ru.itmo.p3114.s312198.exceptions.InitializationException;
import ru.itmo.p3114.s312198.exceptions.InvalidCommandException;
import ru.itmo.p3114.s312198.exceptions.NoSuchCommandException;
import ru.itmo.p3114.s312198.managers.ClientConsoleManager;
import ru.itmo.p3114.s312198.parsers.CommandParser;
import ru.itmo.p3114.s312198.transmission.AuthenticationRequest;

import java.io.IOException;

public class AuthenticationConsoleManager extends ClientConsoleManager {
    private final CommandParser commandParser = new CommandParser();

    public AuthenticationConsoleManager(CommandHashMap validCommands) {
        super(validCommands);
    }

    public AuthenticationRequest formAuthenticationRequest() throws InitializationException {
        if (consoleReader == null || validCommands == null) {
            throw new InitializationException("Console manager was not initialized");
        } else {
            try {
                String userInput = consoleReader.flexibleConsoleReadLine();
                String commandName = commandParser.parseCommandName(userInput, validCommands);
                String argumentLine = commandParser.getArgumentLine(userInput, commandName);
                CommandRecord commandRecord = commandParser.createCommandRecord(commandName, argumentLine, validCommands);
                if ("exit".equals(commandRecord.getCommand().getCommandName())) {
                    shutdown();
                }
                if (commandRecord.getCommand() instanceof AuthenticationCommand) {
                    return ((AuthenticationCommand) commandRecord.getCommand()).formRequest(consoleReader, Boolean.FALSE);
                }
            } catch (NoSuchCommandException | InvalidCommandException exception) {
                System.out.println(exception.getMessage());
            } catch (IOException ioException) {
                System.out.println("Unexpected IOException occurred");
            }
            return null;
        }
    }
}
