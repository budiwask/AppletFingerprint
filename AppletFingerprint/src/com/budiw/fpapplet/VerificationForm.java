package com.budiw.fpapplet;

import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.digitalpersona.onetouch.*;

public class VerificationForm extends CaptureForm
{
	private static final long serialVersionUID = -9160876912046100450L;
	private static final String DEFAULT_FEATURE_NAME = "inputfeature";
	public static final String DEFAULT_FEATURE_PATH = System.getProperty("user.home") + "\\"  + DEFAULT_FEATURE_NAME + ".fpp";
	private static final String VERIFICATION_KEYWORD = "VERIFIED";
	private String templateName = "";
	
	
	public VerificationForm(String templateName) {
		super();
		if(templateName == "" || templateName == null)
			this.templateName = EnrollmentForm.DEFAULT_TEMPLATE_NAME;
		else
			this.templateName = templateName;
		
//		featurePath = System.getProperty("user.home") + "\\"  + filename + ".fpp";
	}
	
	public VerificationForm() {
		super();
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
			writeFile(DEFAULT_FEATURE_PATH, features.serialize());
			String response = uploadFeature();
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
	
	@SuppressWarnings("unused")
	protected String uploadFeature() {
		HttpClient httpclient = new DefaultHttpClient();
		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost("https://localhost/upload.php");
//		BasicHttpParams fileParam = new BasicHttpParams();
//		fileParam.setParameter("templateName", filename);
//		httppost.setParams(fileParam);
		File file = new File(DEFAULT_FEATURE_PATH);

		//This will appear on $_FILES, uploading the challenger's fingerprint feature
		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file, "image/jpeg");
		mpEntity.addPart("userfile", cbFile);
		
		//This will appear on $_POST, uploading templateName to be matched against the input feature
		ContentBody tName = null;
		try {
			tName = new StringBody(templateName);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		mpEntity.addPart("templateName", tName);


		httppost.setEntity(mpEntity);
		//		System.out.println("executing request " + httppost.getRequestLine());

		try {
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			
//			System.out.println(response.getStatusLine());
			
			//Print content of response, check for verification message
			if (resEntity != null) {
				String responsePayload = EntityUtils.toString(resEntity);
				httpclient.getConnectionManager().shutdown();
				System.out.println(responsePayload);
				return responsePayload;
			} 
			if (resEntity != null) {
				EntityUtils.consume(resEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		//When response is null, return empty String
		return "";
		
	}

}
