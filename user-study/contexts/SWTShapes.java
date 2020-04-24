package f2BasicShapes;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * ZetCode Java SWT tutorial
 *
 * In this program, we draw some
 * basic shapes of the Java SWT library
 *
 * @author jan bodnar
 * website zetcode.com
 * last modified June 2009
 */

public class SWTApp {

    private Shell shell;

    public SWTApp(Display display) {

        shell = new Shell(display);

        shell.addPaintListener(new ArcExamplePaintListener());

        shell.setText("Basic shapes");
        shell.setSize(430, 300);
        shell.setLocation(300, 300);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private class ArcExamplePaintListener implements PaintListener {

        public void paintControl(PaintEvent e) {

            drawShapes(e);
            e.gc.dispose();
        }
    }

    private void drawShapes(PaintEvent e) {

        e.gc.setAntialias(SWT.ON);

        e.gc.setBackground(new Color(e.display, 150, 150, 150));

        e.gc.fillRectangle(20, 20, 120, 80);
        e.gc.fillRectangle(180, 20, 80, 80);
        e.gc.fillOval(290, 20, 120, 70);


}
}
