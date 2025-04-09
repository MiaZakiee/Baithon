package Parsers;

import Lexers.Token;
import Lexers.TokenType;
import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        // R visitVarStmt(Var stmt);
        R visitBlockStmt(Block stmt);
        R visitMultiVar(MultiVar stmt);
    }

    public static class Expression extends Stmt {
        public Expression(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }

        // getter
        public Expr getExpression() {
            return expression;
        }
    }

    public static class Print extends Stmt {
        public Print(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        // getter
        public Expr getExpression() {
            return expression;
        }
    }

    // public static class Var extends Stmt {
    //     public Var(Token name, Expr initializer, TokenType declaredType) {
    //         this.name = name;
    //         this.initializer = initializer;
    //         this.declaredType = declaredType; 
    //     }

    //     final Token name;
    //     final Expr initializer;
    //     final TokenType declaredType;

    //     @Override
    //     public <R> R accept(Visitor<R> visitor) {
    //         return visitor.visitVarStmt(this);
    //     }

    //     // getter
    //     public Token getName() {
    //         return name;
    //     }
    //     public Expr getInitializer() {
    //         return initializer;
    //     }

    //     public TokenType getDeclaredType() {
    //         return declaredType;
    //     }
    // }

    public static class Block extends Stmt {
        public Block(List<Stmt> statements) {
            this.statements = statements;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }

        final List<Stmt> statements;

        // getter
        public List<Stmt> getStatements() {
            return statements;
        }
    }

    public static class MultiVar extends Stmt {
        public final List<Token> names;
        public final List<Expr> initializers;
        public final TokenType declaredType;

        public MultiVar(List<Token> names, List<Expr> initializers, TokenType declaredType) {
            this.names = names;
            this.initializers = initializers;
            this.declaredType = declaredType;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitMultiVar(this);
        }

        // getters
        public List<Token> getNames() {
            return names;
        }
        public List<Expr> getInitializers() {
            return initializers;
        }
        public TokenType getDeclaredType() {
            return declaredType;
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
