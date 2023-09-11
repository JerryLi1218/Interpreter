package com.myinterpreter.lox;

import java.util.ArrayList;
import java.util.List;

import static com.myinterpreter.lox.TokenType.*;

public class Parser {
    
    // to unwind the parser
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Expr expression() {
        return equality();
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt statement() {
        if(match(PRINT)) return printStatement();

        return expressionStatment();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' afer value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ':' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt expressionStatment() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' afer value.");
        return new Stmt.Expression(expr);
    }

    // equality -> comparison ( ( "!=" | "==" ) comparison )* ;
    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;  
    private Expr comparison() {
        Expr expr = term();
        
        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }

    // term -> factor ( ( "-" | "+" ) factor )* ;
    private Expr term() {
        Expr expr = factor();
        
        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }

    // factor -> unary ( ( "/" | "*" ) unary )* ;
    private Expr factor() {
        Expr expr = unary();
        
        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }
        
        return expr;
    }

    // unary -> ( ":" | "-" ) unary | primary ;
    private Expr unary() {

        while (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        
        return primary();
    }
    
    // primary -> NUMBER | STRING | "true" | "false" | "nil"
    //        | "(" expression ")" ;
    private Expr primary() {
        if (match(FALSE)) return new Expr.Literal(false);
        if (match(TRUE)) return new Expr.Literal(true);
        if (match(NIL)) return new Expr.Literal(null);
        
        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);    
        }

        if(match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }
        
        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");

    }


    // express that check using a handy match() method.
    // to see if the current token has any of the given types
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    // checks to see if the next token is of theexpected type
    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    // returns true if the current token is of the given type.
    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    // consumes the current token and returns it.
    private Token advance() {
        if (!isAtEnd()) current++; 
        return previous();
    }

    // bottom out on the last handful of primitive operations.
    // checks if we’ve run out of tokens to parse.
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    // returns thecurrent token we have yet to consume
    private Token peek() {
        return tokens.get(current);
    }

    // returns the mostrecently consumed token
    private Token previous() {
        return tokens.get(current - 1);
    }

    // report an error
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }
    

    // discard tokens until we’re right at the beginning of the nextstatement.
    private void synchronize() {
        advance();

        while(!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }


}   
