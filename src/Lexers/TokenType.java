package Lexers;

public enum TokenType {
  // starters
  START, END, EOF,

  // Variable Declaration
  DECLARE, VAR,

  // I/O
  PRINT, SCAN,

  // Identifiers
  IDENTIFIER,

  // Data types
  NUMBER, STRING,
  INTEGER, CHARACTER, BOOLEAN, FLOAT,

  // Literals
  TRUE, FALSE,
  NIL,

  // Logical Operators
  AND, OR, NOT,

  // Arithmetic Operators
  PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,

  // Assignment Operators
  ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MULTIPLY_ASSIGN, DIVIDE_ASSIGN,
  MODULO_ASSIGN,

  // Comparison Operators
  EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,

  // comments
  DASH, DASH_DASH,

  // Single-character tokens.
  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
  COMMA, DOT, SEMICOLON, COLON,

  // Bisaya++ Thing
  CONCAT, NEW_LINE, ESCAPE, NEW_LINE_LITERAL,

  // Control Flow
  IF, ELSE, ELIF, FOR, WHILE, DO, BREAK, CONTINUE, RETURN,
}