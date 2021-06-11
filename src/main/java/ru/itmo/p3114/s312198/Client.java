package ru.itmo.p3114.s312198;

import ru.itmo.p3114.s312198.commands.CommandHashMap;
import ru.itmo.p3114.s312198.commands.actions.complex.Add;
import ru.itmo.p3114.s312198.commands.actions.complex.AddIfMax;
import ru.itmo.p3114.s312198.commands.actions.complex.RemoveGreater;
import ru.itmo.p3114.s312198.commands.actions.complex.Update;
import ru.itmo.p3114.s312198.commands.actions.simple.Clear;
import ru.itmo.p3114.s312198.commands.actions.simple.ExecuteScript;
import ru.itmo.p3114.s312198.commands.actions.simple.Exit;
import ru.itmo.p3114.s312198.commands.actions.simple.Help;
import ru.itmo.p3114.s312198.commands.actions.simple.History;
import ru.itmo.p3114.s312198.commands.actions.simple.Info;
import ru.itmo.p3114.s312198.commands.actions.simple.Message;
import ru.itmo.p3114.s312198.commands.actions.simple.Nop;
import ru.itmo.p3114.s312198.commands.actions.simple.Permission;
import ru.itmo.p3114.s312198.commands.actions.simple.PrintFieldAscendingGroupAdmin;
import ru.itmo.p3114.s312198.commands.actions.simple.RemoveAllByShouldBeExpelled;
import ru.itmo.p3114.s312198.commands.actions.simple.RemoveAnyByTransferredStudents;
import ru.itmo.p3114.s312198.commands.actions.simple.RemoveById;
import ru.itmo.p3114.s312198.commands.actions.simple.Show;
import ru.itmo.p3114.s312198.commands.authentication.Login;
import ru.itmo.p3114.s312198.commands.authentication.Register;
import ru.itmo.p3114.s312198.commands.types.CommandTypes;
import ru.itmo.p3114.s312198.io.ConsoleReader;
import ru.itmo.p3114.s312198.managers.ClientConsoleManager;
import ru.itmo.p3114.s312198.transmission.AuthenticationRequest;
import ru.itmo.p3114.s312198.transmission.AuthenticationResponse;
import ru.itmo.p3114.s312198.transmission.CSChannel;
import ru.itmo.p3114.s312198.transmission.ResponsePack;
import ru.itmo.p3114.s312198.transmission.User;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        CommandHashMap validAuthenticationCommands = new CommandHashMap();
        validAuthenticationCommands.addCommandRecord("login", CommandTypes.AUTHENTICATION_COMMAND, new Login());
        validAuthenticationCommands.addCommandRecord("register", CommandTypes.AUTHENTICATION_COMMAND, new Register());
        validAuthenticationCommands.addCommandRecord("exit", CommandTypes.SIMPLE_COMMAND, new Exit());

        CommandHashMap validCommands = new CommandHashMap();
        validCommands.addCommandRecord("help", CommandTypes.SIMPLE_COMMAND, new Help());
        validCommands.addCommandRecord("info", CommandTypes.SIMPLE_COMMAND, new Info());
        validCommands.addCommandRecord("show", CommandTypes.SIMPLE_COMMAND, new Show());
        validCommands.addCommandRecord("remove_by_id", CommandTypes.SIMPLE_COMMAND, new RemoveById());
        validCommands.addCommandRecord("clear", CommandTypes.SIMPLE_COMMAND, new Clear());
        validCommands.addCommandRecord("execute_script", CommandTypes.SIMPLE_COMMAND, new ExecuteScript());
        validCommands.addCommandRecord("exit", CommandTypes.SIMPLE_COMMAND, new Exit());
        validCommands.addCommandRecord("history", CommandTypes.SIMPLE_COMMAND, new History());
        validCommands.addCommandRecord("remove_all_by_should_be_expelled", CommandTypes.SIMPLE_COMMAND, new RemoveAllByShouldBeExpelled());
        validCommands.addCommandRecord("remove_any_by_transferred_students", CommandTypes.SIMPLE_COMMAND, new RemoveAnyByTransferredStudents());
        validCommands.addCommandRecord("print_field_ascending_group_admin", CommandTypes.SIMPLE_COMMAND, new PrintFieldAscendingGroupAdmin());
        validCommands.addCommandRecord("nop", CommandTypes.SIMPLE_COMMAND, new Nop());
        validCommands.addCommandRecord("msg", CommandTypes.SIMPLE_COMMAND, new Message());
        validCommands.addCommandRecord("permission", CommandTypes.SIMPLE_COMMAND, new Permission());
        validCommands.addCommandRecord("add", CommandTypes.COMPLEX_COMMAND, new Add());
        validCommands.addCommandRecord("update", CommandTypes.COMPLEX_COMMAND, new Update());
        validCommands.addCommandRecord("add_if_max", CommandTypes.COMPLEX_COMMAND, new AddIfMax());
        validCommands.addCommandRecord("remove_greater", CommandTypes.COMPLEX_COMMAND, new RemoveGreater());

        AuthenticationConsoleManager authenticationConsoleManager = new AuthenticationConsoleManager(validAuthenticationCommands);
        ConsoleReader consoleReader = new ConsoleReader();
        authenticationConsoleManager.initialize(consoleReader);
        Boolean connecting = Boolean.TRUE;
        Boolean running = Boolean.TRUE;

        while (connecting) {
            try (CSChannel channel = new CSChannel(new Socket("localhost", 7035))) {
                AuthenticationRequest authenticationRequest = authenticationConsoleManager.formAuthenticationRequest();
                channel.writeObject(authenticationRequest);
                AuthenticationResponse authenticationResponse = (AuthenticationResponse) channel.readObject();
                System.out.println(authenticationResponse.getServerMessage());
                if (authenticationResponse.allowed()) {
                    User user = authenticationResponse.getUser();
                    ClientConsoleManager clientConsoleManager = new ClientConsoleManager(validCommands);
                    clientConsoleManager.autoInitialize();
                    while (running) {
                        channel.writeObject(clientConsoleManager.formPrimaryPack(user));
                        ResponsePack responsePack = (ResponsePack) channel.readObject();
                        clientConsoleManager.printResponsePack(responsePack);
                        if (responsePack.allowed()) {
                            channel.writeObject(clientConsoleManager.formSecondaryPack(user));
                            responsePack = (ResponsePack) channel.readObject();
                            clientConsoleManager.printResponsePack(responsePack);
                        }
                    }
                }
            } catch (IOException ioException) {
                System.out.println(ioException.getMessage());
                try {
                    System.out.println("Enter \"Y\" if you want to reconnect or anything else to exit");
                    connecting = "Y".equalsIgnoreCase(consoleReader.flexibleConsoleReadLine());
                } catch (IOException ignore) {
                }
            }
        }
        System.out.println("Shutting down the console manager");
    }
}
