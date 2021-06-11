package ru.itmo.p3114.s312198.commands.authentication;

import ru.itmo.p3114.s312198.commands.actions.AbstractCommand;
import ru.itmo.p3114.s312198.commands.results.CommandResult;
import ru.itmo.p3114.s312198.commands.results.Status;
import ru.itmo.p3114.s312198.io.ConsoleReader;
import ru.itmo.p3114.s312198.transmission.AuthenticationRequest;
import ru.itmo.p3114.s312198.transmission.User;

import java.io.IOException;
import java.util.ArrayList;

public class Login extends AuthenticationCommand {
    public Login() {
        super("login", "", "Use it to log in");
    }

    @Override
    public CommandResult execute() {
        ArrayList<String> output = new ArrayList<>();
        return new CommandResult(Status.OK, output);
    }

    @Override
    public AuthenticationRequest formRequest(ConsoleReader consoleReader) {
        String username;
        String credentials;
        try {
            System.out.println("Enter your username: ");
            username = consoleReader.flexibleConsoleReadLine();
            System.out.println("Enter your password: ");
            credentials = SHA1(username + consoleReader.flexibleConsoleReadPassword());
            actor = new User(username, credentials, null);
            return new AuthenticationRequest("LOG", actor);
        } catch (IOException ioException) {
            System.out.println("An unexpected IOException occurred");
            return null;
        }
    }

    @Override
    public AbstractCommand clone() {
        ArrayList<String> cloneArguments = new ArrayList<>(arguments);
        Login commandClone = new Login();
        commandClone.setArguments(cloneArguments);
        commandClone.setOwner(owner);
        return commandClone;
    }
}
