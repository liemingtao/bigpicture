package org.jzy3d.demos.io.hbase;

import java.util.List;

import org.jzy3d.demos.BigPicture;
import org.jzy3d.demos.vbo.barmodel.builder.VBOBuilderColumnDatabase;
import org.jzy3d.demos.vbo.barmodel.generators.GeneratorKeyValue;
import org.jzy3d.demos.vbo.barmodel.model.KeyVal;
import org.jzy3d.io.hbase.HBaseIO;
import org.jzy3d.io.hbase.Progress;
import org.jzy3d.plot3d.primitives.vbo.drawable.DrawableVBO;
import org.jzy3d.utils.LoggerUtils;

/**
 * Draws key-values of each row, where each column name is mapped to Y and a
 * color, and each column value is mapped to Z.
 * 
 * @author martin
 *
 */
public class DemoHBaseColumnPlotAWT {
    public static int MILION = 1000000;

    static String TABLE = DemoHBaseColumnPlotAWT.class.getSimpleName();
    static String FAMILY = "demo";
    static {
        LoggerUtils.minimal();
    }

    public static void main(String[] args) throws Exception {

        int nRaws = 100; //MILION / 1000;
        int nPivotTheme = 8;
        int nPivotCol = 35 * nPivotTheme;
        int nCpCcCat = 12;
        int nCpCcCol = 10;

        GeneratorKeyValue generator = new GeneratorKeyValue();
        final List<List<KeyVal<String, Float>>> rows = generator.vip(nRaws, nPivotCol, nCpCcCat, nCpCcCol);

        DrawableVBO drawable = new DrawableVBO(new VBOBuilderColumnDatabase(rows));
        BigPicture.chartBlack(drawable, BigPicture.Type.ddd);

        
        String[] families = { FAMILY };

        HBaseIO hbase = new HBaseIO();
        hbase.tableDelete(TABLE);
        hbase.tableCreate(TABLE, families);
        
        Progress progress = new Progress(){
            public void progress(int value) {
                if(value%1000==0)
                    System.out.println(value + " inserted");
            }
        };
        hbase.putAll(rows, TABLE, FAMILY, progress);

        hbase.scanPrint(TABLE);
        
        
        rows.clear();
        hbase.scanRows(TABLE, rows);
        
        
        
        
        DrawableVBO drawable2 = new DrawableVBO(new VBOBuilderColumnDatabase(rows));
        BigPicture.chartBlack(drawable2, BigPicture.Type.ddd);
    }
}