package Main;

import Interpreter.RunTimeError;
import Lexers.Token;
import java.util.HashMap;
import java.util.Map;
// import Parsers.Expr;

public class Environment {
    final Environment enclosing;
    private final Map<String,Object> values = new HashMap<>();

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


    public void define(String name, Object value) {
        values.put(name, value);
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.getLexeme())) {
            values.put(name.getLexeme(), value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new RunTimeError(name, "Undefined variable '" + name.getLexeme() + "'.");
    }
}
