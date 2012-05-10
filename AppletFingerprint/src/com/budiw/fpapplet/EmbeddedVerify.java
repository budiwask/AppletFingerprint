package com.budiw.fpapplet;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;

public class EmbeddedVerify extends JApplet {
	private static final long serialVersionUID = 3688292316784751274L;
	private VerificationForm vf = null;
	public void init() {
		System.out.println(System.getProperties().toString());
        
		setSize(0,0);
		//Check if the applet is not open already
		if(vf == null) {
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
		} else {
			vf.setVisible(true);
		}
    }
	
	private void createGUI() {
		vf = new VerificationForm(this.getParameter("templateName"));		
	}
	
	
}