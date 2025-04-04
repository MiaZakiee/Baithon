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
import Parsers.*;

public class Interpreter implements Expr.Visitor<Object> {
    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        return null;
    }

    public void interpret(Expr expr) {
        try {
            Object value = evaluate(expr);
            System.out.println(stringify(value));
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
        return expr.getValue();
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        return null;
    }

	@Override
	public Object visitVariableExpr(Expr.Variable expr) {
		return null;
	}

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.getExpression());
    }

    // Helper functions -----------------------------------------------------

    private Object evaluate(Expr expr) {
        return expr.accept(this);
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

        return object.toString();
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Number && right instanceof Number) return;
        throw new RunTimeError(operator, "Operands must be a number.");
    }
    
}
