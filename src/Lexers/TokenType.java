package Lexers;

enum TokenType {
  // starters
  START, END, EOF,

  // Variable Declaration
  DECLARE,

  // I/O
  PRINT, SCAN,

  // Data types
  INTEGER, CHARACTER, BOOLEAN, FLOAT,

  // Literals
  TRUE, FALSE,

  // Logical Operators
  AND, OR, NOT,

  // Arithmetic Operators
  PLUS, MINUS, TIMES, DIVIDE, MODULO,

  // Comparison Operators

  EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,

  // comments
  DASH, DASH_DASH,

  // Single-character tokens.
  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE, LEFT_BRACKET, RIGHT_BRACKET,
  COMMA, DOT, SEMICOLON, SLASH, STAR,

  // Bisaya++ Thing
  CONCAT, NEW_LINE,

  // Control Flow
  IF, ELSE, ELIF, FOR, WHILE, DO, BREAK, CONTINUE, RETURN,
}