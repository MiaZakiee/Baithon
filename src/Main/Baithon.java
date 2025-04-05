package Main;

import Interpreter.Interpreter;
import Interpreter.RunTimeError;
import Lexers.Scanner;
import Lexers.Token;
import Parsers.Parser;
import Parsers.Stmt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
public class Baithon {
  // checks if there are any errors
  static boolean hadError = false;
  // checks if there are any runtime errors
  static boolean hadRuntimeError = false;

  // Interpreter instance
  public static final Interpreter interpreter = new Interpreter();

  // This is the main function that runs the program
  // It takes the command line arguments and runs the program
  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64); 
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  // This function runs the file passed as an argument
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));
  }

  // This function runs the prompt
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (;;) { 
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null) break;
      run(line);
      hadError = false;
    }
  }

  // This function runs the source code passed as an argument
  private static void run(String source) {
    if (hadError) System.exit(65);
    if (hadRuntimeError) System.exit(70);
    
    // Lexical Analysis
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    // debugging
    // for (Token token : tokens) {
    //   System.out.println(token);
    // }

    // Parsing
    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    // if any errors were found, stop printing tokens
    if (hadError) return;

    // Interpret the expression
    interpreter.interpret(statements); 

    // System.out.println(new AstPrinter().print(expression));

    // Print tokens
    // for (Token token : tokens) {
      // System.out.println(token);
    // }
  }

  public static void error(int line, String message) {
    report(line, "", message);
  }


  public static void runTimeError(RunTimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.getLine() + "]");
    hadRuntimeError = true;
  }

  private static void report(int line, String where,
                             String message) {
    System.err.println(
        "[line " + line + "] Error" + where + ": " + message);
    hadError = true;
  }

}

