package bomber.UI;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class UserInterface extends JFrame{

	public UserInterface(){
		
		initiateUI();
	}
	
	private void initiateUI(){
		
		setTitle("Bomb Blitz");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        //Create Prototype Button
        JButton startButton = new JButton("Start");

        startButton.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });
        
        //Create Exit Button
        JButton exitButton = new JButton("Exit");

        exitButton.addActionListener((ActionEvent event) -> {
            System.exit(0);
        });

        createLayout(startButton, exitButton);
	}

	private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        ParallelGroup hgroup = gl.createParallelGroup();
        for(JComponent comp : arg){
        	hgroup.addComponent(comp);
        }
        gl.setHorizontalGroup(hgroup);
        
        SequentialGroup vgroup = gl.createSequentialGroup();
        for(JComponent comp : arg){
        	vgroup.addComponent(comp);
        }
        gl.setVerticalGroup(vgroup);
    }
}
