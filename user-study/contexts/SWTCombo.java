package c5Combo;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * ZetCode Java SWT tutorial
 *
 * In this program, we use the Combo
 * widget to select an option. 
 * The selected option is shown in the
 * Label widget.
 *
 * @author jan bodnar
 * website zetcode.com
 * last modified June 2009
 */


public class SWTApp {

    Shell shell;

    public SWTApp(Display display) {

        shell = new Shell(display);

        shell.setText("Combo");

        initUI();

        shell.setSize(300, 250);
        shell.setLocation(300, 300);

        shell.open();

        while (!shell.isDisposed()) {
          if (!display.readAndDispatch()) {
            display.sleep();
          }
        }
    }


    public void initUI() {

        final Label label = new Label(shell, SWT.LEFT);
        label.setText("...");

        label.setLocation(50, 100);
        label.pack();

        final Combo combo = new Combo(shell, SWT.DROP_DOWN);
        combo.add("Ubuntu");
        combo.add("Fedora");
        combo.add("Mandriva");
        combo.add("Red Hat");
        combo.add("Mint");

}
}
