package Parsers;

import Lexers.Token;
import Lexers.TokenType;
import java.beans.Expression;
import java.util.List;

/* 
* expression     → literal
*                | unary
*                | binary
*                | grouping ;
* 
* literal        → NUMBER | STRING | "true" | "false" | "nil" ;
* grouping       → "(" expression ")" ;
* unary          → ( "-" | "!" ) expression ;
* binary         → expression operator expression ;
* operator       → "==" | "!=" | "<" | "<=" | ">" | ">="
*                | "+"  | "-"  | "*" | "/" ;
*/

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    // Constructor
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expression parse() {
        try {
            return parseTokens();
        } catch (Exception e) {
            System.out.println("Error parsing expression: " + e.getMessage());
            return null;
        }
    }

    private  Expression parseTokens() {
        while (!isAtEnd()) {
            Token token = advance();
            // System.out.println("Parsing token: " + token); // Debugging output
            // TODO: Implement parsing rules here

            // Wrong implementation
            // Ensure first token is START
            // if (!(token.getType() == TokenType.START)) {
            //     System.out.println("Error: Expected START token, got " + token);
            //     return null;
            // }
        }

        return null; // Placeholder return
    }

    // Helper functions
    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
