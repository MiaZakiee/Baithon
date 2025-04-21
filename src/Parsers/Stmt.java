package Parsers;

import java.util.List;

import Lexers.Token;
import Lexers.TokenType;

public abstract class Stmt {
    public interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
        R visitBlockStmt(Block stmt);
        R visitMultiVar(MultiVar stmt);
        R visitIfStmt(If stmt);
        R visitElseIfStmt(ElseIf stmt);
        R visitWhileStmt(While stmt);
        R visitScanStmt(Scan stmt);
        R visitDoWhileStmt(DoWhile stmt);
        R visitBreakStmt(Break stmt);
        R visitContinueStmt(Continue stmt);
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
        boolean isLast = false;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }

        // getter
        public Expr getExpression() {
            return expression;
        }
        public boolean isLast() {
            return isLast;
        }
        // setter
        public void setLast(boolean isLast) {
            this.isLast = isLast;
        }
    }

    public static class Var extends Stmt {
        public Var(Token name, Expr initializer, TokenType declaredType) {
            this.name = name;
            this.initializer = initializer;
            this.declaredType = declaredType; 
        }

        final Token name;
        final Expr initializer;
        final TokenType declaredType;

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

        public TokenType getDeclaredType() {
            return declaredType;
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

    public static class If extends Stmt {
        public If(Expr condition, Stmt thenBranch, List<ElseIf> elseIfBranches, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseIfBranches = elseIfBranches;
            this.elseBranch = elseBranch;
        }

        final Expr condition;
        final Stmt thenBranch;
        final List<ElseIf> elseIfBranches;
        final Stmt elseBranch;

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }

        // getters
        public Expr getCondition() {
            return condition;
        }
        public Stmt getThenBranch() {
            return thenBranch;
        }
        public List<ElseIf> getElseIfBranches() {
            return elseIfBranches;
        }
        public Stmt getElseBranch() {
            return elseBranch;
        }
    }

    public static class ElseIf extends Stmt {
        final Expr condition;
        final Stmt block;

        public ElseIf(Expr condition, Stmt block) {
            this.condition = condition;
            this.block = block;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitElseIfStmt(this);
        }

        // Getters
        public Expr getCondition() {
            return condition;
        }

        public Stmt getBlock() {
            return block;
        }
    }

    public static class While extends Stmt {
        public While(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
        return visitor.visitWhileStmt(this);
        }

        final Expr condition;
        final Stmt body;

        // getters
        public Expr getCondition() {
            return condition;
        }
        public Stmt getBody() {
            return body;
        }
    }

    public static class DoWhile extends Stmt {
        public DoWhile(Expr condition, Stmt body) {
        this.condition = condition;
        this.body = body;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitDoWhileStmt(this);
        }

        final Expr condition;
        final Stmt body;

        // getters
        public Expr getCondition() {
            return condition;
        }
        public Stmt getBody() {
            return body;
        }
    }

    public static class Break extends Stmt {
        public Break() {}

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBreakStmt(this);
        }
    }

    public static class Continue extends Stmt {
        public Continue() {}

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitContinueStmt(this);
        }
    }

    public static class Scan extends Stmt {
        private final List<Token> names;

        public Scan( List<Token> names) {
            this.names = names;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitScanStmt(this);
        }

        // getters
        public List<Token> getNames() {
            return names;
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
