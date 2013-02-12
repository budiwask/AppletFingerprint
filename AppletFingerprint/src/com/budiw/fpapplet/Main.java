package com.budiw.fpapplet;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import netscape.javascript.JSObject;

public class Main extends JApplet {

	private static final long serialVersionUID = 4472512675731104474L;
	private JSObject jso = null;
	
	//Applet params: templateName:<name>, fpmode:<enroll/verify>

	//Called when this applet is loaded into the browser.
    public void init() {
    	//###### If launching from Eclipse, comment the line below (Eclipse doesn't launch from JSO, unlike browsers)
    	jso = JSObject.getWindow(this);
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
    
    public void stop() {
    	System.exit(0);
    }
    
    //Let's do this: LEEEERRRROOOOOYYYYYY..............
    //Retrieve the param from webpage by calling this.getParameter(<paramname>)
    private void createGUI() {
    		new EnrollmentForm(this.getParameter("templateName"), jso);
    }
}