/*
 * Parser
 * this class is responsible for parsing the tokens generated by the lexer
 * and converting them into an abstract syntax tree (AST).
 * The parser will also check for syntax errors and report them to the user.
 * * The parser will also check for the following:
 * - Expressions
 * - Literals
 * - Grouping
 * - Unary and Binary expressions
 * - Operators
 * - Parentheses
 *
 *  This is basically where we set the grammar rules for the language. 
 *  
 *  Probably the most libog part of the project pls help :((
 *  ga follow ra gyud ko sa lox tutorial
 */
package Parsers;

import Lexers.Token;
import Lexers.TokenType;
import static Lexers.TokenType.*;
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

    // Parse the tokens and return an expression
    // main function
    public Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            // remove this later, only for testing
            System.out.println("Error parsing expression: " + error.getMessage());
            return null;
        }
    }

    // this function is used to parse the expression
    private Expr expression() {
        return equality();
    }

    // this function is used to parse the equality expression
    private Expr equality() {
        Expr expr = comparison();

        while (match(NOT_EQUAL, NOT_EQUAL)) {
        Token operator = previous();
        Expr right = comparison();
        expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // this function is used to parse the comparison expression
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // this function is used to parse the term expression
    private Expr term() {
        Expr expr = factor();

        while (match(TokenType.MINUS, TokenType.PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // this function is used to parse the factor expression
    private Expr factor() {
        Expr expr = unary();

        while (match(TokenType.DIVIDE, TokenType.MULTIPLY)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // this function is used to parse the unary expression
    private Expr unary() {
        if (match(TokenType.NOT, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    // this function is used to parse the primary expression
    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NIL)) return new Expr.Literal(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().getLiteral());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    // error handling methods
    // this class is used to handle the parse error
    private static class ParseError extends RuntimeException {}

    // this function is used to consume the token
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    // this function is used to handle the error
    private ParseError error(Token token, String message) {
        reportError(token, message);
        return new ParseError();
    }

    // this function is used to report the error
    static void reportError(Token token, String message) {
        if (token.getType() == TokenType.EOF) {
            report(token.getLine(), " at end", message);
        } else {
            report(token.getLine(), " at '" + token.getLexeme() + "'", message);
        }
    }

    // this function is used to report the error
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
    }

    // this function is used to synchronize the parser with the tokens
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
        if (previous().getType() == SEMICOLON) return;

        switch (peek().getType()) {
            // TODO
            // case CLASS:
            // case FUN:
            // case VAR:
            case FOR:
            case IF:
            case WHILE:
            case PRINT:
            case RETURN:
            return;
        }

        advance();
        }
    }

    // this function is used to match the token with the type
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
        if (check(type)) {
            advance();
            return true;
        }
        }

        return false;
    }

    // this function is used to check if the token is of the type
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
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
