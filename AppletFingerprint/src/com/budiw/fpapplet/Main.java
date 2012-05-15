package com.budiw.fpapplet;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

import netscape.javascript.JSObject;

public class Main extends JApplet {

	private static final long serialVersionUID = 4472512675731104474L;
	private String appletMode = "";
	private JSObject jso = null;
	
	//Applet params: templateName:<name>, fpmode:<enroll/verify>

	//Called when this applet is loaded into the browser.
    public void init() {
    	appletMode = this.getParameter("fpmode");
    	//###### If launching from Eclipse, comment the line below (Eclipse doesn't launch from JSO, unlike browsers)
    	//How about OpenMRS compatibility?
//    	jso = JSObject.getWindow(this);
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
    	if(appletMode.equalsIgnoreCase("enroll")) {
    		new EnrollmentForm(this.getParameter("templateName"));
    	}
    	else
    		new VerificationForm(this.getParameter("templateName"), jso);
	}
}