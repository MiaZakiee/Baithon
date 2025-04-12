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
import Lexers.TokenType;
import Main.Baithon;
import Main.Environment;
import Parsers.*;
import java.util.List;

public class Interpreter implements Expr.Visitor<Object>
                                    ,Stmt.Visitor<Void> {
    private Environment environment = new Environment();


    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.getValue());  // Evaluate the value of the expression
        Token variableName = expr.getName(); // Get the variable name

        // get current value
        Object currentValue = environment.get(variableName); // Get the current value of the variable

        // check if the variable is already defined
        if (currentValue == null) {
            throw new RunTimeError(variableName, "Variable '" + variableName.getLexeme() + "' is not defined.");
        }

        // Assign the value to the variable
        environment.assign(variableName, value);
        
        return value; // Return the value
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
                checkNumberOperand(expr.getOperator(), right);
                if (right instanceof Integer) {
                    return - (Integer) right;
                } else {
                    return - (Double) right;
                }
            case NOT:
                return !(boolean) isTruthy(right);
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Number) return;
        throw new RunTimeError(operator, "Operand must be a number.");
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
        Token operator = expr.getOperator();

        // Left     Right   Result
        // Integer	Integer	Integer
        // Integer	Double	Double
        // Double	Integer	Double
        // Double	Double	Double

        switch (operator.getType()) {
            case PLUS:
                if (left instanceof String || right instanceof String) {
                    if (!(left instanceof String) || !(right instanceof String)) {
                        throw new RunTimeError(expr.getOperator(),
                        "Type mismatch: Cannot concatenate " + left.getClass().getSimpleName() +
                        " with " + right.getClass().getSimpleName());
                    }
                    return (String) left + (String) right;
                }
                if (left instanceof Character && right instanceof Character) {
                    return (char) ((Character) left + (Character) right);
                }
                return numberArithmetic(left, right, operator);
            case MINUS,MULTIPLY,DIVIDE,MODULO,GREATER,GREATER_EQUAL,LESS,LESS_EQUAL:
                return numberArithmetic(left, right, operator);
            case EQUAL:
                return isEqual(left, right);
            case NOT_EQUAL:
                return !isEqual(left, right);
            case CONCAT:
                return stringify(left) + stringify(right);
            case NEW_LINE:
                // System.out.print("Stringify NEW_LINE: " + stringify(left) + stringify(right));
                String leftString = stringify(left).stripTrailing();
                String rightString = stringify(right).stripLeading();
                return leftString + rightString;
            case ESCAPE:
                // Insert the escape sequence literal between left and right parts.
                String escapeValue = expr.getOperator().getLiteral() != null
                ? (String) expr.getOperator().getLiteral()
                : ""; // default to "&" if none provided
                return stringify(left) + escapeValue + stringify(right);
            default:
                throw new RunTimeError(expr.getOperator(), "Unknown operator: " + expr.getOperator().getLexeme());
            }
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
        Object left = evaluate(expr.getLeft());

        if (expr.getOperator().getType() == TokenType.OR) {
        if (isTruthy(left)) return left;
        } else {
        if (!isTruthy(left)) return left;
        }

        return evaluate(expr.getRight());
    }

	@Override
	public Object visitVariableExpr(Expr.Variable expr) {
        return environment.get(expr.getName());
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.getExpression());
    }

    
    // this function is kinda useless kay murag adto tanan mo agi sa visitMultiVar
    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Token name = stmt.getName();
        Expr initializer = stmt.getInitializer();
        TokenType declaredType = stmt.getDeclaredType();;

        if (initializer != null) {
            Object value = evaluate(initializer);

            // Convert "OO" and "DILI" to boolean if the declared type is BOOLEAN
            if (declaredType == TokenType.BOOLEAN && value instanceof String) {
                if (value.equals("OO")) value = true;
                if (value.equals("DILI")) value = false;
            }
        }

        environment.define(name.getLexeme(), null, declaredType);
        // DEBUGING FEATURE: print the variable name and value
        try {
            // System.out.println("Variable " + name.getLexeme() + " = " + stringify(value) + " of type " + value.getClass().getName());
        } catch (NullPointerException e) {
            // System.out.println("null variable: " + name.getLexeme());
        }
        
        return null;
    }

    @Override
    public Void visitMultiVar(Stmt.MultiVar stmt) {
        List<Token> names = stmt.getNames();
        List<Expr> initializers = stmt.getInitializers();
        TokenType declaredType = stmt.getDeclaredType();

        for(int i = 0; i < names.size(); i++) {
            Token name = names.get(i);
            Expr initializer = initializers.get(i);
            Object value = null;

            if (initializer != null) {
                value = evaluate(initializer);

                // Convert "OO" and "DILI" to boolean if the declared type is BOOLEAN
                if (declaredType == TokenType.BOOLEAN && value instanceof String) {
                    if (value.equals("OO")) value = true;
                    if (value.equals("DILI")) value = false;
                }
            }

            environment.define(name.getLexeme(), value, declaredType);

            // DEBUGING FEATURE: print the variable name and value
            try {
                // System.out.println("Variable " + name.getLexeme() + " = " + stringify(value) + " of type " + value.getClass().getName());
            } catch (NullPointerException e) {
                // System.out.println("null variable: " + name.getLexeme());
            }
        }
        return null;
    }

    @Override
    public Object visitIncrementOrDecrementExpr(Expr.IncrementOrDecrement expr) {
        Token variableToken = expr.getVariable().getName();
        Object value = environment.get(variableToken);

        if (!(value instanceof Integer || value instanceof Double)) {
            throw new RunTimeError(variableToken, "Variable must be a number.");
        }

        Object newValue;
        if (expr.getOperator().getType() == TokenType.INCREMENT) {
            newValue = (value instanceof Integer) ?
                (Integer) value + 1 :
                (Double) value + 1.0;
        } else if (expr.getOperator().getType() == TokenType.DECREMENT) {
            newValue = (value instanceof Integer) ?
                (Integer) value - 1 :
                (Double) value - 1.0;
        } else {
            throw new RunTimeError(variableToken, "Invalid increment/decrement operator.");
        }

        // update the variable in the environment
        environment.assign(variableToken, newValue);

        return expr.isPrefix() ? newValue : value;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if (isTruthy(evaluate(stmt.getCondition()))) {
            execute(stmt.getThenBranch());
        } else if (stmt.getElseBranch() != null) {
            execute(stmt.getElseBranch());
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while (isTruthy(evaluate(stmt.getCondition()))) {
            execute(stmt.getBody());
        }
        return null;
    }

    // Helper functions -----------------------------------------------------

    // Used for type fidelity
    private Object numberArithmetic(Object left, Object right, Token operator) {
        TokenType type = operator.getType();

        // Check if both operands are numbers
        if (!(left instanceof Number) || !(right instanceof Number)) {
            throw new RunTimeError(operator, "Operands must be numbers.");
        }

        boolean leftIsInt = left instanceof Integer;
        boolean rightIsInt = right instanceof Integer;

        // Integer + Integer = Integer
        if (leftIsInt && rightIsInt) {
            int l = (int) left;
            int r = (int) right;
            return switch (type) {
                case PLUS -> l + r;
                case MINUS -> l - r;
                case MULTIPLY -> l * r;
                case DIVIDE -> {
                    if (r == 0) throw new RunTimeError(null, "Division by zero.");
                    yield l / r;
                }
                case MODULO -> {
                    if (r == 0) throw new RunTimeError(null, "Division by zero.");
                    yield l % r;
                }
                case GREATER -> l > r;
                case GREATER_EQUAL -> l >= r;
                case LESS -> l < r;
                case LESS_EQUAL -> l <= r;
                default -> throw new RunTimeError(null, "Unsupported operator for integers.");
            };
        }

        // Otherwise, convert both to double
        double l = toDouble(left);
        double r = toDouble(right);
        return switch (type) {
            case PLUS -> l + r;
            case MINUS -> l - r;
            case MULTIPLY -> l * r;
            case DIVIDE -> {
                if (r == 0) throw new RunTimeError(null, "Division by zero.");
                yield l / r;
            }
            case MODULO -> {
                if (r == 0) throw new RunTimeError(null, "Division by zero.");
                yield l % r;
            }
            case GREATER -> l > r;
            case GREATER_EQUAL -> l >= r;
            case LESS -> l < r;
            case LESS_EQUAL -> l <= r;
            default -> throw new RunTimeError(null, "Unsupported operator for doubles.");
        };
    }

    private double toDouble(Object number) {
        if (number instanceof Integer) return ((Integer) number).doubleValue();
        if (number instanceof Double) return (Double) number;
        throw new RuntimeException("Expected number, got: " + number.getClass().getSimpleName());
    }

    private Object evaluate(Expr expr) {
        // System.out.println("Evaluating expression: " + expr.getClass().getSimpleName());
        Object result = expr.accept(this);
        // System.out.println("Result: " + result + " (Type: " + (result != null ? result.getClass().getName() : "null") + ")");
        return result;
    }

    private Object evaluate(List<Expr> expressions) {
        Object value = null;
        for (Expr expression : expressions) {
            value = evaluate(expression);
        }
        return value;
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
        
        String result = stringify(value);

        // System.out.println("Print statement: " + result);

        // result = result
            // .replace("&", "") // concat remove &
            // .replace("$", "\n"); // new line

        System.out.print(result + "\n");

        return null;
    }

    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) return true;
        if (left == null) return false;
        return left.equals(right);
    }

    private String stringify(Object object) {
        // System.out.println("Stringifying object: " + object + " (Type: " + (object != null ? object.getClass().getName() : "null") + ")");

        if (object == null) return "nil";

        // maybe change this to OO or DILI??
        if (object instanceof Boolean) return (boolean) object ? "OO" : "DILI";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2); // remove trailing ".0"
            }
            return text;
        }

        if (object instanceof Integer) return object.toString();
        if (object instanceof Character) return object.toString();
        if (object instanceof String) return (String) object;

        return object.toString();
    }

    private boolean isTypeCompatible(TokenType declaredType, Object value) {
        if (declaredType == TokenType.BOOLEAN && value instanceof String) {
            return value.equals("OO") || value.equals("DILI");
        }

        boolean result =  switch (declaredType) {
            case INTEGER -> value instanceof Integer;
            case FLOAT -> value instanceof Double;
            case STRING -> value instanceof String;
            case BOOLEAN -> value instanceof Boolean;
            case CHARACTER -> value instanceof Character;
            default -> false;
        };

        // System.out.println("isTypeCompatible: Declared type: " + declaredType + ", Value type: " + (value != null ? value.getClass().getName() : "null") + ", Result: " + result);
        return result;
    }
}
