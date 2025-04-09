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
  public static final Map<String, TokenType> keywords;
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
    keywords.put("TINUOD", TokenType.BOOLEAN);
    // TODO: temporary word will not use string as keyword!
    keywords.put("STRING", TokenType.STRING);

    // Logical Operators
    keywords.put("O", TokenType.OR);
    keywords.put("UG", TokenType.AND);
    keywords.put("DILI", TokenType.NOT);

    // BOOLEAN VALUES
    // REMOVED BECAUSE OF LANGUAGE SPECIFICATION SAKDNSADNJASNDKASNDA
    // keywords.put("\"OO\"",TokenType.TRUE);
    // keywords.put("\"DILI\"",TokenType.FALSE);

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
    // System.out.println("scanner/scanToken: " + c);

    switch (c) {
      // Single Characters
      // (,),{,},.,;,/,*,+,-,%,&
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case '[':
        StringBuilder escapeContent = new StringBuilder();
        while (!isAtEnd() && peek() != ']') {
            escapeContent.append(advance());
        }

        if (peek() == ']') {
            advance(); // Consume the closing bracket
            String content = escapeContent.toString();

            // more than one character == bad
            if (content.length() > 1) {
                Baithon.error(line, "Invalid escape sequence: " + content);
                return;
            }

            // Handle special case for `[]]` as a literal `]`
            if (content.isEmpty() && peek() == ']') {
                advance(); // Consume the second `]`
                addToken(TokenType.STRING, "]"); // Treat it as a literal `]`
            } else {
                // Handle the escape sequence
                // For example, if the escape sequence is `\n`, treat it as a newline character
                switch (content) {
                    case "n":
                        content = "\n"; // Newline
                        break;
                    case "t":
                        content = "\t"; // Tab
                        break;
                    case "r":
                        content = "\r"; // Carriage return
                        break;
                }
                addToken(TokenType.STRING, content); // Treat the content as a STRING token
            }
        } else {
            Baithon.error(line, "Unterminated escape sequence. Missing ']'.");
        }
        break;
      case ',': addToken(COMMA); break;
      case '"':
          string();
        break;
      case '\'':
        character();
        break;
      case '.': 
        if (Character.isDigit(peek())) {
          number();
        } else {
          addToken(DOT);
        }
        break;
      case ':': addToken(COLON); break;
      case ';': addToken(SEMICOLON); break;
      case '/': addToken(match('=') ? DIVIDE_ASSIGN : DIVIDE); break;
      case '*': addToken(match('=') ? MULTIPLY_ASSIGN : MULTIPLY); break;
      case '%': addToken(match('=') ? MODULO_ASSIGN : MODULO); break;
      case '+': 
        if (match('+')) {
          addToken(INCREMENT);
        } else if (match('=')) {
          addToken(PLUS_ASSIGN);
        } else {
          addToken(PLUS);
        }
        break;

      // Bisaya++ thing
      case '&': addToken(CONCAT); break;
      case '$': addToken(NEW_LINE_LITERAL); break;

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
          // consume the rest of the line
          while (peek() != '\n' && !isAtEnd()) advance();
          // consume the new line
          if (peek() == '\n') {
            line++;
            addToken(NEW_LINE);
            advance();
          }
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
          identifier();
        } else {
          Baithon.error(line, "Unexpected character: " + c);
        }     
    }
  }

  // This function will add the token to the list of tokens
  private void addToken(TokenType type) {
    // System.out.println("scanner/addToken: " + type);
    tokens.add(new Token(type, source.substring(start, current), null, line));
  }

  // This function will add the token to the list of tokens with a literal value
  private void addToken(TokenType type, Object literal) {
    // System.out.println("scanner/addToken: " + type + " " + literal);
    tokens.add(new Token(type, source.substring(start, current), literal, line));
  }

  // This function will scan the character and add it to the list of tokens
  void character () {
    if (isAtEnd() || peek() == '\n') {
      Baithon.error(line, "Unterminated character literal.");
      return;
    }

    char value = advance();

    if (peek() == '\'') {
      advance(); // consume the closing quote
      addToken(TokenType.CHARACTER, value);
    } else {
      Baithon.error(line, "Unterminated character literal.");
    }

    // debugging
    // System.out.println("scanner/character: " + value);
  }

  // This function will scan the string and add it to the list of tokens
  void string() {
    // consume string
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') line++;
      advance();
    }

    // check if the string is closed properly
    if (isAtEnd()) {
      Baithon.error(line, "Unterminated string.");
      return;
    }

    // consume the closing quote
    advance();

    // get the string value
    String value = source.substring(start + 1, current - 1);
    addToken(TokenType.STRING, value);
    // debugging
    // System.out.println("scanner/string: " + value);
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


  // this function will scan the identifier and add it to the list of tokens
  // it will also check if the identifier is a reserved word
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
