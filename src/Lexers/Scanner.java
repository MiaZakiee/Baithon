/*
 * This file is responsible for scanning the source code and converting it into tokens.
 * Once converted into tokens, the tokens are then passed to the parser.
 * If any errors are found, the scanner will stop scanning and report the error.
 * The scanner will also check for reserved keywords and literals.
 * 
 * The scanner will also check for the following:
 * - Single character tokens
 * - Two character tokens
 * - Whitespaces
 * - Comments
 * - Numbers
 * - Identifiers (AKA variables)
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
    keywords.put("MUGNA", TokenType.VAR);
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
      case '.': 
        if (Character.isDigit(peek())) {
          number();
        } else {
          addToken(DOT);
        }
        break;
      case ';': addToken(SEMICOLON); break;
      case '/': addToken(match('=') ? DIVIDE : DIVIDE_ASSIGN); break;
      case '*': addToken(match('=') ? MULTIPLY : MULTIPLY_ASSIGN); break;
      case '%': addToken(match('=') ? MODULO : MODULO_ASSIGN); break;
      case '+': addToken(match('=') ? PLUS : PLUS_ASSIGN); break;

      // Bisaya++ thing
      case '&': addToken(NEW_LINE);
      case '$': addToken(CONCAT);

      // Two character tokens
      // (==, !=, <=, >=)
      case '=': addToken(match('=') ? DECLARE : EQUAL); break;
      case '!': addToken(match('=') ? NOT_EQUAL : NOT); break;
      case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
      case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

      // Whitespaces
      case ' ': break;
      case '\r':
        if (match('\n')) {
          addToken(TokenType.NEW_LINE);
          line++;}
        break;
      case '\t': break;
      case '\n': 
        addToken(NEW_LINE);
        line++; 
        break;

      case '-': 
        if (match('-')) { // Comments
          while (peek() != '\n' && !isAtEnd()) advance();
        } else if (match('=')) {  // Assignment operator
          addToken(MINUS_ASSIGN);
        } 
        else {  // Minus operator
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

  // This function will add the token to the list of tokens with a literal value
  private void addToken(TokenType type, Object literal) {
    tokens.add(new Token(type, source.substring(start, current), literal, line));
  }

  // This function will scan the number and add it to the list of tokens

  void number() {
    boolean isDouble = false;

    // Check if number starts with a dot, e.g., .5
    if (source.charAt(start) == '.' && Character.isDigit(peek())) {
      isDouble = true;
      while (Character.isDigit(peek())) advance();
    } else {
      while (Character.isDigit(peek())) advance();

      // Check for decimal point
      if (peek() == '.' && Character.isDigit(peekNext())) {
        isDouble = true;
        advance(); // consume '.'
        while (Character.isDigit(peek())) advance();
      }
    }

    // Check for scientific notation (e or E)
    if (peek() == 'e' || peek() == 'E') {
      isDouble = true;
      advance(); // consume 'e' or 'E'

      // optional sign
      if (peek() == '+' || peek() == '-') {
        advance();
      }

      // must have at least one digit after 'e'
      if (!Character.isDigit(peek())) {
        Baithon.error(line, "Invalid scientific notation: expected digit after 'e'");
        return;
      }

      while (Character.isDigit(peek())) advance();
    }

    String numberAsString = source.substring(start, current);
    Object numberValue;

    try {
      if (isDouble) {
        numberValue = Double.valueOf(numberAsString);
        addToken(TokenType.FLOAT, numberValue);
      } else {
        numberValue = Integer.valueOf(numberAsString);
        addToken(TokenType.INTEGER, numberValue);
      }
    } catch (NumberFormatException e) {
      Baithon.error(line, "Invalid number format: " + numberAsString);
    }
  }


  private void identifier() {
    // Consume the whole identifier/variable name
    // no need to check if first character is a digit since na cover na sha sa scanToken
    while (Character.isLetterOrDigit(peek()) || peek() == '_') {
      advance();
    }

    // Check if the identifier is a reserved word
    String text = source.substring(start, current);
    TokenType type = keywords.getOrDefault(text, null);

    // If the identifier is a reserved word, add it to the list of tokens
    if (type != null) {
      addToken(type);
      return;
    } 


    // If the identifier has a space in it, we need to check if the next character is a space
    // and if it is, we need to consume it
    if (peek() == ' ') {
      int currentIndex = current;
      advance(); // go to the next character

      // read the next identifier
      int secondStart = current;
      while (Character.isLetterOrDigit(peek()) || peek() == '_') {
        advance();
      }

      // combine the two identifiers
      String secondText = source.substring(secondStart, current);
      TokenType concatTokenType = keywords.getOrDefault(text + " " + secondText, null);

      // if the combined identifier is a reserved word, add it to the list of tokens
      // else go back to the previous character
      if (concatTokenType != null) {
        addToken(concatTokenType);
        return;
      } else {
        current = currentIndex;
      }
    }
    // base case, one word token
    addToken(IDENTIFIER);
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
