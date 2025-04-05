package Interpreter;

import Lexers.Token;

public class RunTimeError extends RuntimeException {
    final public Token token;

    public RunTimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}