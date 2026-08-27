import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlotWindow extends JPanel {

    private List<Double> xs;
    private List<Double> ys;

    public PlotWindow(
    List<Double> xs,
    List<Double> ys) {

        this.xs = xs;
        this.ys = ys;

        JFrame frame =
        new JFrame(
        "Scientific Calculator"
        );

        frame.setDefaultCloseOperation(
        JFrame.DISPOSE_ON_CLOSE
        );

        frame.setSize(800, 600);

        frame.add(this);

        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 =
        (Graphics2D) g;

        if (xs.size() < 2) {
            return;
        }

        double xmin =
        xs.stream()
        .mapToDouble(Double::doubleValue)
        .min()
        .orElse(-1);

        double xmax =
        xs.stream()
        .mapToDouble(Double::doubleValue)
        .max()
        .orElse(1);

        double ymin =
        ys.stream()
        .mapToDouble(Double::doubleValue)
        .min()
        .orElse(-1);

        double ymax =
        ys.stream()
        .mapToDouble(Double::doubleValue)
        .max()
        .orElse(1);

        for (int i = 1;
        i < xs.size();
        i++) {

            double x1 = xs.get(i - 1);
            double y1 = ys.get(i - 1);

            double x2 = xs.get(i);
            double y2 = ys.get(i);

            int px1 =
            (int)(
            (x1 - xmin)
            / (xmax - xmin)
            * getWidth()
            );

            int py1 =
            getHeight()
            - (int)(
            (y1 - ymin)
            / (ymax - ymin)
            * getHeight()
            );

            int px2 =
            (int)(
            (x2 - xmin)
            / (xmax - xmin)
            * getWidth()
            );

            int py2 =
            getHeight()
            - (int)(
            (y2 - ymin)
            / (ymax - ymin)
            * getHeight()
            );

            g2.drawLine(
            px1, py1,
            px2, py2
            );
        }
    }
}
