package com.budiw.fpapplet;

import javax.swing.JOptionPane;

import com.digitalpersona.onetouch.*;

public class VerificationForm extends CaptureForm
{
	private static final long serialVersionUID = -9160876912046100450L;
	private static final String FEATURE_PATH = System.getProperty("user.home") + "\\inputfeature.fpp";
	private static final String VERIFICATION_KEYWORD = "VERIFIED";
	
	public VerificationForm() {
	}
	
	protected void init()
	{
		super.init();
		this.setTitle("Fingerprint Verification");
		updateStatus(0);
	}

	protected void process(DPFPSample sample) {
		super.process(sample);

		// Process the sample and create a feature set for the enrollment purpose.
		DPFPFeatureSet features = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

		// Check quality of the sample and start verification if it's good
		if (features != null)
		{
			//Write to file and upload to server for comparison. Verification format must be *.fpp
			writeFile(FEATURE_PATH, features.serialize());
			String response = uploadFingerprint(FEATURE_PATH, true);
			if(response.indexOf(VERIFICATION_KEYWORD) != -1) {
				JOptionPane.showMessageDialog(this, "Verified");
				setVisible(false);
			} else 
				JOptionPane.showMessageDialog(this, "DENIED", "FAILED VERIFICATION", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void updateStatus(int FAR)
	{
		// Show "False accept rate" value
		setStatus(String.format("False Accept Rate (FAR) = %1$s", FAR));
	}

}
