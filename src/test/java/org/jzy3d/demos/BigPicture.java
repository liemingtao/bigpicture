package org.jzy3d.demos;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLProfile;

import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.axes.layout.IAxeLayout;
import org.jzy3d.plot3d.rendering.canvas.Quality;
import org.jzy3d.plot3d.rendering.view.AWTRenderer3d;
import org.jzy3d.plot3d.rendering.view.Renderer3d;
import org.jzy3d.plot3d.rendering.view.View;
import org.jzy3d.plot3d.rendering.view.ViewportMode;
import org.jzy3d.plot3d.rendering.view.modes.ViewPositionMode;

import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;

public class BigPicture {
    public static String TITLE = "BigPicture";

    public enum Type {
        /** 2D */
        dd, /** 3D */
        ddd
    }

    public static Chart chart(AbstractDrawable drawable, Type type) {
        return chart(drawable, type, "awt", false, new Rectangle(1000, 1000));
    }

    public static Chart chartBlack(AbstractDrawable drawable, Type type) {
        return chart(drawable, type, "awt", true, new Rectangle(1000, 1000));
    }

    public static Chart offscreen(AbstractDrawable drawable, Type type) {
        return chart(drawable, type, "offscreen,"+1000+"," + 1000, false, null);
    }

    
    public static Chart offscreen(AbstractDrawable drawable, Type type, int width, int height) {
        return chart(drawable, type, "offscreen,"+width+"," + height, false, null);
    }

    
    public static Chart screenshot(AbstractDrawable drawable, Type type, int width, int height, File output) throws IOException {
        Chart chart = chart(drawable, type, "offscreen,"+width+"," + height, false, null);
        screenshot(chart, output);
        return chart;
    }

    protected static void screenshot(Chart chart, File output) throws IOException {
        if (output.exists())
            output.delete();
        if (!output.getParentFile().exists())
            output.mkdirs();
        chart.getCanvas().screenshot(output);
        System.out.println("Dumped screenshot in: " + output);
    }


    @SuppressWarnings("static-access")
    public static Chart chart(AbstractDrawable drawable, Type type, String wt, boolean black, Rectangle rect) {
        Chart chart = null;//AWTChartComponentFactory.chart(Quality.Intermediate, wt);
        chart = new AWTChartComponentFactory(){
            @Override
            public Renderer3d newRenderer(View view, boolean traceGL, boolean debugGL) {
                return new AWTRenderer3d(view, traceGL, debugGL){
                    @Override
                    public void display(GLAutoDrawable canvas) {
                        GL gl = canvas.getGL();

                        if (view != null) {
                            view.clear(gl);
                            view.render(gl, glu);

                            if (doScreenshotAtNextDisplay) {
                                AWTGLReadBufferUtil screenshot = new AWTGLReadBufferUtil(GLProfile.getGL2GL3(), true);
                                screenshot.readPixels(gl, false);
                                image = screenshot.getTextureData();
                                bufferedImage = screenshot.readPixelsToBufferedImage(gl, false);
                                
                                doScreenshotAtNextDisplay = false;
                            }
                        }
                    }

                };
            }
        }.chart(Quality.Advanced, wt);
        
        chart.getScene().getGraph().add(drawable);
        if (black)
            chart.black();
        chart.getView().getCamera().setViewportMode(ViewportMode.STRETCH_TO_FILL);
        chart.addMouseController();
        if (rect != null)
            chart.open(TITLE, rect.width, rect.height);
        else
            chart.open(TITLE, rect);
        if (type.equals(Type.dd))
            layout2d(chart);
        return chart;
    }

    public static void layout2d(Chart chart) {
        View view = chart.getView();
        view.setViewPositionMode(ViewPositionMode.TOP);
        view.getCamera().setViewportMode(ViewportMode.STRETCH_TO_FILL);
        IAxeLayout axe = chart.getAxeLayout();
        axe.setZAxeLabelDisplayed(false);
        axe.setTickLineDisplayed(false);
    }

}
