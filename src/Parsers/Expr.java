/*
 * This abstract class is used to represent the structure of the expressions in the language.
 * This is used in the Parser class to create a tree of expressions.
 * 
 * The Expr class contains a nested class called Binary which is used to represent binary expressions.
 * 
 * Expression types:
 * - Assign: Represents an assignment expression. X = 5
 * - Binary: Represents a binary expression. X + Y, X - Y, etc.
 * - Grouping: Represents a grouping expression. (X + Y) * 5
 * - Literal: Represents a literal expression. 5, "Hello", etc.
 * - Logical: Represents a logical expression. X && Y, X || Y, etc.
 * - Unary: Represents a unary expression. -X, !X, etc.
 * - Variable: Represents a variable expression. X, Y, etc.
 */
package Parsers;

import Lexers.Token;

abstract public class Expr {

    // The base class for all expression types
    public interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitUnaryExpr(Unary expr);
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitLogicalExpr(Logical expr);
        R visitVariableExpr(Variable expr);
    }

    public abstract  <R> R accept(Visitor<R> visitor);
    // Expression types

    // Assignment expression
    // This is used to represent the assignment of a value to a variable
    public static class Assign extends Expr {
        final Token name;
        final Expr value;

        public Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        // getter
        public Token getName() {
            return name;
        }
        public Expr getValue() {
            return value;
        }
    }

    // Binary expression
    // This is used to represent binary operations like addition, subtraction, etc.
    public static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        // getters
        public Expr getLeft() {
            return left;
        }
        public Token getOperator() {
            return operator;
        }
        public Expr getRight() {
            return right;
        }
    }

    // Grouping expression
    // This is used to represent expressions that are grouped together
    // using parentheses
    public static class Grouping extends Expr {
        final Expr expression;

        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        // getter
        public Expr getExpression() {
            return expression;
        }
    }

    // Literal expression
    // This is used to represent literal values like numbers, strings, etc.
    public static class Literal extends Expr {
        final Object value;

        public Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        // getter
        public Object getValue() {
            return value;
        }
    }

    // Logical expression
    // This is used to represent logical operations like AND, OR, etc.
    public static class Logical extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        public Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }

        // getters
        public Expr getLeft() {
            return left;
        }
        public Token getOperator() {
            return operator;
        }
        public Expr getRight() {
            return right;
        }
    }

    // Unary expression
    // This is used to represent unary operations like negation, etc.
    public static class Unary extends Expr {
        final Token operator;
        final Expr right;

        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        // getters
        public Token getOperator() {
            return operator;
        }
        public Expr getRight() {
            return right;
        }
    }

    // Variable expression
    // This is used to represent variables in the language
    public static class Variable extends Expr {
        final Token name;

        public Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        // getter
        public Token getName() {
            return name;
        }
    }
}
