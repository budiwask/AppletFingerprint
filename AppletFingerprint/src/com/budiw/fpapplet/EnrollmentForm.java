package com.budiw.fpapplet;

import java.io.File;
import java.io.FileOutputStream;

import com.digitalpersona.onetouch.*;
import com.digitalpersona.onetouch.processing.*;
import javax.swing.JOptionPane;

public class EnrollmentForm extends CaptureForm
{
	private static final long serialVersionUID = 7675828998942686645L;
	private DPFPEnrollment enroller = DPFPGlobal.getEnrollmentFactory().createEnrollment();
	private static final String TEST_FILEPATH = System.getProperty("user.home") + "\\template2.fpt";
	
	public EnrollmentForm() {
	}
	
	protected void init()
	{
		super.init();
		this.setTitle("Fingerprint Enrollment");
		updateStatus();
	}

	protected void process(DPFPSample sample) {
		super.process(sample);
		// Process the sample and create a feature set for the enrollment purpose.
		DPFPFeatureSet features = extractFeatures(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

		// Check quality of the sample and add to enroller if it's good
		if (features != null) try
		{
			makeReport("The fingerprint feature set was created.");
			enroller.addFeatures(features);		// Add feature set to template.
		}
		catch (DPFPImageQualityException ex) { }
		finally {
			updateStatus();

			// Check if template has been created.
			switch(enroller.getTemplateStatus())
			{
				case TEMPLATE_STATUS_READY:	// report success and stop capturing
					stop();
					writeTemplateToFile();
					uploadFingerprint(TEST_FILEPATH, false);
					setPrompt("Click Close, and then click Fingerprint Verification.");
					break;

				case TEMPLATE_STATUS_FAILED:	// report failure and restart capturing
					enroller.clear();
					stop();
					updateStatus();
					JOptionPane.showMessageDialog(EnrollmentForm.this, "The fingerprint template is not valid. Repeat fingerprint enrollment.", "Fingerprint Enrollment", JOptionPane.ERROR_MESSAGE);
					start();
					break;
			}
		}
	}
	
	private void writeTemplateToFile() {
		try {
			FileOutputStream out = new FileOutputStream(new File(TEST_FILEPATH));
			out.write(enroller.getTemplate().serialize());
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateStatus()
	{
		// Show number of samples needed.
		setStatus(String.format("Fingerprint samples needed: %1$s", enroller.getFeaturesNeeded()));
	}
	
}
