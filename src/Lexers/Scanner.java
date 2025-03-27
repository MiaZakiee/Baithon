/*
 * This file is responsible for scanning the source code and converting it into tokens.
 * Once converted into tokens, the tokens are then passed to the parser.
 * If any errors are found, the scanner will stop scanning and report the error.
 * The scanner will also check for reserved keywords and literals.
 * 
 * The scanner will also check for the following:
 * - Single character tokens
 * - Two character tokens TODO
 * - Whitespaces
 * - Comments
 * - Numbers
 * - Identifiers (AKA variables) TODO
 * 
 * Naa pa shay missing functionalities and I would love to do some unit testing for this. 
 * as well as for the whole project :D
 */
package Lexers;

import static Lexers.TokenType.*;
import Main.Baithon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
  // This is the file we will be scanning
  private final String source;

  // This is the list of tokens we will be returning
  private final List<Token> tokens = new ArrayList<>();

  // This is the reserved keywords in the language
  private static final Map<String, TokenType> keywords;
  static {
    keywords = new HashMap<>();
    // Start and End keywords
    keywords.put("SUGOD", TokenType.START);
    keywords.put("KATAPUSAN", TokenType.END);
    
    // Declaration keywords
    keywords.put("MUGNA", TokenType.DECLARE);
    keywords.put("IPAKITA",TokenType.PRINT);
    keywords.put("DAWAT",TokenType.SCAN);

    // Data types
    keywords.put("NUMERO", TokenType.INTEGER);
    keywords.put("LETRA", TokenType.CHARACTER);
    keywords.put("TIPIK", TokenType.FLOAT);
    keywords.put("TINOOD", TokenType.BOOLEAN);

    // Logical Operators
    keywords.put("O", TokenType.OR);
    keywords.put("UG", TokenType.AND);
    keywords.put("DILI", TokenType.NOT);

    // Literals
    keywords.put("OO",TokenType.TRUE);
    keywords.put("DILI",TokenType.FALSE);

    // Control Flow
    keywords.put("ALANG SA",TokenType.FOR);
    keywords.put("KUNG",TokenType.IF);
    keywords.put("KUNG DILI",TokenType.ELSE);
    keywords.put("KUNG PA",TokenType.ELIF);
  }

  // Constructor
  public Scanner(String source) {
    this.source = source;
  }

  // Scanner variables
  private int start = 0;
  private int current = 0;
  private int line = 1; 

  // This function will scan the tokens
  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(TokenType.EOF, "", null, line));
    return tokens;
  }

  // This function will scan the current token
  private void scanToken() {
    char c = advance();

    switch (c) {
      // Single Characters
      // (,),{,},[,],.,;,/,*,+,-,%,&
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case '[': addToken(LEFT_BRACKET); break;
      case ']': addToken(RIGHT_BRACKET); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case ';': addToken(SEMICOLON); break;
      case '/': addToken(SLASH); break;
      case '*': addToken(STAR); break;
      case '+': addToken(PLUS); break;
      case '%': addToken(MODULO); break;
      // Bisaya++ thing
      case '&': addToken(NEW_LINE);
      case '$': addToken(CONCAT);

      // Two character tokens
      // (==, !=, <=, >=)
      case '=': addToken(match('=') ? EQUAL : DECLARE); break;
      case '!': addToken(match('=') ? NOT_EQUAL : NOT); break;
      case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
      case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

      // Whitespaces
      case ' ': break;
      case '\r': break;
      case '\t': break;
      case '\n': line++; break;

      // comments
      case '-': 
        if (match('-')) {
          while (peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(MINUS);
        }
        break;

      default:
        if (Character.isDigit(c)) {
          number();
        } else if (Character.isLetter(c)) {
          //Check first through this function if the scanned text is a reserved word or an identifier
          //TOD0 SOME RESERVED WORDS ARE SEPARATED BY SPACE. EX. "KUNG WALA"
          identifier();
        } else {
          Baithon.error(line, "Unexpected character.");
        }     
    }
  }

  // This function will add the token to the list of tokens
  private void addToken(TokenType type) {
    tokens.add(new Token(type, source.substring(start, current), null, line));
  }

  // This function will scan the number and add it to the list of tokens
  void number() {
    // Consume the whole number before the decimal point
    while (Character.isDigit(peek())) advance();

    // Look for a fractional part
    if (peek() == '.' && Character.isDigit(peekNext())) {
      // Consume the "."
      advance();

      // Consume the digits after the "."
      while (Character.isDigit(peek())) advance();
    }

    addToken(INTEGER);
  }

  private void identifier() {    
    while (Character.isLetter(peek())) {
      advance();
    }

    String text = source.substring(start, current);
    TokenType type = keywords.getOrDefault(text, null);

    // Stop scanning when END is found
    if (type == TokenType.END) {
      current = source.length(); // Force scanner to finish
    }

    
    if (type != null) {
      addToken(type);
    } else {
      Baithon.error(line, "Unexpected word: " + text);
    }

  }

// ------------------------ UTIL FUNCTIONS -------------------------

  private char peek() {
    return isAtEnd() ? '\0' : source.charAt(current);
  }
  
  private boolean match(char expected) {
      if (isAtEnd() || source.charAt(current) != expected) return false;
      current++; // Consume the expected character
      return true;
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    return source.charAt(current++);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }
}
