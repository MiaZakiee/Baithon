# Welcome to Baithon :D

## Introduction

Baithon (Bisaya++) is a strongly–typed high–level interpreted Cebuano-based programming language developed to teach Cebuanos
the basics of programming. Its simple syntax and native keywords make programming easy to learn.

## Language Grammar

### Program Structure

- all codes are placed inside SUGOD and KATAPUSAN
- all variable declaration is starts with MUGNA
- all variable names are case-sensitive and starts with letter or an underscore (\_) and followed by a letter,
  underscore or digits.
- every line contains a single statement
- comments starts with double minus sign(--) and it can be placed anywhere in the program
- all reserved words are in capital letters and cannot be used as variable names
- dollar sign($) signifies next line or carriage return
- ampersand(&) serves as a concatenator
- the square braces([]) are as escape code

### Data Types

1. NUMERO - an ordinary number with no decimal part. It occupies 4 bytes in memory. (Integer)
2. LETRA - A single symbol. (Character)
3. TINUOD - Represents a literal true or false. (Boolean)
4. TIPIK - A number with a decimal part. (Float)

## Operators

### Arithmetic Operators

- ( ) &emsp;&emsp;&emsp; - Parenthesis
- \*, / , % &emsp; - Multiplication, Division, Modulo
- \+ . - &nbsp;&emsp;&emsp; - Addition, Subtraction
- \> ,< - &emsp;&ensp;&nbsp; - Greater than, Lesser than
- \>= ,<= - &ensp; - Greater than or equal to, Lesser than or equal to
- == ,<> - &ensp; - Equal, Not equal

### Logical Operators

- UG &emsp;- AND
- O &emsp;&ensp;- OR
- DILI &nbsp; - NOT

### Unary Operators

- \+ &emsp; - Positive
- \-&emsp;&ensp; - Negative

## How to run your program

to run your baithon file

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
   java -cp out/production/Baithon/ Main.Baithon SamplePrograms/test.txt

   ```
