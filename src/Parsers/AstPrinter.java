/*
 * AST Printer
 * This class is responsible for printing the AST (Abstract Syntax Tree) in a readable format.
 * It implements the Visitor pattern to traverse the AST and generate a string representation of it.
 */

package Parsers;

public class AstPrinter implements Expr.Visitor<String> {
    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return parenthesize("assign " + expr.name.getLexeme(), expr.value);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.getLexeme(), expr.right);
    }
    
    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.getLexeme(),
        expr.left, expr.right);
    }
    
    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }
    
    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "nil";
        return expr.value.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.operator.getLexeme(), expr.left, expr.right);
    }

    @Override
    public String visitVariableExpr(Expr.Variable expr) {
        return expr.name.getLexeme();
    }

    @Override
    public String visitIncrementOrDecrementExpr(Expr.IncrementOrDecrement expr) {
        String op = expr.getOperator().getLexeme();
        String inner = expr.getVariable().getName().getLexeme();

        return expr.isPrefix()
          ? "(" + op + inner + ")"
          : "(" + inner + op + ")";
    }

    // Helper function
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();
        
        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }

    public String print(Expr expr) {
        return expr.accept(this);
    }
}


