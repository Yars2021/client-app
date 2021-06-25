package ru.itmo.p3114.s312198.commands.authentication;

import ru.itmo.p3114.s312198.commands.actions.AbstractCommand;
import ru.itmo.p3114.s312198.io.ConsoleReader;
import ru.itmo.p3114.s312198.transmission.AuthenticationRequest;
import ru.itmo.p3114.s312198.transmission.User;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

abstract public class AuthenticationCommand extends AbstractCommand {
    protected User actor;

    protected String SHA1(String input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(input.getBytes(StandardCharsets.UTF_8), 0, input.length());
            return DatatypeConverter.printHexBinary(messageDigest.digest());
        } catch (NoSuchAlgorithmException exc) {
            return null;
        }
    }

    public AuthenticationCommand(String command, String inputPattern, String description) {
        super(command, inputPattern, description);
    }

    public void setActor(User actor) {
        this.actor = actor;
    }

    public User getActor() {
        return actor;
    }

    abstract public AuthenticationRequest formRequest(ConsoleReader consoleReader, Boolean suppressOutput);
}
