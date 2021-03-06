package org.jzy3d.demos.io.hbase.column;

import java.util.List;

import org.jzy3d.chart.BigPicture;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.Type;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.demos.drawing.vbo.barmodel.builder.VBOBuilderLineStrip;
import org.jzy3d.io.hbase.HBaseIO;
import org.jzy3d.maths.Histogram;
import org.jzy3d.maths.Statistics;
import org.jzy3d.plot2d.primitives.Histogram2d;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;

/**
 * Draws key-values of each row, where each column name is mapped to Y and a color, and each column value is mapped to Z.
 * @author martin
 *
 */
public class DemoHBaseColumnScanAndPlot{
    public static void main(String[] args) {
        String columnName = "4pivot.col4";

        HBaseIO hbase = new HBaseIO();
        List<Float> rows = hbase.scanColumn(DemoHBaseColumnGenerate.TABLE, DemoHBaseColumnGenerate.FAMILY, columnName);
        line(rows);
        histogram(rows).open("", 800, 800);
    }

    private static Chart histogram(List<Float> values) {
        // Density 
        float min = Statistics.min(values);
        float max = Statistics.max(values);

        Histogram model = new Histogram(min,max,10);
        
        for(Float value: values){
            model.add(value);
        }
        Histogram2d histogram = new Histogram2d(model);
        
        Chart chart = AWTChartComponentFactory.chart().black().view2d();
        histogram.addTo(chart);
        return chart;
    }

    private static void line(List<Float> rows) {
        DrawableVBO drawable = new DrawableVBO(new VBOBuilderLineStrip(rows));
        BigPicture.chart(drawable, Type.dd).black();
    }
    
    public static Histogram testHistAdd(){
        Histogram h = new Histogram(0, 1, 10);
        for (int i = 0; i < 1000; i++) {
            h.add((float)Math.random());
        }
        return h;
    }

}
