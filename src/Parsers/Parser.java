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
import java.util.ArrayList;
import java.util.Arrays;
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
    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        // consume new lines
        while (match(TokenType.NEW_LINE)) {
            // skip new lines
        }

        // ensure that the first token is a START token
        if (!match(TokenType.START)) {
            throw error(peek(), "Expect 'SUGOD' at the start of the program.");
        }

        // parse the statements
        while (!isAtEnd() && !check(TokenType.END)) {
            if (match(TokenType.NEW_LINE)) continue; // skip new lines
            statements.add(statement());
        }

        // ensure that the last token is an END token
        if (!match(TokenType.END)) throw error(peek(), "Expect 'KATAPUSAN' at the end of the program.");

        // consume END token
        advance(); // consume END token

        // check if there are tokens after the END token
        if (!isAtEnd()) {
            throw error(peek(), "Unexpected token after 'KATAPUSAN'.");
        }

        return statements;
    }

    // this function is used to parse the expression
    private Expr expression() {
        return assignment();
    }

    private Stmt statement() {

        if (match(TokenType.PRINT)) return printStatement();
        if (match(TokenType.VAR)) return varDeclaration(false);
        if (match(TokenType.PUNDOK)) return new Stmt.Block(block());
        if (match(TokenType.WHILE)) return whileStatement();
        if (match(TokenType.FOR)) return forStatement();
        if (match(TokenType.SCAN)) return scanStatement();
        if (match(TokenType.DO)) return doWhileStatement();
        if (match(TokenType.BREAK)) return breakStatement();
        if (match(TokenType.CONTINUE)) return continueStatement();
        
        if (check(TokenType.IF) || check(TokenType.ELIF)) {
            advance();
            return ifStatement();
        }

        Expr expr = expression();

        if (!check(EOF) && !check(TokenType.NEW_LINE) && !check(TokenType.END)) {
            throw error(peek(), "Expect new line after statement.");
        }
        
        // If we're not at the end of the file or block, consume the newline
        if (match(TokenType.NEW_LINE)) {
            // do nothing, just consume it
        }
        
        return new Stmt.Expression(expr);
    }

    private Stmt declaration() {
        try {
        if (match(DECLARE)) return varDeclaration(false);

        return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt printStatement() {
        consume(TokenType.COLON, "Expect ':' after 'IPAKITA'.");
        Expr value = expression(); // Parse the entire expression, including concatenation
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration(boolean isLoop) {
        // need to consume data types
        TokenType dataType = peek().getType();
        // System.out.println("Parser: declared dataType: " + dataType);

        if (dataType != INTEGER 
        && dataType != FLOAT 
        && dataType != CHARACTER
        && dataType != BOOLEAN
        && dataType != STRING) {
            throw error(peek(), "Invalid data type.");
        }

        advance(); // consume data type

        // parse the variable nameS PLURAL for multi declaration
        List<Token> names = new ArrayList<>();
        List<Expr> initializers = new ArrayList<>();

        // consume the variable names
        do { 
            Token name = consume(TokenType.IDENTIFIER, "Expected variable name.");
            names.add(name);
        } while (!isLoop && match(TokenType.COMMA));

        if (match(TokenType.DECLARE)) {
            do { 
                Expr initializer = expression();
                
                // Baithon specification: true is "OO" and false is "DILI" all enclosed in double quotes
                if (dataType == BOOLEAN && initializer instanceof Expr.Literal) { 
                    Object value = ((Expr.Literal) initializer).getValue();

                    if (value instanceof String) {
                        if (value.equals("OO")) {
                            initializer = new Expr.Literal(true);
                        } else if (value.equals("DILI")) {
                            initializer = new Expr.Literal(false);
                        } else {
                            throw error(peek(), "Invalid boolean value: " + value + ". Use \"OO\" or \"DILI\".");
                        }
                    }
                }

                if (!isTypeCompatible(dataType, initializer)) {
                    throw error(peek(), "Type mismatch: " + getExprType(initializer) + " is not of type " + dataType);
                }

                initializers.add(initializer);
            } while (!isLoop && match(TokenType.COMMA));
        } else {
            if (peek().getType() != NEW_LINE) throw error(peek(), 
            "Expect new line after variable declaration.");

            for (int i = 0; i < names.size(); i++) {
                initializers.add(new Expr.Literal(null));
            }
        }

        return new Stmt.MultiVar(names, initializers, dataType);
    }

    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'IF'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
        consume(NEW_LINE, "Expect new line after condition.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;

        if (match(TokenType.ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'MINTRAS'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");
        consume(NEW_LINE, "Expect new line after condition.");

        loopDepth++;
        Stmt body = statement();
        loopDepth--;

        return new Stmt.While(condition, body);
    }

    private Stmt doWhileStatement() {
        consume(TokenType.NEW_LINE, "Expect new line after 'BUHATA'.");
        loopDepth++;
        Stmt body = statement();
        loopDepth--;
        consume(TokenType.WHILE, "Expect 'MINTRAS' after 'BUHAT'.");
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'MINTRAS'.");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "Expect ')' after condition.");

        return new Stmt.DoWhile(condition, body);
    }

    private Stmt forStatement() {
        consume(TokenType.LEFT_PAREN, "Expect '(' after 'ALANG SA'.");

        Stmt initializer = null;
        if (match(TokenType.VAR)) {
            initializer = varDeclaration(true);
        } else if (!match(TokenType.COMMA)) {
            initializer = expressionStatement();
        }
        consume(TokenType.COMMA, "Expect ',' after initializer.");
        // debugging
        // System.out.println("Parser: initializer                FOR LOOP: " + initializer);

        Expr condition = null;
        if (!check(COMMA)) {
            condition = expression();
        }
        consume(TokenType.COMMA, "Expect ',' after loop condition.");

        // debugging
        // System.out.println("Parser: condition             FOR LOOP: " + condition);

        Expr increment = null;
        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }

        // debugginginitializer
        // System.out.println("Parser: increment         FOR LOOP: " + increment);

        consume(RIGHT_PAREN, "Expect ')' after loop condition.");
        consume(NEW_LINE, "Expect new line after condition.");

        loopDepth++;
        Stmt body = statement();
        loopDepth--;

        if (increment != null) {
        body = new Stmt.Block(
            Arrays.asList(
                body,
                new Stmt.Expression(increment)));
        }

        if (condition == null) condition = new Expr.Literal(true);
        body = new Stmt.While(condition, body);

        if (initializer != null) {
            body = new Stmt.Block(Arrays.asList(initializer, body));
        }

        return body;
    }

    private int loopDepth = 0;    
    private Stmt breakStatement() {
        if (loopDepth == 0) {
            throw error(previous(), "Cannot use 'HUNONG' outside of a loop.");
        }
        consume(TokenType.NEW_LINE, "Expect new line after 'HUNONG'.");
        return new Stmt.Break();
    }

    private Stmt continueStatement() {
        if (loopDepth == 0) {
            throw error(previous(), "Cannot use 'PADAYON' outside of a loop.");
        }
        consume(TokenType.NEW_LINE, "Expect new line after 'PADAYON'.");
        return new Stmt.Continue();
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        // Consume opening brace
        consume(LEFT_BRACE, "Expect '{' after PUNDOK.");

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            if (match(TokenType.NEW_LINE)) continue;
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Stmt scanStatement() {
        consume(TokenType.COLON, "Expect ':' after 'DAWAT'.");

        List<Token> names = new ArrayList<>();
        do {
            Token name = consume(TokenType.IDENTIFIER, "Expected variable name after 'DAWAT'.");
            names.add(name);
        } while (match(TokenType.COMMA));

        return new Stmt.Scan(names);
    }

    private Expr assignment() {
        Expr expr = parseOr();
        // System.out.println("Parser: left side of assignment: " + expr);
        
        // Compound assignment
        if (match(PLUS_ASSIGN,MINUS_ASSIGN,MULTIPLY_ASSIGN,DIVIDE_ASSIGN,MODULO_ASSIGN)) {
            Token operator = previous();
            Expr value = assignment();

            if (!(expr instanceof Expr.Variable)) {
                throw error(operator,"Invalid assignment target for: " + operator.getLexeme() + ".");
            }

            Expr.Variable var = (Expr.Variable) expr;
            TokenType simpleOp;
            switch (operator.getType()) {
                case PLUS_ASSIGN:
                    simpleOp = TokenType.PLUS;
                    break;
                case MINUS_ASSIGN:
                    simpleOp = TokenType.MINUS;
                    break;
                case MULTIPLY_ASSIGN:
                    simpleOp = TokenType.MULTIPLY;
                    break;
                case DIVIDE_ASSIGN:
                    simpleOp = TokenType.DIVIDE;
                    break;
                    case MODULO_ASSIGN:
                    simpleOp = TokenType.MODULO;
                    break;
                    default:
                    throw error(operator, "Unknown operator: " + operator.getLexeme());
                }
                
            Expr binary = new Expr.Binary(
                new Expr.Variable(var.getName()),
                new Token(simpleOp, operator.getLexeme().substring(0,1), null, operator.getLine()),
                value
                );
                
            return new Expr.Assign(var.getName(), binary);
        }

        // Simple assignment
        if (match(DECLARE)) {
            Token equals = previous();
            Expr value = assignment();
            // System.out.println("Parser: right side of assignment: " + value);

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).getName();
                return new Expr.Assign(name, value);
            }

            throw error(equals, "Invalid assignment target."); 
        }

        return expr;
    }


    // this function is used to parse the equality expression
    private Expr equality() {
        Expr expr = comparison();

        while (match(EQUAL, NOT_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr parseOr() {
        Expr expr = parseAnd();

        while (match(TokenType.OR)) {
            Token operator = previous();
            Expr right = parseAnd();
            expr = new Expr.Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr parseAnd() {
        Expr expr = equality();

        while (match(TokenType.AND)) {
            Token operator = previous();
            Expr right = equality();
            expr = new Expr.Logical(expr, operator, right);
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

        while (match(TokenType.MINUS, TokenType.PLUS, TokenType.CONCAT)) {
            Token operator = previous();
            Expr right = factor();

            // Check if the operator is CONCAT and the right-hand side is missing
            if (operator.getType() == TokenType.CONCAT && !(right instanceof Expr)) {
                throw error(operator, "Missing expression after '&' operator.");
            }

            

            expr = new Expr.Binary(expr, operator, right);
        }

        // Check for invalid concatenation without an operator
        if (check(TokenType.STRING) || check(TokenType.IDENTIFIER)) {
            throw error(peek(), "Missing '&' operator between expressions.");
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

        if (match(TokenType.INCREMENT, TokenType.DECREMENT)) {
            Token operator = previous();
            Expr operand = unary();
        
            if (!(operand instanceof Expr.Variable)) {
                throw error(previous(), "Can only apply '"
                + operator.getLexeme() + "' to a variable.");
            }

            return new Expr.IncrementOrDecrement(operator,(Expr.Variable) operand, true);
        }
        return primary();
    }

    // this function is used to parse the primary expression
    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NULL)) return new Expr.Literal(null);

        if (match(TokenType.INTEGER, TokenType.FLOAT, TokenType.CHARACTER, TokenType.STRING)) {
            // Return a literal expression for numbers, strings, etc.
            return new Expr.Literal(previous().getLiteral());
        }

        if (match(TokenType.NEW_LINE_LITERAL)) {
            // Return a literal expression for new line
            return new Expr.Literal('\n');
        }

        if (match(TokenType.IDENTIFIER)) {
            // Return a variable expression for identifiers
            Expr.Variable var = new Expr.Variable(previous());

            if (match(TokenType.INCREMENT,TokenType.DECREMENT)) {
                Token operator = previous();
                return new Expr.IncrementOrDecrement(operator, var, false);
            } 
                
            return var;
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        // Debugging
        // System.out.println("Parser: peek: " + peek());
        // System.out.println("Parser: current: " + current);
        // System.out.println("Parser: tokens: " + tokens);

        throw error(previous(), "Expect expression.");
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
        if (previous().getType() == TokenType.NEW_LINE) return;

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
                // DEBUGGING
                // System.out.println("Parser: matched token: " + type);
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
        if (!isAtEnd()) {
            // DEBUGGING
            // System.out.println("Parser: advancing token: " + peek());
            current++;
        }
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isTypeCompatible(TokenType declaredType, Expr initializer) {
        if (initializer instanceof Expr.Literal) {
            Object value = ((Expr.Literal) initializer).getValue();
            return switch (declaredType) {
                case INTEGER -> value instanceof Integer;
                case FLOAT -> value instanceof Double;
                case STRING -> value instanceof String;
                case BOOLEAN -> value instanceof Boolean;
                case CHARACTER -> value instanceof Character;
                default -> false;
            };
        }
        return true; // Allow non-literal expressions (e.g., variables, binary expressions)
    }
    
    private String getExprType(Expr expr) {
        if (expr instanceof Expr.Literal) {
            Object value = ((Expr.Literal) expr).getValue();
            if (value instanceof String) return "STRING";
            if (value instanceof Boolean) return "BOOLEAN";
            if (value instanceof Double) return "FLOAT";
            if (value instanceof Character) return "CHARACTER";
            if (value instanceof Integer) return "INTEGER"; // or separate NUMBER/INTEGER
            if (value == null) return "NULL";
        } 
        return "UNKNOWN";
    }
}