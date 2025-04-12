package Main;

import Interpreter.RunTimeError;
import Lexers.Token;
import Lexers.TokenType;
import java.util.HashMap;
import java.util.Map;
// import Parsers.Expr;

public class Environment {
    final Environment enclosing;
    private final Map<String,Object> values = new HashMap<>();
    private final Map<String,TokenType> types = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Object get(Token name) {
        if (values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }
        if (enclosing != null) {
            return enclosing.get(name);
        }
        throw new RunTimeError(name, "Undefined variable '" + name.getLexeme() + "'.");
    }


    public void define(String name, Object value, TokenType type) {
        // System.out.println("Defining variable: " + name + ", value: " + value + ", type: " + type);
        values.put(name, value);
        types.put(name, type);
    }

    public void assign(Token name, Object value) {
        // System.out.println("Assigning variable: " + name + ", value: " + value);
        // TokenType varType = types.get(name.getLexeme());
        // if (varType == null) throw new RunTimeError(name, "Variable '" + name.getLexeme() + "' is not defined.");


        // if (varType == TokenType.INTEGER) {
        //     if (value instanceof Integer) {
        //         // OK
        //     } else if (value instanceof Double) {
        //         double d = (Double) value;
        //         if (d == (int)d) {
        //             // round‐trip whole numbers back to Integer
        //             value = (int)d;
        //         } else {
        //             throw new RunTimeError(name,
        //                 "Type mismatch: Expected INTEGER but got Double.");
        //         }
        //     } else {
        //         throw new RunTimeError(name,
        //             "Type mismatch: Expected INTEGER but got " + value.getClass().getSimpleName());
        //     }
        // }

        // // …and similar for FLOAT, CHARACTER, etc.

        // values.put(name.getLexeme(), value);

        // System.out.println("Assigning variable: " + name + ", value: " + value);
        if (types.containsKey(name.getLexeme())) {
            TokenType varType = types.get(name.getLexeme());

            // Type checking for INTEGER
            if (varType == TokenType.INTEGER) {
                if (value instanceof Integer) {
                    // OK
                } else if (value instanceof Double) {
                    double d = (Double) value;
                    if (d == (int) d) {
                        value = (int) d; // Convert to Integer
                    } else {
                        throw new RunTimeError(name, "Type mismatch: Expected INTEGER but got Double.");
                    }
                } else {
                    throw new RunTimeError(name, "Type mismatch: Expected INTEGER but got " + value.getClass().getSimpleName());
                }
            }

            // Add similar checks for FLOAT, CHARACTER, etc.

            values.put(name.getLexeme(), value);
        } else if (enclosing != null) {
            // If the variable is not found in the current environment, check the enclosing environment
            enclosing.assign(name, value); // Delegate to enclosing environment
        } else {
            throw new RunTimeError(name, "Variable '" + name.getLexeme() + "' is not defined.");
        }
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
