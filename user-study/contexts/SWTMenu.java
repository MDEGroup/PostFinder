

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * ZetCode Java SWT tutorial
 *
 * This program creates a check menu item.
 * It will show or hide a statusbar.
 *
 * @author jan bodnar
 * website zetcode.com
 * last modified June 2009
 */

public class SWTMenu {

    private Shell shell;
    private Label status;
    private MenuItem statItem;

    public SWTMenu(Display display) {

        shell = new Shell(display);

        shell.setText("Check menu item");

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
    Listener statListener = new Listener() {

        public void handleEvent(Event event) {
            if (statItem.getSelection()) {
                status.setVisible(true);
            } else {
                status.setVisible(false);
            }
        }
    };

    public void initUI() {

        Menu menuBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menuBar);

        MenuItem cascadeFileMenu = new MenuItem(menuBar, SWT.CASCADE);
        cascadeFileMenu.setText("&File");

        Menu fileMenu = new Menu(shell, SWT.DROP_DOWN);
}}