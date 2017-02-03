package bomber.UI.tests;

import java.awt.EventQueue;

import bomber.UI.UserInterface;

public class UITest {

	public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            UserInterface ui = new UserInterface();
            ui.setVisible(true);
        });
    }

}
