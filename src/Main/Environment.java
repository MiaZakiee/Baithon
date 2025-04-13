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

            if (varType == TokenType.FLOAT) {
                if (value instanceof Double) {
                    // OK
                } else if (value instanceof Integer) {
                    value = ((Integer) value).doubleValue(); // Convert to Double
                } else {
                    throw new RunTimeError(name, "Type mismatch: Expected FLOAT but got " + value.getClass().getSimpleName());
                }
            }

            if (varType == TokenType.BOOLEAN) {
                if (value instanceof String) {
                    if (value.equals("OO")) {
                        value = true;
                    } else if (value.equals("DILI")) {
                        value = false;
                    } else {
                        throw new RunTimeError(name, "Type mismatch: Expected BOOLEAN but got String.");
                    }
                } else if (!(value instanceof Boolean)) {
                    throw new RunTimeError(name, "Type mismatch: Expected BOOLEAN but got " + value.getClass().getSimpleName());
                }
            }

            // else string so.. whatever goes?? 

            values.put(name.getLexeme(), value);
        } else if (enclosing != null) {
            // If the variable is not found in the current environment, check the enclosing environment
            enclosing.assign(name, value); // Delegate to enclosing environment
        } else {
            throw new RunTimeError(name, "Variable '" + name.getLexeme() + "' is not defined.");
        }
    }
}
