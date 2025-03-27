package Baithon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Baithon.TokenType.*;

class Scanner {
    // This is the file we will be scanning
    private final String source;
    // This is the list of tokens we will be returning
    private final List<Token> tokens = new ArrayList<>();

    // This is the current position in the source file
    private int start = 0;
    private int current = 0;
    @SuppressWarnings("FieldMayBeFinal")
    private int line = 1; 

    // This is the reserved keywords in the language
    private static final Map<String, TokenType> keywords;
    static {
        keywords = new HashMap<>();
        keywords.put("START", TokenType.START);
        keywords.put("END", TokenType.END);
        keywords.put("EOF", TokenType.EOF);
        keywords.put("MUGNA", TokenType.MUGNA);
        keywords.put("NUMERO", TokenType.NUMERO);
        keywords.put("LETRA", TokenType.LETRA);
        keywords.put("TINOOD", TokenType.TINOOD);
    }

    Scanner (String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
    char c = advance();
    
    if (Character.isLetter(c)) {
      identifier();
    } else if (c == ' ' || c == '\r' || c == '\t' || c == '\n') {
      // Ignore whitespace.
    } else {
      Lox.error(line, "Unexpected character.");
    }
  }

  private void identifier() {
    while (Character.isLetter(peek())) advance();

    String text = source.substring(start, current);
    TokenType type = keywords.getOrDefault(text, null);
    
    if (type != null) {
      addToken(type);
    } else {
      Lox.error(line, "Unexpected word: " + text);
    }

    // Stop scanning when END is found
    if (type == TokenType.END) {
      current = source.length(); // Force scanner to finish
    }
  }

  private char peek() {
    return isAtEnd() ? '\0' : source.charAt(current);
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    tokens.add(new Token(type, source.substring(start, current), null, line));
  }


}
