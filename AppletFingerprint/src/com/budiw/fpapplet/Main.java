package com.budiw.fpapplet;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class Main extends JApplet {

	private static final long serialVersionUID = 4472512675731104474L;
	private String appletMode = "";

	//Called when this applet is loaded into the browser.
    public void init() {
    	appletMode = this.getParameter("fpmode");
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
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
    
    //Let's do this: LEEEERRRROOOOOYYYYYY..............
    //Retrieve the param from webpage by calling this.getParameter(<paramname>)
    private void createGUI() {
    	if(appletMode.equalsIgnoreCase("enroll"))
    		new EnrollmentForm(this.getParameter("templateName"));
    	else
    		new VerificationForm(this.getParameter("templateName"));
    		
	}
}