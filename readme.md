# Baithon - A Cebuano-Based Programming Language

## Introduction

Baithon (Bisaya++) is a strongly-typed high-level interpreted Cebuano-based programming language developed to teach Cebuanos
the basics of programming. Its simple syntax and native keywords make programming easy to learn.

## Language Overview

### Core Features

- Native Cebuano keywords and syntax
- Strong static typing
- Block-scoped variables
- Control flow structures
- Functional programming concepts

## Language Grammar

### Program Structure

- All code is placed inside `SUGOD` and `KATAPUSAN` blocks
- All variable declarations start with `MUGNA`
- Variable names are case-sensitive and start with a letter or underscore (\_), followed by letters, underscores, or digits
- Every line contains a single statement
- Comments start with double minus sign (`--`) and can be placed anywhere in the program
- All reserved words are in capital letters and cannot be used as variable names
- Dollar sign (`$`) signifies next line or carriage return
- Ampersand (`&`) serves as a concatenator
- Square braces (`[]`) are used as escape code

## Data Types

| Keyword  | Description             | Example                |
| -------- | ----------------------- | ---------------------- |
| `NUMERO` | Integer value (4 bytes) | `MUGNA NUMERO x = 42`  |
| `TIPIK`  | Floating-point number   | `MUGNA TIPIK y = 3.14` |
| `LETRA`  | Single character        | `MUGNA LETRA c = 'A'`  |
| `TINUOD` | Boolean value (OO/DILI) | `MUGNA TINUOD z = OO`  |

## Operators

### Arithmetic Operators

- `( )` &emsp;&emsp;&emsp; - Parenthesis
- `*`, `/`, `%` &emsp; - Multiplication, Division, Modulo
- `+`, `-` &nbsp;&emsp;&emsp; - Addition, Subtraction
- `>`, `<` &emsp;&ensp;&nbsp; - Greater than, Less than
- `>=`, `<=` &ensp; - Greater than or equal to, Less than or equal to
- `==`, `<>` &ensp; - Equal to, Not equal to

### Logical Operators

- `UG` &emsp;&emsp; - Logical AND
- `O` &emsp;&emsp;&nbsp; - Logical OR
- `DILI` &nbsp;&nbsp; - Logical NOT

### Assignment Operators

- `=` &emsp;&emsp;&emsp;&nbsp; - Basic assignment
- `+=` &emsp;&emsp;&nbsp; - Add and assign
- `-=` &emsp;&emsp;&nbsp; - Subtract and assign
- `*=` &emsp;&emsp;&nbsp; - Multiply and assign
- `/=` &emsp;&emsp;&nbsp; - Divide and assign
- `%=` &emsp;&emsp;&nbsp; - Modulo and assign

### Increment/Decrement Operators

- `++` &emsp;&emsp;&nbsp; - Increment
- `--` &emsp;&emsp;&nbsp; - Decrement

### Special Operators

- `&` &emsp;&emsp;&emsp; - String concatenation
- `$` &emsp;&emsp;&emsp; - Newline character

## Control Structures

### Conditional Statements

```
KUNG (condition) {
    // code to execute if true
} KUNG PA (another_condition) {
    // code to execute if previous condition is false and this is true
} KUNG DILI {
    // code to execute if all conditions are false
}
```

### Loops

#### While Loop

```
MINTRAS (condition) {
    // code to repeat while condition is true
}
```

#### Do-While Loop

```
BUHATA
PUNDOK {
    // code to execute at least once
} MINTRAS (condition)
```

#### For Loop

```
ALANG SA (initializer, condition, increment) {
    // code to execute for each iteration
}
```

### Execution Blocks

- All execution blocks start with the key word `PUNDOK`

```
KUNG (CONDITON)
PUNDOK {
    STATEMENT
}
```

### Break and Continue

- `HUNONG` - Break out of a loop
- `PADAYON` - Skip to the next iteration

## Architecture

Baithon follows a classic interpreter architecture:

1. **Lexical Analysis** - Converts source code into tokens

   - `Scanner.java` - Scans input and produces tokens

2. **Syntactic Analysis** - Validates syntax and builds AST

   - `Parser.java` - Constructs AST following grammar rules

3. **Abstract Syntax Tree (AST)**

   - `Expr.java` - Expression nodes
   - `Stmt.java` - Statement nodes

4. **Runtime Environment**

   - `Environment.java` - Manages variable scopes and values

5. **Interpreter**
   - `Interpreter.java` - Executes the program by traversing the AST
   - `RunTimeError.java` - Handles runtime exceptions

## How to Run Your Program

1. Change directory to Baithon

   ```
   cd /path/to/Baithon
   ```

2. Compile Java source code

   ```
   javac -d out/production/Baithon -cp src src/**/*.java
   ```

3. Place your program in SamplePrograms folder

   ```
   Baithon/SamplePrograms
   ```

4. Run your program
   ```
   java -cp out/production/Baithon/ Main.Baithon SamplePrograms/test.by
   ```

## Examples

### Sample program
```
SUGOD
    MUGNA NUMERO x, y, z = 5
    MUGNA LETRA a_1='n'
    MUGNA TINUOD t="OO"
    x=y=4
    a_1='c'
    -- this is a comment
    IPAKITA: x & t & z & $ & a_1 & [#] & "last"
KATAPUSAN
```
### Output
```
4OO5
c#last
```

### Program with arithmetic operation
```
SUGOD
    MUGNA NUMERO xyz, abc=100
    xyz= ((abc *5)/10 + 10) * -1
    IPAKITA: [[] & xyz & []]
KATAPUSAN
```

### Output
```
[-60]
```

### Program with logical operation
```
SUGOD
    MUGNA NUMERO a=100, b=200, c=300 
    MUGNA TINUOD d="DILI"
    d = (a < b UG c <>200)
    IPAKITA: d
KATAPUSAN
```

### Output
```
OO
```

### Program with logical operation
```
SUGOD
    MUGNA NUMERO a=100, b=200, c=300 
    MUGNA TINUOD d="DILI"
    d = (a < b UG c <>200)
    IPAKITA: d
KATAPUSAN
```

### Output
```
OO
```