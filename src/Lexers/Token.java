package Lexers;

public class Token {
  // Token class
  final TokenType type;
  // Lexeme is the string representation of the token
  final String lexeme;
  // Literal is the value of the token
  final Object literal;
  // Line number of the token
  final int line; 

  public Token(TokenType type, String lexeme, Object literal, int line) {
    this.type = type;
    this.lexeme = lexeme;
    this.literal = literal;
    this.line = line;
  }

  public String toString() {
    return type + " " + lexeme + " " + literal;
  }
  
  public TokenType getType() {
    return type;
  }

  // Getters
  public String getLexeme() {
    return this.lexeme;
  }

  public Object getLiteral() {
    return this.literal;
  }

  public int getLine() {
    return this.line;
  }
}

