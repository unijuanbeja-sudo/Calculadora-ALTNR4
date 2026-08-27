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

## Etapa 3 — Graficación y cierre del lenguaje
 
En esta etapa se completó el tutorial siguiendo las secciones 27 a 44: se agregó el comando `plot`, se implementó la ventana gráfica con Swing, se realizaron las pruebas finales de todo el lenguaje y se documentaron las preguntas de cierre, los retos y la reflexión final.
 
### Motivación: evaluar la misma expresión muchas veces
 
Hasta la etapa anterior, una expresión como `sin(pi/2)` se evaluaba una única vez y producía un único número. Para dibujar `y = sin(x)` es necesario evaluar la **misma expresión** para muchos valores distintos de `x`:
 
```text
Asignar valor a x
        ↓
Visitar el mismo árbol
        ↓
Obtener y
        ↓
Cambiar x
        ↓
Visitar nuevamente
```
 
Esto es posible porque `x` no es una constante grabada en el árbol sintáctico: es una variable que se busca en la tabla de símbolos (`memory`) en cada visita. Cambiar el valor asociado a `"x"` en el mapa entre una visita y otra permite reutilizar el árbol sin volver a analizar el texto de la expresión.
 
### Diseño del comando `plot`
 
Se agregó a la regla `stat` la alternativa:
 
```antlr
stat
    : expr NEWLINE                                   # printExpr
    | ID '=' expr NEWLINE                            # assign
    | 'clear' NEWLINE                                # clear
    | 'vars' NEWLINE                                 # showVars
    | 'plot' '(' expr ',' expr ',' expr ')' NEWLINE  # plotExpr
    | NEWLINE                                        # blank
    ;
```
 
La sintaxis soportada es:
 
```text
plot(expresion, xmin, xmax)
```
 
Por ejemplo:
 
```text
plot(sin(x), -6.28, 6.28)
plot(x^2, -10, 10)
```
 
ANTLR reconoce tres subexpresiones dentro de los paréntesis, accesibles como:
 
| Índice | Contenido |
|---|---|
| `ctx.expr(0)` | la función a graficar, p. ej. `sin(x)` |
| `ctx.expr(1)` | `xmin` |
| `ctx.expr(2)` | `xmax` |
 
Se regeneró el proyecto con:
 
```bash
antlr4 -no-listener -visitor ScientificCalc.g4
```
 
lo que agrega a `ScientificCalcVisitor.java` y `ScientificCalcBaseVisitor.java` el método `visitPlotExpr(...)`.
 
### Evidencia
 
**Captura 10 - Gramática con el comando `plot`**
 
<img width="572" height="187" alt="image" src="https://github.com/user-attachments/assets/8c44d55c-37d7-4905-ab06-a7ec732e533d" />

 
 
### Muestreo de la función e implementación de `visitPlotExpr`
 
Para dibujar una curva continua se toman 800 muestras equiespaciadas entre `xmin` y `xmax`:
 
```text
xi = xmin + i * (xmax - xmin) / (N - 1)
```
 
En cada muestra se actualiza la tabla de símbolos y se vuelve a visitar el árbol de la expresión:
 
```java
@Override
public Double visitPlotExpr(
ScientificCalcParser.PlotExprContext ctx) {
 
    double xmin = visit(ctx.expr(1));
    double xmax = visit(ctx.expr(2));
 
    int samples = 800;
 
    List<Double> xs = new ArrayList<>();
    List<Double> ys = new ArrayList<>();
 
    for (int i = 0; i < samples; i++) {
 
        double x = xmin + i * (xmax - xmin) / (samples - 1);
 
        memory.put("x", x);
 
        double y = visit(ctx.expr(0));
 
        if (Double.isFinite(y)) {
            xs.add(x);
            ys.add(y);
        }
    }
 
    new PlotWindow(xs, ys);
 
    return 0.0;
}
```
 
### El problema de las discontinuidades
 
Al graficar `plot(1/x, -5, 5)`, cuando `x` se aproxima a `0`, Java puede producir `Infinity`, `-Infinity` o `NaN`. Estos valores no se pueden ubicar en un plano cartesiano de píxeles.
 
**Solución aplicada (reto de la sección 32):** antes de agregar cada muestra a las listas `xs`/`ys` se comprueba `Double.isFinite(y)`, descartando silenciosamente los puntos no válidos:
 
```java
if (Double.isFinite(y)) {
    xs.add(x);
    ys.add(y);
}
```
 
### Evidencia
 
**Captura 11 - Implementación de `visitPlotExpr`**
 
<img width="448" height="777" alt="image" src="https://github.com/user-attachments/assets/2f2f669d-20df-4a5d-ae3e-822acc2e9e07" />
 
 
 
### Ventana gráfica (`PlotWindow.java`)
 
Se implementó `PlotWindow` como un `JPanel` embebido en un `JFrame` de 800×600 píxeles. El flujo de `paintComponent` es:
 
1. Calcular `xmin`, `xmax`, `ymin`, `ymax` a partir de las listas de puntos ya filtradas.
2. Transformar cada coordenada matemática a coordenadas de píxel:
```java
int px = (int)((x - xmin) / (xmax - xmin) * getWidth());
int py = getHeight() - (int)((y - ymin) / (ymax - ymin) * getHeight());
```
 
El término `getHeight() -` es necesario porque en Java2D el eje Y de pantalla crece hacia abajo, mientras que en el plano cartesiano crece hacia arriba.
 
3. Recorrer los puntos consecutivos y unirlos con `g2.drawLine(px1, py1, px2, py2)`, formando la curva.
### Evidencia
 
**Captura 12 - Código de `PlotWindow.java`**
 
<img width="463" height="577" alt="image" src="https://github.com/user-attachments/assets/5d37246b-a134-4f9e-97ff-69660aa21333" />

<img width="476" height="752" alt="image" src="https://github.com/user-attachments/assets/ce197e07-dbaf-49c9-b869-0236fc91452b" />

<img width="272" height="810" alt="image" src="https://github.com/user-attachments/assets/3f4af079-60a2-43b2-ab62-1eaf8e5eae84" />

 
 
 
### Primera gráfica
 
Se compiló y ejecutó el proyecto:
 
```bash
javac -cp ".:antlr-4.11.1-complete.jar" *.java
java  -cp ".:antlr-4.11.1-complete.jar" Main
```
 
y se probó:
 
```text
plot(x^2,-10,10)
plot(sin(x),-6.28,6.28)
```
 
### Evidencia
 
**Captura 13 - Gráfica de la parábola**
 
<img width="1416" height="593" alt="image" src="https://github.com/user-attachments/assets/64b2c4a9-af21-4127-b627-ee9dd8ec788e" />

 
 
 
**Captura 14 - Gráfica del seno**
 
<img width="1417" height="597" alt="image" src="https://github.com/user-attachments/assets/4243f222-0b5f-43ab-a626-222476e9b583" />

 
 
 
### Archivo de pruebas (`ejemplos.txt`)
 
```text
2+2
2+3*4
(2+3)*4
 
a = 10
b = 20
 
a+b
a*b
 
sqrt(25)
 
2^8
 
pi
2*pi
 
sin(pi/2)
cos(0)
 
log(100)
ln(e)
 
vars
 
plot(x^2,-10,10)
 
plot(sin(x),-6.28,6.28)
```
 
### Explorando el árbol sintáctico
 
Para `sin(x) + 2*x^2`, el árbol (simplificado) es:
 
```text
                addSub (+)
               /          \
     functionCall          mulDiv (*)
      /      \              /      \
   "sin"      id(x)        2      power (^)
                                   /      \
                                 id(x)    2
```
 
| Parte de la expresión | Etiqueta / nodo en el árbol |
|---|---|
| suma | `# addSub` (raíz) |
| función seno | `# functionCall` con `function = sin` |
| multiplicación | `# mulDiv` |
| potencia | `# power` |
| identificador `x` | `# id` (dos apariciones, una dentro de `sin`, otra en `x^2`) |
| número `2` | `# number` (dos apariciones: el factor y el exponente) |
 
Cuando se ejecuta `visit(ctx.expr())`, **no se evalúa una cadena de texto**: se recorre (visita) la estructura de árbol ya construida por el Parser. El texto original solo existe dentro de los tokens hoja (`NUMBER`, `ID`, palabras reservadas); todo lo demás es navegación de nodos padre-hijo mediante llamadas recursivas a `visit(...)`.
 
### Prueba de todo el lenguaje
 
Se ejecutó la secuencia completa propuesta en el tutorial:
 
```text
radio = 10
area = pi * radio^2
area
angulo = pi/4
sin(angulo)
cos(angulo)
vars
plot(sin(x), -6.28, 6.28)
plot(x^2, -10, 10)
```
 
Resultados obtenidos (parte no gráfica):
 
| Expresión | Resultado obtenido |
|---|---|
| `area` | `314.1592653589793` |
| `sin(angulo)` | `0.7071067811865475` |
| `cos(angulo)` | `0.7071067811865476` |
| `vars` | `area = 314.1592653589793`, `angulo = 0.7853981633974483`, `radio = 10.0` |
 
Ambos comandos `plot` completan las 800 muestras sin excepciones y abren la ventana gráfica correspondiente.
 
### Evidencia
 
**Captura 15 - Prueba completa del lenguaje**
 
<img width="1130" height="882" alt="image" src="https://github.com/user-attachments/assets/a35984ff-acb5-452f-99ee-3720b5a5a71a" />

 
 
### Preguntas finales
 
1. **¿Cuál es la responsabilidad del Lexer?** Convertir el flujo de caracteres de entrada en una secuencia de tokens (NUMBER, ID, MUL, ADD, palabras reservadas como `sin` o `plot`, etc.), descartando espacios en blanco.
2. **¿Cuál es la responsabilidad del Parser?** Tomar los tokens producidos por el Lexer y comprobar que cumplen las reglas gramaticales, construyendo un árbol sintáctico que representa la estructura de la entrada.
3. **¿Qué función cumplen las etiquetas como `#addSub` o `#functionCall`?** Le indican a ANTLR que genere un tipo de nodo de árbol distinto para cada alternativa de una regla, y por tanto un método de Visitor propio (`visitAddSub`, `visitFunctionCall`), en lugar de un único método genérico para toda la regla `expr`.
4. **¿Qué ventaja ofrece el patrón Visitor?** Separa la sintaxis (definida en la gramática) de la semántica (definida en la clase que extiende `ScientificCalcBaseVisitor`). Permite recorrer el mismo árbol con distintas lógicas (evaluar, graficar, imprimir, optimizar) sin modificar la gramática ni las clases generadas.
5. **¿Qué representa la tabla de símbolos?** El mapa `memory` que asocia cada identificador (`String`) con su valor numérico (`Double`) actual; es la memoria de variables del intérprete.
6. **¿Por qué la variable `x` cambia continuamente durante una gráfica?** Porque `visitPlotExpr` actualiza `memory.put("x", x)` en cada una de las 800 iteraciones antes de volver a visitar `ctx.expr(0)`, simulando la evaluación de la función en muchos puntos distintos.
7. **¿Por qué podemos evaluar el mismo árbol sintáctico varias veces?** Porque el árbol es una estructura de datos inmutable construida una sola vez por el Parser; visitarlo no lo modifica, solo lee sus nodos. Cambiar el estado externo (la tabla de símbolos) entre visitas basta para obtener resultados distintos.
8. **¿Qué sucede cuando se intenta graficar una función con una discontinuidad?** La evaluación en el punto de discontinuidad produce `Infinity`, `-Infinity` o `NaN`. Si no se filtran, estos valores generan líneas o transformaciones de coordenadas inválidas en el dibujo. Por eso `visitPlotExpr` descarta esas muestras con `Double.isFinite(y)`.
9. **¿Qué modificaciones serían necesarias para implementar funciones con dos argumentos?** Habría que agregar una nueva alternativa a `expr`, por ejemplo `function2 '(' expr ',' expr ')' # functionCall2`, con una regla `function2` para nombres como `pow`, `max`, `min`, y un método `visitFunctionCall2` que visite `ctx.expr(0)` y `ctx.expr(1)` por separado antes de aplicar la operación.
10. **¿Por qué la calculadora desarrollada puede considerarse un lenguaje de dominio específico?** Porque no es un lenguaje de propósito general: su gramática, vocabulario (funciones, constantes, comandos) y semántica están diseñados exclusivamente para resolver un dominio acotado — evaluación y graficación de expresiones matemáticas — igual que ocurre con lenguajes de consulta o de configuración.
### Retos
 
**Reto 1 — Nuevas funciones (implementado):** se agregaron `asin`, `acos`, `atan`, `floor` y `ceil` tanto a la regla `function` de la gramática como al `switch` de `visitFunctionCall`, reutilizando exactamente el mismo mecanismo que las funciones existentes. Se verificaron sin error de compilación ni de ejecución.
 
Para los retos 2 a 5, siguiendo la indicación del tutorial de diseñar primero la gramática sin tocar el código Java, se proponen las siguientes reglas (diseño únicamente, sin implementar el Visitor):
 
**Reto 2 — Funciones con dos argumentos** (`pow(2,8)`, `max(10,25)`, `min(10,25)`):
 
```antlr
expr
    : ...
    | function2 '(' expr ',' expr ')'   # functionCall2
    | ...
    ;
 
function2
    : 'pow'
    | 'max'
    | 'min'
    ;
```
 
**Reto 3 — Rango vertical explícito** (`plot(expr,xmin,xmax,ymin,ymax)`):
 
```antlr
stat
    : ...
    | 'plot' '(' expr ',' expr ',' expr ',' expr ',' expr ')' NEWLINE  # plotExprRange
    | ...
    ;
```
 
(alternativa que convive con la regla `plotExpr` de tres argumentos, para no romper la sintaxis ya implementada.)
 
**Reto 4 — Varias funciones en una misma gráfica** (`plot(sin(x),cos(x),-6.28,6.28)`):
 
```antlr
stat
    : ...
    | 'plot' '(' exprList ',' expr ',' expr ')' NEWLINE  # plotMultiExpr
    | ...
    ;
 
exprList
    : expr (',' expr)*
    ;
```
 
**Reto 5 — Definición de funciones propias** (`f(x) = x^2 + 2*x + 1`, luego `f(5)` y `plot(f(x),-10,10)`):
 
```antlr
stat
    : ...
    | ID '(' ID ')' '=' expr NEWLINE   # defineFunction
    | ...
    ;
 
expr
    : ...
    | ID '(' expr ')'   # userFunctionCall
    | ...
    ;
```
 
Esto exigiría una tabla de símbolos adicional para funciones definidas por el usuario (nombre → parámetro formal + árbol de la expresión), y que `userFunctionCall` sustituya temporalmente el parámetro formal en la tabla `memory` antes de visitar el cuerpo de la función — la misma técnica ya usada para la variable `x` al graficar.
 
### Evidencia
 
**Captura 16 - Reto 1 implementado**
 
<img width="176" height="178" alt="image" src="https://github.com/user-attachments/assets/bbb788fd-2054-421b-8f00-ebede8afaa25" />
 
 
 
### Lista de comprobación
 
- números reales
- suma
- resta
- multiplicación
- división
- paréntesis
- variables
- potencia
- operadores unarios
- constantes `pi` y `e`
- funciones científicas (incluye el Reto 1: `asin`, `acos`, `atan`, `floor`, `ceil`)
- comando `clear`
- comando `vars`
- comando `plot`
- visualización gráfica
### Reflexión final
 
El laboratorio partió de una gramática mínima para expresiones aritméticas y, paso a paso, evolucionó hasta un pequeño lenguaje de dominio específico capaz de evaluar expresiones, mantener variables, invocar funciones matemáticas y representar funciones gráficamente. La arquitectura se mantuvo constante durante todo el proceso:
 
```text
Gramática → Lexer → Parser → Árbol → Visitor
```
 
La lección central es la separación de responsabilidades: **la gramática define qué es válido decir** (sintaxis) y **el Visitor define qué significa cada cosa** (semántica). Gracias a esa separación fue posible agregar potencias, funciones, constantes, comandos de memoria y finalmente graficación sin reescribir el intérprete completo cada vez — solo se amplió la gramática, se regeneraron los archivos de ANTLR y se agregó el método `visit...` correspondiente a la nueva etiqueta. Esa misma estrategia es la base de intérpretes, compiladores, traductores, analizadores de código y lenguajes de consulta mucho más complejos que el desarrollado aquí.



