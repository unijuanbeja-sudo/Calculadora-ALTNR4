import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Override
    public Double visitPrintExpr(
    ScientificCalcParser.PrintExprContext ctx) {

        double value = visit(ctx.expr());

        System.out.println(value);

        return value;
    }

    @Override
    public Double visitAssign(
    ScientificCalcParser.AssignContext ctx) {

        String id = ctx.ID().getText();

        double value = visit(ctx.expr());

        memory.put(id, value);

        return value;
    }

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

    // Nuevo: Fase 2 (secciones 17-26)

    @Override
    public Double visitPower(
    ScientificCalcParser.PowerContext ctx) {

        double base = visit(ctx.expr(0));
        double exponent = visit(ctx.expr(1));

        return Math.pow(base, exponent);
    }

    @Override
    public Double visitUnary(
    ScientificCalcParser.UnaryContext ctx) {

        double value = visit(ctx.expr());

        if (ctx.op.getText().equals("-")) {
            return -value;
        }

        return value;
    }

    @Override
    public Double visitFunctionCall(
    ScientificCalcParser.FunctionCallContext ctx) {

        String function =
        ctx.function().getText();

        double value =
        visit(ctx.expr());

        switch (function) {

            case "sin":
                return Math.sin(value);

            case "cos":
                return Math.cos(value);

            case "tan":
                return Math.tan(value);

            case "sqrt":
                return Math.sqrt(value);

            case "log":
                return Math.log10(value);

            case "ln":
                return Math.log(value);

            case "abs":
                return Math.abs(value);

            case "exp":
                return Math.exp(value);

            // Reto 1 (seccion 42): nuevas funciones cientificas
            case "asin":
                return Math.asin(value);

            case "acos":
                return Math.acos(value);

            case "atan":
                return Math.atan(value);

            case "floor":
                return Math.floor(value);

            case "ceil":
                return Math.ceil(value);

            default:
                throw new RuntimeException(
                "Funcion desconocida: " + function
                );
        }
    }

    @Override
    public Double visitConstantExpr(
    ScientificCalcParser.ConstantExprContext ctx) {

        String constant =
        ctx.constant().getText();

        if (constant.equals("pi")) {
            return Math.PI;
        }

        if (constant.equals("e")) {
            return Math.E;
        }

        return 0.0;
    }

    @Override
    public Double visitClear(
    ScientificCalcParser.ClearContext ctx) {

        memory.clear();

        System.out.println(
        "Memoria eliminada."
        );

        return 0.0;
    }

    @Override
    public Double visitShowVars(
    ScientificCalcParser.ShowVarsContext ctx) {

        if (memory.isEmpty()) {
            System.out.println(
            "No hay variables definidas."
            );
            return 0.0;
        }

        for (Map.Entry<String, Double> entry :
        memory.entrySet()) {

            System.out.println(
            entry.getKey()
            + " = "
            + entry.getValue()
            );
        }

        return 0.0;
    }

    // Fase 3 (secciones 27-38, graficacion)

    @Override
    public Double visitPlotExpr(
    ScientificCalcParser.PlotExprContext ctx) {

        double xmin = visit(ctx.expr(1));
        double xmax = visit(ctx.expr(2));

        int samples = 800;

        List<Double> xs =
        new ArrayList<>();

        List<Double> ys =
        new ArrayList<>();

        for (int i = 0;
        i < samples;
        i++) {

            double x =
            xmin
            + i * (xmax - xmin)
            / (samples - 1);

            memory.put("x", x);

            double y =
            visit(ctx.expr(0));

            // Reto seccion 32: se descartan valores no finitos
            // (Infinity, -Infinity, NaN) para evitar que una
            // discontinuidad (p. ej. 1/x en x=0) dañe la grafica.
            if (Double.isFinite(y)) {
                xs.add(x);
                ys.add(y);
            }
        }

        new PlotWindow(xs, ys);

        return 0.0;
    }
}
