package com.myinterpreter.lox;

public class Interpreter implements Expr.Visitor<Object>{
    
    // simply pull it back out
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    // recursively evaluate that subexpression and return it
    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluation(expr.expression);
    }

    // helper method which simply sends the expression back into the
    // interpreter’s visitor implementation
    private Object evaluation(Expr expr) {
        return expr.accept(this);
    }


    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluation(expr.right);
        
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                return -(double)right;
        }

        return  null;
    }
    
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluation(expr.left);
        Object right = evaluation(expr.right);

        switch (expr.operator.type) {
            // comparison operators
            case GREATER:
                return (double)left > (double)right;
            case GREATER_EQUAL:
                return (double)left >= (double)right;
            case LESS:
                return (double)left < (double)right;
            case LESS_EQUAL:
                return (double)left <= (double)right;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return !isEqual(left, right);
            
            // arithmetic operators
            case MINUS:
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                break;
            case SLASH:
                return (double)left / (double)right;
            case STAR:
                return (double)left * (double)right;

        }


        return null;
    }

}
