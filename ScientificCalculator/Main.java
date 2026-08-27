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