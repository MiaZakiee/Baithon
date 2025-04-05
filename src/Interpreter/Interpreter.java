/*
 * Baithon Interpreter
 * This class is responsible for interpreting the expressions
 * and executing the code.
 * It implements the Visitor pattern to visit each expression
 * and evaluate it.
 * The Interpreter class is the main entry point for the interpreter.
 * It takes the expression and evaluates it.
 * The Interpreter class also handles runtime errors and
 * prints the result of the expression.
 */
package Interpreter;

import Lexers.Token;
import Main.Baithon;
import Main.Environment;
import Parsers.*;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>
                                    ,Stmt.Visitor<Void> {
    private Environment environment = new Environment();


    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.getValue());
        environment.assign(expr.getName(), value);
        return value;
    }

    public void interpret(List<Stmt> statements) {
        try {
        for (Stmt statement : statements) {
            execute(statement);
        }
        } catch (RunTimeError error) {
            Baithon.runTimeError(error);
        }
    } 

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.getRight());

        switch (expr.getOperator().getType()) {
            case MINUS:
                return -(double) right;
            case NOT:
                return !(boolean) isTruthy(right);
        }

        return null;
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.getLeft());
        Object right = evaluate(expr.getRight());

        switch (expr.getOperator().getType()) {
            case PLUS:
                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                if (left instanceof String && right instanceof Number) {
                    return (String) left + right.toString();
                }
                if (left instanceof Number && right instanceof String) {
                    return left.toString() + (String) right;
                }
                if (left instanceof Number && right instanceof Number) {
                    return (double) left + (double) right;
                }
                if (left instanceof Character && right instanceof Character) {
                    return (char) ((Character) left + (Character) right);
                }
                throw new RunTimeError(expr.getOperator(), "Operands must be two strings or two numbers.");
            case MINUS:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left - (double) right;
            case MULTIPLY:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left * (double) right;
            case DIVIDE:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left / (double) right;
            case MODULO:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left % (double) right;
            case GREATER:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.getOperator(), left, right);
                return (double) left <= (double) right;
            case EQUAL:
                return isEqual(left, right);
            case NOT_EQUAL:
                return !isEqual(left, right);
        }
        return null;
    }

    
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        Object value = expr.getValue();

        // DEBUGING FEATURE: print the literal value and type
        // System.out.println("Literal value: " + value);
        // System.out.println("Literal type: " + value.getClass());
        // System.out.println("Literal type name: " + value.getClass().getName());

        return value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        return null;
    }

	@Override
	public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.getName());
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.getExpression());
    }

    // Helper functions -----------------------------------------------------

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.getStatements(), new Environment(environment));
        return null;
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try {
        this.environment = environment;

        for (Stmt statement : statements) {
            execute(statement);
        }
        } finally {
            this.environment = previous;
        }
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        evaluate(stmt.getExpression());
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.getExpression());
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if (stmt.getInitializer() != null) {
            value = evaluate(stmt.getInitializer());
            // DEBUGING FEATURE: print the initializer value and type
            // System.out.println("Initializer: " + value);
            // System.out.println("Initializer type: " + value.getClass());
            // System.out.println("Initializer type name: " + value.getClass().getName());
        }

        environment.define(stmt.getName().getLexeme(), value);

        // DEBUGING FEATURE: print the variable name and value
        System.out.println("Variable " + stmt.getName().getLexeme() + " = " + stringify(value) + " of type " + value.getClass().getName());
        return null;
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        if (object instanceof Integer) return object.toString();

        return object.toString();
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Number && right instanceof Number) return;
        throw new RunTimeError(operator, "Operands must be a number.");
    }
    
}
