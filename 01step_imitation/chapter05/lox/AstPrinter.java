package lox;

import java.util.List;

class AstPrinter implements Expr.Visitor<String> {

  /*
   * AstPrinter class 将语法树（Abstract Syntax Tree，AST）中的表达式打印为字符串形式
   * 其主要目的为帮助开发人员理解生成的语法树结构，验证语法分析的正确性，并对编译器或解释器进行调试
   */

  //接收一个 Expr 类型的表达式，并返回该表达式的字符串表示
  String print(Expr expr) {
    return expr.accept(this);
  }

  //处理不同类型的表达式的具体访问方法
  //----------------------------------//
  @Override
  public String visitBinaryExpr(Expr.Binary expr) {
    return parenthesize(expr.operator.lexeme,
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
  public String visitUnaryExpr(Expr.Unary expr) {
    return parenthesize(expr.operator.lexeme, expr.right);
  }
  //----------------------------------//

  //私有辅助方法，用于将表达式和其子表达式以括号括起来，形成完整的表达式字符串
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
  // Note: AstPrinting other types of syntax trees is not shown in the
  // book, but this is provided here as a reference for those reading
  // the full code.
  private String parenthesize2(String name, Object... parts) {
    StringBuilder builder = new StringBuilder();

    builder.append("(").append(name);
    transform(builder, parts);
    builder.append(")");

    return builder.toString();
  }

  private void transform(StringBuilder builder, Object... parts) {
    for (Object part : parts) {
      builder.append(" ");
      if (part instanceof Expr) {
        builder.append(((Expr)part).accept(this));
      } else if (part instanceof Token) {
        builder.append(((Token) part).lexeme);
      } else if (part instanceof List) {
        transform(builder, ((List) part).toArray());
      } else {
        builder.append(part);
      }
    }
  }
}
