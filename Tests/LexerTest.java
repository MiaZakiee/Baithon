import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import Lexers.Scanner;
import Lexers.Token;
import Lexers.TokenType;

class LexerTest {

    @Test
    void testKeywords() {
        // Test all reserved keywords
        String source = "SUGOD KATAPUSAN MUGNA IPAKITA DAWAT NUMERO LETRA TIPIK TINUOD " +
                        "O UG DILI OO DILI ALANG SA MINTRAS KUNG KUNG DILI KUNG PA BUHATA " +
                        "HUNONG PADAYON PUNDOK NULL";
                        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        // -1 to exclude EOF token
        assertEquals(24, tokens.size() - 1);
        assertEquals(TokenType.START, tokens.get(0).getType());
        assertEquals(TokenType.END, tokens.get(1).getType());
        assertEquals(TokenType.VAR, tokens.get(2).getType());
        assertEquals(TokenType.PRINT, tokens.get(3).getType());
        assertEquals(TokenType.SCAN, tokens.get(4).getType());
        assertEquals(TokenType.INTEGER, tokens.get(5).getType());
        assertEquals(TokenType.CHARACTER, tokens.get(6).getType());
        assertEquals(TokenType.FLOAT, tokens.get(7).getType());
        assertEquals(TokenType.BOOLEAN, tokens.get(8).getType());
        assertEquals(TokenType.OR, tokens.get(9).getType());
        assertEquals(TokenType.AND, tokens.get(10).getType());
        assertEquals(TokenType.NOT, tokens.get(11).getType());
        assertEquals(TokenType.TRUE, tokens.get(12).getType());
        assertEquals(TokenType.FALSE, tokens.get(13).getType());
        assertEquals(TokenType.FOR, tokens.get(14).getType());
        assertEquals(TokenType.WHILE, tokens.get(16).getType());
        assertEquals(TokenType.IF, tokens.get(17).getType());
        assertEquals(TokenType.ELSE, tokens.get(18).getType());
        assertEquals(TokenType.ELIF, tokens.get(20).getType());
        assertEquals(TokenType.DO, tokens.get(22).getType());
        assertEquals(TokenType.BREAK, tokens.get(23).getType());
        assertEquals(TokenType.CONTINUE, tokens.get(24).getType());
        assertEquals(TokenType.PUNDOK, tokens.get(25).getType());
        assertEquals(TokenType.NULL, tokens.get(26).getType());
    }

    @Test
    void testOperators() {
        // Test all operators
        String source = "= == <> < <= > >= + - * / % += -= *= /= %= ++ -- & $";
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(TokenType.DECLARE, tokens.get(0).getType());
        assertEquals(TokenType.EQUAL, tokens.get(1).getType());
        assertEquals(TokenType.NOT_EQUAL, tokens.get(2).getType());
        assertEquals(TokenType.LESS, tokens.get(3).getType());
        assertEquals(TokenType.LESS_EQUAL, tokens.get(4).getType());
        assertEquals(TokenType.GREATER, tokens.get(5).getType());
        assertEquals(TokenType.GREATER_EQUAL, tokens.get(6).getType());
        assertEquals(TokenType.PLUS, tokens.get(7).getType());
        assertEquals(TokenType.MINUS, tokens.get(8).getType());
        assertEquals(TokenType.MULTIPLY, tokens.get(9).getType());
        assertEquals(TokenType.DIVIDE, tokens.get(10).getType());
        assertEquals(TokenType.MODULO, tokens.get(11).getType());
        assertEquals(TokenType.PLUS_ASSIGN, tokens.get(12).getType());
        assertEquals(TokenType.MINUS_ASSIGN, tokens.get(13).getType());
        assertEquals(TokenType.MULTIPLY_ASSIGN, tokens.get(14).getType());
        assertEquals(TokenType.DIVIDE_ASSIGN, tokens.get(15).getType());
        assertEquals(TokenType.MODULO_ASSIGN, tokens.get(16).getType());
        assertEquals(TokenType.INCREMENT, tokens.get(17).getType());
        assertEquals(TokenType.DECREMENT, tokens.get(18).getType());
        assertEquals(TokenType.CONCAT, tokens.get(19).getType());
        assertEquals(TokenType.NEW_LINE_LITERAL, tokens.get(20).getType());
    }

    @Test
    void testLiterals() {
        // Test numeric literals
        String source = "123 45.67 -123 -45.67 0 0.0 .5";
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(TokenType.INTEGER, tokens.get(0).getType());
        assertEquals(123, tokens.get(0).getLiteral());
        
        assertEquals(TokenType.FLOAT, tokens.get(1).getType());
        assertEquals(45.67, tokens.get(1).getLiteral());
        
        assertEquals(TokenType.MINUS, tokens.get(2).getType());
        assertEquals(TokenType.INTEGER, tokens.get(3).getType());
        assertEquals(123, tokens.get(3).getLiteral());
        
        assertEquals(TokenType.MINUS, tokens.get(4).getType());
        assertEquals(TokenType.FLOAT, tokens.get(5).getType());
        assertEquals(45.67, tokens.get(5).getLiteral());
        
        assertEquals(TokenType.INTEGER, tokens.get(6).getType());
        assertEquals(0, tokens.get(6).getLiteral());
        
        assertEquals(TokenType.FLOAT, tokens.get(7).getType());
        assertEquals(0.0, tokens.get(7).getLiteral());
        
        assertEquals(TokenType.FLOAT, tokens.get(8).getType());
        assertEquals(0.5, tokens.get(8).getLiteral());
    }

    @Test
    void testStringAndCharLiterals() {
        // Test string and character literals
        String source = "\"Hello, World!\" 'A' '\\n' \"Special [n] characters\"";
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(TokenType.STRING, tokens.get(0).getType());
        assertEquals("Hello, World!", tokens.get(0).getLiteral());
        
        assertEquals(TokenType.CHARACTER, tokens.get(1).getType());
        assertEquals('A', tokens.get(1).getLiteral());
        
        // Test escape sequences
        assertEquals(TokenType.CHARACTER, tokens.get(2).getType());
        
        assertEquals(TokenType.STRING, tokens.get(3).getType());
        assertEquals("Special [n] characters", tokens.get(3).getLiteral());
    }

    @Test
    void testIdentifiers() {
        // Test valid identifiers
        String source = "variable _variable variable123 _123 a_b_c";
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        for (int i = 0; i < 5; i++) {
            assertEquals(TokenType.IDENTIFIER, tokens.get(i).getType());
        }
        
        assertEquals("variable", tokens.get(0).getLexeme());
        assertEquals("_variable", tokens.get(1).getLexeme());
        assertEquals("variable123", tokens.get(2).getLexeme());
        assertEquals("_123", tokens.get(3).getLexeme());
        assertEquals("a_b_c", tokens.get(4).getLexeme());
    }

    @Test
    void testComments() {
        // Test comments
        String source = "-- This is a comment\nSUGOD -- This is another comment\nKATAPUSAN";
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        assertEquals(4, tokens.size()); // START, NEW_LINE, END, EOF
        assertEquals(TokenType.NEW_LINE, tokens.get(0).getType());
        assertEquals(TokenType.START, tokens.get(1).getType());
        assertEquals(TokenType.END, tokens.get(2).getType());
    }

    @Test
    void testComprehensiveProgram() {
        // Test a comprehensive program with multiple language features
        String source = 
            "SUGOD\n" +
            "    MUGNA NUMERO x, y, z = 5\n" +
            "    MUGNA LETRA a_1='n'\n" +
            "    MUGNA TINUOD t=\"OO\"\n" +
            "    x=y=4\n" +
            "    a_1='c'\n" +
            "    -- this is a comment\n" +
            "    IPAKITA: x & t & z & $ & a_1 & [n] & \"last\"\n" +
            "KATAPUSAN";
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        // Check a few key tokens
        assertTrue(tokens.size() > 20);
        assertEquals(TokenType.START, tokens.get(0).getType());
        
        int variableCount = 0;
        int declarationCount = 0;
        int commentCount = 0;
        
        for (Token token : tokens) {
            if (token.getType() == TokenType.IDENTIFIER) variableCount++;
            if (token.getType() == TokenType.DECLARE) declarationCount++;
            if (token.getType() == TokenType.COMMENT) commentCount++;
        }
        
        assertTrue(variableCount >= 5); // x, y, z, a_1, t
        assertTrue(declarationCount >= 3); // = operators
    }
    
    @Test
    void testNestedBlocksAndEscapes() {
        // Test nested blocks and escape sequences
        String source = 
            "SUGOD\n" +
            "    KUNG (x < 10)\n" +
            "    PUNDOK {\n" +
            "        KUNG (y > 5)\n" +
            "        PUNDOK {\n" +
            "            IPAKITA: \"Nested [[] blocks []]\" & $ & [#]\n" +
            "        }\n" +
            "    }\n" +
            "KATAPUSAN";
        
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        
        int braceCount = 0;
        int ifCount = 0;
        
        for (Token token : tokens) {
            if (token.getType() == TokenType.LEFT_BRACE || token.getType() == TokenType.RIGHT_BRACE) braceCount++;
            if (token.getType() == TokenType.IF) ifCount++;
        }
        
        assertEquals(4, braceCount); // 2 pairs of braces
        assertEquals(2, ifCount); // 2 KUNG statements
    }
}