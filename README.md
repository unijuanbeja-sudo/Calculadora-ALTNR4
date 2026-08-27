# Calculadora Científica con ANTLR

## Lenguajes de Programación y Traducción

Este proyecto consiste en el desarrollo progresivo de una calculadora científica utilizando **ANTLR** y el patrón de diseño **Visitor**.

El objetivo es construir un pequeño lenguaje matemático capaz de interpretar expresiones, almacenar variables, ejecutar funciones matemáticas y, en etapas posteriores, representar funciones gráficamente.

La arquitectura general utilizada por el intérprete es:

```text
Entrada del usuario
        ↓
      Lexer
        ↓
      Tokens
        ↓
      Parser
        ↓
Árbol sintáctico
        ↓
     Visitor
        ↓
    Resultado
```

## 1. Estructura inicial del proyecto

Se creó la carpeta principal del proyecto con los archivos base necesarios para desarrollar la calculadora científica con ANTLR.

La estructura inicial es:

```text
CalculadoraCientifica/
├── ScientificCalc.g4
├── Main.java
├── ScientificEvalVisitor.java
├── PlotWindow.java
├── ejemplos.txt
└── README.md
```
<img width="660" height="206" alt="image" src="https://github.com/user-attachments/assets/3248df74-77b8-40ed-81a8-4fcc58d3daf8" />

## 2. Gramática inicial del lenguaje

La primera versión de la gramática se definió en el archivo `ScientificCalc.g4`.

Esta gramática permite reconocer expresiones matemáticas básicas, números reales, identificadores, asignaciones de variables y expresiones entre paréntesis.

```antlr
grammar ScientificCalc;

prog
    : stat+ EOF
    ;

stat
    : expr NEWLINE          # printExpr
    | ID '=' expr NEWLINE   # assign
    | NEWLINE               # blank
    ;

expr
    : expr op=('*'|'/') expr   # mulDiv
    | expr op=('+'|'-') expr   # addSub
    | NUMBER                   # number
    | ID                       # id
    | '(' expr ')'             # parens
    ;

MUL : '*';
DIV : '/';
ADD : '+';
SUB : '-';

NUMBER
    : [0-9]+ ('.' [0-9]+)?
    ;

ID
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;

NEWLINE
    : '\r'? '\n'
    ;

WS
    : [ \t]+ -> skip
    ;
```

### Explicación de las reglas

La regla `prog` representa el programa completo. Permite procesar una o más instrucciones hasta llegar al final de la entrada mediante `EOF`.

```antlr
prog
    : stat+ EOF
    ;
```

La regla `stat` representa las instrucciones que puede recibir inicialmente la calculadora:

```antlr
stat
    : expr NEWLINE          # printExpr
    | ID '=' expr NEWLINE   # assign
    | NEWLINE               # blank
    ;
```

Sus alternativas permiten:

* evaluar una expresión y mostrar su resultado;
* asignar el resultado de una expresión a una variable;
* aceptar líneas vacías.

La regla `expr` define las expresiones matemáticas:

```antlr
expr
    : expr op=('*'|'/') expr   # mulDiv
    | expr op=('+'|'-') expr   # addSub
    | NUMBER                   # number
    | ID                       # id
    | '(' expr ')'             # parens
    ;
```

Esta regla permite reconocer:

* multiplicaciones;
* divisiones;
* sumas;
* restas;
* números;
* identificadores;
* expresiones entre paréntesis.

Las etiquetas como:

```text
# mulDiv
# addSub
# number
# id
# parens
```

permiten a ANTLR identificar diferentes tipos de nodos en el árbol sintáctico y posteriormente generar métodos específicos en el patrón Visitor.

### Reconocimiento de números

La regla:

```antlr
NUMBER
    : [0-9]+ ('.' [0-9]+)?
    ;
```

permite reconocer números enteros y reales.

Ejemplos válidos:

```text
10
25
3.14
100.5
0.25
```

### Reconocimiento de identificadores

La regla:

```antlr
ID
    : [a-zA-Z_][a-zA-Z_0-9]*
    ;
```

indica que un identificador debe comenzar con una letra o con `_`.

Después del primer carácter puede contener letras, números o `_`.

Ejemplos válidos:

```text
variable
x2
_resultado
radio
variable_2
```

Ejemplos no válidos:

```text
2x
variable-final
```

`2x` no es válido porque comienza con un número.

`variable-final` no es válido porque el carácter `-` no pertenece a la regla `ID`.

### Manejo de saltos de línea y espacios

La regla `NEWLINE` reconoce el final de cada instrucción:

```antlr
NEWLINE
    : '\r'? '\n'
    ;
```

La regla `WS` reconoce espacios y tabulaciones y los descarta:

```antlr
WS
    : [ \t]+ -> skip
    ;
```

Esto permite escribir expresiones con espacios sin afectar su interpretación.

### Evidencia de la gramática

> **CAPTURA 02 - Gramática inicial del lenguaje**
>
> Insertar aquí la captura de `ScientificCalc.g4` donde se observe la gramática inicial y la estructura del proyecto en el explorador.

kali



<img width="1155" height="805" alt="image" src="https://github.com/user-attachments/assets/d5cc790c-d505-4b84-aa93-e3be73b10c5f" />






## 3. Generación del Lexer, Parser y Visitor

Después de definir la gramática en `ScientificCalc.g4`, se utilizó ANTLR para generar automáticamente los componentes necesarios para procesar el lenguaje.

Se ejecutó el siguiente comando:

```bash
antlr4 -no-listener -visitor ScientificCalc.g4
```

La opción `-visitor` indica a ANTLR que genere las clases necesarias para utilizar el patrón de diseño **Visitor**.

La opción `-no-listener` evita la generación del patrón Listener, ya que en este proyecto se utilizará Visitor para recorrer y evaluar el árbol sintáctico.

Después de ejecutar el comando se generaron, entre otros, los siguientes archivos:

```text
ScientificCalcLexer.java
ScientificCalcParser.java
ScientificCalcVisitor.java
ScientificCalcBaseVisitor.java
ScientificCalc.tokens
ScientificCalcLexer.tokens
ScientificCalc.interp
ScientificCalcLexer.interp
```

### Componentes generados

`ScientificCalcLexer.java` corresponde al **Lexer**. Su responsabilidad es analizar la entrada y convertir los caracteres en tokens reconocidos por la gramática.

Por ejemplo, para una entrada:

```text
10 + 20
```

el Lexer puede reconocer elementos como:

```text
NUMBER
ADD
NUMBER
```

`ScientificCalcParser.java` corresponde al **Parser**. Este recibe los tokens producidos por el Lexer y comprueba cómo se relacionan según las reglas de la gramática.

A partir de estas reglas construye un **árbol sintáctico**.

`ScientificCalcVisitor.java` define las operaciones de visita que pueden realizarse sobre los diferentes tipos de nodos del árbol sintáctico.

`ScientificCalcBaseVisitor.java` proporciona una implementación base del Visitor que posteriormente será extendida por nuestra clase `ScientificEvalVisitor`.

### Relación entre la gramática y el Visitor

Las etiquetas utilizadas en la gramática:

```antlr
# printExpr
# assign
# blank
# mulDiv
# addSub
# number
# id
# parens
```

provocan que ANTLR genere métodos específicos para esos tipos de nodos.

Por ejemplo:

```text
# printExpr  → visitPrintExpr(...)
# assign     → visitAssign(...)
# mulDiv     → visitMulDiv(...)
# addSub     → visitAddSub(...)
# number     → visitNumber(...)
# id         → visitId(...)
# parens     → visitParens(...)
```

Esto permite separar el reconocimiento de la estructura del lenguaje de la lógica utilizada para evaluar cada expresión.

### Flujo hasta este punto

```text
ScientificCalc.g4
        ↓
      ANTLR
        ↓
 ┌──────────────┐
 │    Lexer     │
 ├──────────────┤
 │    Parser    │
 ├──────────────┤
 │   Visitor    │
 └──────────────┘
```

Los archivos generados por ANTLR no deben modificarse manualmente. Cuando se modifique la gramática, estos archivos deberán volver a generarse.

### Evidencia

> **CAPTURA 03 - Generación de archivos con ANTLR**
>
> Insertar aquí la captura de la terminal donde se observe la ejecución de:
>
> `antlr4 -no-listener -visitor ScientificCalc.g4`
>
> y posteriormente el comando `ls` mostrando los archivos generados.

<!-- Insertar aquí: captura_03_generacion_antlr.png -->

<img width="659" height="214" alt="image" src="https://github.com/user-attachments/assets/b79d11c6-21e3-4cb2-914e-618f940950c4" />



## 4. Implementación del Visitor

La lógica de evaluación del lenguaje se implementa en la clase `ScientificEvalVisitor`, la cual extiende la clase `ScientificCalcBaseVisitor<Double>` generada por ANTLR.

```java
public class ScientificEvalVisitor
extends ScientificCalcBaseVisitor<Double> {
```

El tipo `Double` indica que los métodos del Visitor devolverán valores numéricos reales.

Además, se define inicialmente una estructura de memoria:

```java
Map<String, Double> memory = new HashMap<>();
```

Esta estructura se utilizará posteriormente como tabla de símbolos para almacenar variables y sus respectivos valores.

### Interpretación de números

Para procesar los tokens reconocidos por la gramática como `NUMBER`, se implementó el método:

```java
@Override
public Double visitNumber(
ScientificCalcParser.NumberContext ctx) {

    return Double.parseDouble(
    ctx.NUMBER().getText()
    );
}
```

El proceso de evaluación es:

```text
Token NUMBER
     ↓
getText()
     ↓
Texto original
     ↓
Double.parseDouble()
     ↓
Valor numérico
```

Por ejemplo:

```text
"3.1416"
```

se convierte en:

```text
3.1416
```

De esta forma, la calculadora puede trabajar con números enteros y números reales.


<img width="846" height="359" alt="image" src="https://github.com/user-attachments/assets/c42e42f9-b9cb-48f3-a38d-cfeb5b9ba564" />


### Suma y resta

Para evaluar operaciones de suma y resta se implementó el método `visitAddSub`.

```java
@Override
public Double visitAddSub(
ScientificCalcParser.AddSubContext ctx) {

    double left = visit(ctx.expr(0));
    double right = visit(ctx.expr(1));

    if (ctx.op.getType() == ScientificCalcParser.ADD) {
        return left + right;
    }

    return left - right;
}
```

Las instrucciones:

```java
visit(ctx.expr(0))
visit(ctx.expr(1))
```

permiten visitar los dos subárboles que forman la operación.

Por ejemplo, para:

```text
10 + 20
```

el árbol puede representarse de forma simplificada como:

```text
      +
     / \
   10   20
```

El Visitor evalúa primero ambos operandos y posteriormente ejecuta la operación correspondiente.

```text
left  = 10
right = 20

resultado = 30
```

### Multiplicación y división

Siguiendo la misma estrategia se implementó `visitMulDiv`.

```java
@Override
public Double visitMulDiv(
ScientificCalcParser.MulDivContext ctx) {

    double left = visit(ctx.expr(0));
    double right = visit(ctx.expr(1));

    if (ctx.op.getType() == ScientificCalcParser.MUL) {
        return left * right;
    }

    return left / right;
}
```

El método identifica el operador reconocido por la gramática y realiza multiplicación o división según corresponda.

Ejemplos:

```text
10 * 5 = 50
20 / 4 = 5
```

De esta forma, la lógica de las operaciones se mantiene separada de la gramática: la gramática define qué expresiones son válidas y el Visitor define cómo deben evaluarse.

## 4. Implementación del Visitor aritmético

La lógica de evaluación de las expresiones se implementa en la clase `ScientificEvalVisitor`, que extiende la clase `ScientificCalcBaseVisitor<Double>` generada automáticamente por ANTLR.

```java
public class ScientificEvalVisitor
extends ScientificCalcBaseVisitor<Double> {
```

El tipo `Double` indica que los métodos del Visitor devolverán valores numéricos reales.

También se declara una estructura de memoria:

```java
Map<String, Double> memory = new HashMap<>();
```

Esta estructura será utilizada posteriormente como tabla de símbolos para almacenar variables.

### Interpretación de números

Para procesar los tokens reconocidos como `NUMBER`, se implementó el método:

```java
@Override
public Double visitNumber(
ScientificCalcParser.NumberContext ctx) {

    return Double.parseDouble(
    ctx.NUMBER().getText()
    );
}
```

El método obtiene el texto reconocido por ANTLR y lo convierte a un valor numérico de tipo `Double`.

Por ejemplo:

```text
"3.1416"
```

se convierte en:

```text
3.1416
```

El proceso puede representarse como:

```text
NUMBER
   ↓
getText()
   ↓
"3.1416"
   ↓
Double.parseDouble()
   ↓
3.1416
```

### Suma y resta

Para evaluar sumas y restas se implementó `visitAddSub`.

```java
@Override
public Double visitAddSub(
ScientificCalcParser.AddSubContext ctx) {

    double left = visit(ctx.expr(0));
    double right = visit(ctx.expr(1));

    if (ctx.op.getType() == ScientificCalcParser.ADD) {
        return left + right;
    }

    return left - right;
}
```

Las instrucciones:

```java
visit(ctx.expr(0))
visit(ctx.expr(1))
```

permiten visitar los dos subárboles que forman la operación.

Por ejemplo:

```text
10 + 20
```

puede representarse como:

```text
      +
     / \
   10   20
```

El Visitor obtiene:

```text
left  = 10
right = 20
```

y posteriormente calcula:

```text
10 + 20 = 30
```

### Multiplicación y división

La multiplicación y la división utilizan la misma estrategia mediante `visitMulDiv`.

```java
@Override
public Double visitMulDiv(
ScientificCalcParser.MulDivContext ctx) {

    double left = visit(ctx.expr(0));
    double right = visit(ctx.expr(1));

    if (ctx.op.getType() == ScientificCalcParser.MUL) {
        return left * right;
    }

    return left / right;
}
```

Ejemplos de operaciones que podrá evaluar:

```text
10 * 5 = 50
20 / 4 = 5
```

El Visitor identifica el operador reconocido por la gramática y ejecuta la operación correspondiente.

### Manejo de paréntesis

Para las expresiones entre paréntesis se implementó:

```java
@Override
public Double visitParens(
ScientificCalcParser.ParensContext ctx) {

    return visit(ctx.expr());
}
```

El método visita directamente la expresión que se encuentra dentro de los paréntesis.

Por ejemplo:

```text
(2 + 3)
```

evalúa internamente:

```text
2 + 3
```

y devuelve:

```text
5
```

Los paréntesis son importantes porque modifican la estructura del árbol sintáctico y, por tanto, el orden de evaluación.

Por ejemplo:

```text
2 + 3 * 4
```

produce:

```text
14
```

mientras que:

```text
(2 + 3) * 4
```

produce:

```text
20
```

Esto permite respetar la precedencia de las operaciones matemáticas.

### Código del Visitor hasta esta etapa

```java
import java.util.HashMap;
import java.util.Map;

public class ScientificEvalVisitor
extends ScientificCalcBaseVisitor<Double> {

    Map<String, Double> memory = new HashMap<>();

    @Override
    public Double visitNumber(
    ScientificCalcParser.NumberContext ctx) {

        return Double.parseDouble(
        ctx.NUMBER().getText()
        );
    }

    @Override
    public Double visitAddSub(
    ScientificCalcParser.AddSubContext ctx) {

        double left = visit(ctx.expr(0));
        double right = visit(ctx.expr(1));

        if (ctx.op.getType() == ScientificCalcParser.ADD) {
            return left + right;
        }

        return left - right;
    }

    @Override
    public Double visitMulDiv(
    ScientificCalcParser.MulDivContext ctx) {

        double left = visit(ctx.expr(0));
        double right = visit(ctx.expr(1));

        if (ctx.op.getType() == ScientificCalcParser.MUL) {
            return left * right;
        }

        return left / right;
    }

    @Override
    public Double visitParens(
    ScientificCalcParser.ParensContext ctx) {

        return visit(ctx.expr());
    }
}
```

### Evidencia

<img width="635" height="737" alt="image" src="https://github.com/user-attachments/assets/56df496a-0a87-420b-b2b7-5ab55ac78e69" />



## 5. Programa principal

El archivo `Main.java` se encarga de conectar todos los componentes generados por ANTLR con el Visitor encargado de evaluar las expresiones.

El código utilizado es:

```java
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {

    public static void main(String[] args) throws Exception {

        CharStream input =
        CharStreams.fromStream(System.in);

        ScientificCalcLexer lexer =
        new ScientificCalcLexer(input);

        CommonTokenStream tokens =
        new CommonTokenStream(lexer);

        ScientificCalcParser parser =
        new ScientificCalcParser(tokens);

        ParseTree tree =
        parser.prog();

        ScientificEvalVisitor visitor =
        new ScientificEvalVisitor();

        visitor.visit(tree);
    }
}
```

### Flujo de ejecución

El programa principal implementa el siguiente flujo:

```text
Entrada del usuario
        ↓
    CharStream
        ↓
      Lexer
        ↓
      Tokens
        ↓
      Parser
        ↓
 Árbol sintáctico
        ↓
     Visitor
        ↓
    Evaluación
```

`CharStreams.fromStream(System.in)` obtiene la entrada escrita por el usuario.

`ScientificCalcLexer` analiza los caracteres de entrada y los transforma en tokens.

`CommonTokenStream` almacena los tokens producidos por el Lexer.

`ScientificCalcParser` analiza la relación entre esos tokens según las reglas definidas en `ScientificCalc.g4`.

`parser.prog()` inicia el análisis desde la regla principal `prog` y construye el árbol sintáctico.

Finalmente, `ScientificEvalVisitor` recorre dicho árbol y ejecuta la lógica correspondiente a cada tipo de expresión.

### Evidencia

<img width="730" height="595" alt="image" src="https://github.com/user-attachments/assets/49503c9b-2685-48c8-84ca-e6a06c34633b" />


## 6. Mostrar los resultados

Para mostrar en pantalla el resultado de cada expresión evaluada, se implementó el método `visitPrintExpr`.

```java
@Override
public Double visitPrintExpr(
ScientificCalcParser.PrintExprContext ctx) {

    double value = visit(ctx.expr());

    System.out.println(value);

    return value;
}
```

El método realiza tres pasos:

1. Visita la expresión reconocida por el Parser.
2. Obtiene el resultado de la evaluación.
3. Imprime el valor en pantalla.

El flujo puede representarse como:

```text
Expresión
   ↓
visit(ctx.expr())
   ↓
Resultado
   ↓
System.out.println(value)
   ↓
Salida en pantalla
```

Por ejemplo, para la entrada:

```text
2+2
```

el resultado esperado es:

```text
4.0
```

Este método permite que las expresiones escritas por el usuario produzcan una salida visible en la terminal.

### Evidencia

<img width="555" height="219" alt="image" src="https://github.com/user-attachments/assets/ba36d923-d283-42ea-b6f9-6aea597860a4" />


## 7. Primera ejecución de la calculadora

Después de implementar el programa principal y el método `visitPrintExpr`, se compiló el proyecto incluyendo el runtime de ANTLR en el classpath.

El comando de compilación utilizado fue:

```bash
javac -cp ".:/usr/share/java/antlr4-runtime.jar" *.java
```

Posteriormente se ejecutó el programa con:

```bash
java -cp ".:/usr/share/java/antlr4-runtime.jar" Main
```

Como primera prueba se ingresó:

```text
2+2
```

y la calculadora produjo:

```text
4.0
```

Esto confirma que el flujo completo funciona correctamente:

```text
Entrada
  ↓
Lexer
  ↓
Tokens
  ↓
Parser
  ↓
Árbol sintáctico
  ↓
Visitor
  ↓
Resultado
```

### Evidencia

> **CAPTURA 06 - Primera ejecución de la calculadora**

<img width="616" height="85" alt="image" src="https://github.com/user-attachments/assets/73523f82-8ea5-4f0e-80a5-859ed5ce4498" />


## 8. Pruebas de operaciones aritméticas

Se realizaron pruebas para comprobar el funcionamiento de las operaciones implementadas en el Visitor.

Las expresiones utilizadas fueron:

```text
2+2
10-3
10*5
20/4
2+3*4
(2+3)*4
```

Los resultados obtenidos fueron:

| Expresión | Resultado |
| --------- | --------: |
| `2+2`     |     `4.0` |
| `10-3`    |     `7.0` |
| `10*5`    |    `50.0` |
| `20/4`    |     `5.0` |
| `2+3*4`   |    `14.0` |
| `(2+3)*4` |    `20.0` |

### Comprobación de precedencia

La expresión:

```text
2+3*4
```

produce:

```text
14.0
```

Esto ocurre porque la multiplicación se evalúa antes que la suma:

```text
2 + (3 * 4)
2 + 12
14
```

En cambio:

```text
(2+3)*4
```

produce:

```text
20.0
```

Los paréntesis modifican el árbol sintáctico y obligan a evaluar primero:

```text
(2 + 3) * 4
5 * 4
20
```

Esta prueba confirma que la gramática respeta correctamente la precedencia de los operadores y el uso de paréntesis.

### Evidencia

> **CAPTURA 07 - Pruebas de operaciones aritméticas**

<img width="651" height="242" alt="image" src="https://github.com/user-attachments/assets/5e2801b9-1e5d-4740-b53a-c91dfdfc8030" />

## 9. Manejo de variables

Para permitir el uso de variables, el Visitor utiliza una estructura de memoria basada en un `Map`.

```java
Map<String, Double> memory = new HashMap<>();
```

Esta estructura funciona como una **tabla de símbolos**, donde cada identificador se asocia con un valor numérico.

Por ejemplo:

```text
Identificador    Valor
a                10.0
b                20.0
radio            5.5
```

### Asignación de variables

Para almacenar variables se implementó el método `visitAssign`.

```java
@Override
public Double visitAssign(
ScientificCalcParser.AssignContext ctx) {

    String id = ctx.ID().getText();

    double value = visit(ctx.expr());

    memory.put(id, value);

    return value;
}
```

El método obtiene el nombre del identificador, evalúa la expresión asignada y almacena ambos valores en `memory`.

Por ejemplo:

```text
a = 10
```

produce conceptualmente:

```text
id = "a"
value = 10.0
```

y posteriormente:

```java
memory.put("a", 10.0);
```

### Recuperación de variables

Para utilizar una variable previamente almacenada se implementó `visitId`.

```java
@Override
public Double visitId(
ScientificCalcParser.IdContext ctx) {

    String id = ctx.ID().getText();

    if (memory.containsKey(id)) {
        return memory.get(id);
    }

    System.err.println(
    "Variable no definida: " + id
    );

    return 0.0;
}
```

El método busca el identificador dentro de la tabla de símbolos.

Si la variable existe, devuelve su valor.

Por ejemplo:

```text
a = 10
a
```

permite recuperar:

```text
10.0
```

Si el identificador no existe, el programa muestra:

```text
Variable no definida: nombre
```

y devuelve `0.0`.

### Flujo de una asignación

```text
a = 10
   ↓
Parser reconoce una asignación
   ↓
visitAssign(...)
   ↓
Obtiene ID
   ↓
Evalúa la expresión
   ↓
memory.put(id, value)
```

### Evidencia

> **CAPTURA 08 - Implementación de variables**

<img width="802" height="579" alt="image" src="https://github.com/user-attachments/assets/87c2d1f4-82e2-4589-9d29-6c1616b6d7bd" />

## 10. Pruebas de variables

Para comprobar el funcionamiento de la tabla de símbolos se realizaron asignaciones y operaciones entre variables.

Las instrucciones utilizadas fueron:

```text
a = 10
b = 20
a+b
a*b
```

Las variables se almacenan internamente en la estructura:

```java
Map<String, Double> memory = new HashMap<>();
```

Después de las asignaciones, la memoria contiene conceptualmente:

```text
a → 10.0
b → 20.0
```

Al evaluar:

```text
a+b
```

el Visitor recupera ambos valores de `memory` y obtiene:

```text
30.0
```

Al evaluar:

```text
a*b
```

se obtiene:

```text
200.0
```

Esto comprueba que el intérprete puede almacenar variables, recuperar sus valores y utilizarlas dentro de expresiones matemáticas.

### Variable no definida

Si se utiliza un identificador que no ha sido almacenado previamente, `visitId` muestra un mensaje de error:

```text
Variable no definida: resultado
```

Con la implementación actual, el método devuelve `0.0` cuando una variable no existe.

Por ejemplo:

```text
resultado + 10
```

utiliza `0.0` como valor de `resultado`.

Esta situación permite analizar si resulta más adecuado devolver cero o generar un error que detenga la evaluación.

### Evidencia

> **CAPTURA 09 - Funcionamiento de variables**
>
<img width="644" height="140" alt="image" src="https://github.com/user-attachments/assets/33bfd9b7-0e92-4978-a607-fe3aaf8a719d" />

## Etapa 2 — Calculadora científica 

En esta etapa se amplió la gramática y el Visitor recibidos del Integrante 1
para convertir la calculadora básica en una calculadora científica completa,
siguiendo las secciones 17 a 26 del tutorial.

### Funcionalidades agregadas
- Potencia (`^`), asociativa a la derecha (ej. `2^8` → `256.0`).
- Operadores unarios (`-x`, `+x`).
- Funciones matemáticas: `sin`, `cos`, `tan`, `sqrt`, `log`, `ln`, `abs`, `exp`.
- Constantes matemáticas `pi` y `e`.
- Comando `clear` (borra la memoria de variables).
- Comando `vars` (muestra las variables definidas en memoria).

### Archivos modificados
- `ScientificCalc.g4`
- `ScientificEvalVisitor.java`
- Archivos regenerados automáticamente por ANTLR: `ScientificCalcLexer.java`,
  `ScientificCalcParser.java`, `ScientificCalcVisitor.java`,
  `ScientificCalcBaseVisitor.java`

### Pruebas realizadas

| Expresión | Resultado esperado | Resultado obtenido |
|---|---|---|
| `2^8` | 256.0 | correcto |
| `sqrt(25)` | 5.0 | correcto |
| `cos(0)` | 1.0 | correcto |
| `log(100)` | 2.0 | correcto |
| `abs(-10)` | 10.0 | correcto |
| `pi` | 3.141592653589793 | correcto |
| `2*pi` | 6.283185307179586 | correcto |
| `sin(pi/2)` | 1.0 | correcto |
| `ln(e)` | 1.0 | correcto |
| `-10` | -10.0 | correcto |
| `-2+5` | 3.0 | correcto |
| `vars` (con a=10, b=20) | Muestra `a = 10.0` / `b = 20.0` | correcto |
| `clear` | "Memoria eliminada." | correcto |
| `vars` (después de clear) | "No hay variables definidas." | correcto |

### Evidencias

### Potencia y funciones
<img width="1275" height="426" alt="potencias" src="https://github.com/user-attachments/assets/5259d25e-68ac-4e1e-ac08-afdb8d1bd62c" />

### Constantes y operadores unarios

<img width="837" height="201" alt="unitarios" src="https://github.com/user-attachments/assets/a794a13b-12b2-41e0-92d5-05f1dd35e125" />

<img width="876" height="276" alt="con1" src="https://github.com/user-attachments/assets/cf7f329b-46dc-4e88-aa46-bcfb899230a0" />

<img width="1284" height="156" alt="con2" src="https://github.com/user-attachments/assets/1f4994c1-e44b-4d13-a082-3f13abcf0c04" />


### Comandos clear y vars
<img width="1281" height="225" alt="com1" src="https://github.com/user-attachments/assets/11df94f5-d421-46c1-83ff-6babb1e8a4f3" />


<img width="1272" height="153" alt="com" src="https://github.com/user-attachments/assets/616af21c-0053-4c19-aaea-1b6bd37888d5" />



