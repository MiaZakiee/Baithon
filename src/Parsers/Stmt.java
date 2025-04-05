package Parsers;

import Lexers.Token;
import java.util.List;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
        R visitBlockStmt(Block stmt);
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

    public static class Var extends Stmt {
        public Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        final Token name;
        final Expr initializer;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }

        // getter
        public Token getName() {
            return name;
        }
        public Expr getInitializer() {
            return initializer;
        }
    }

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

    public abstract <R> R accept(Visitor<R> visitor);
}
