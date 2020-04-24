package c2CheckButton;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * ZetCode Java SWT tutorial
 *
 * This program uses a check button
 * widget to show/hide the title
 * of the window
 *
 * @author jan bodnar
 * website zetcode.com
 * last modified June 2009
 */


public class SWTApp {

    private Shell shell;
    private Button cb;

    public SWTApp(Display display) {

        shell = new Shell(display);

        shell.setText("Check button");

        initUI();

        shell.setSize(250, 200);
        shell.setLocation(300, 300);

        shell.open();
        
        while (!shell.isDisposed()) {
          if (!display.readAndDispatch()) {
            display.sleep();
          }
        }
    }


    public void initUI() {

        cb = new Button(shell, SWT.CHECK);
        cb.setText("Show title");
        cb.setSelection(true);
        cb.setLocation(50, 50);
        cb.pack();


}
}