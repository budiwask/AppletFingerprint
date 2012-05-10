package com.budiw.fpapplet;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class EmbeddedEnroll extends JApplet{

	private static final long serialVersionUID = 8148893385869648404L;
	public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
		setSize(0,0);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
            e.printStackTrace();
            
        }
    }
	
	private void createGUI() {
		new EnrollmentForm(this.getParameter("templateName"));		
	}
}
