package com.budiw.fpapplet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.digitalpersona.onetouch.DPFPCaptureFeedback;
import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPImageQualityAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPImageQualityEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;

public class CaptureForm extends JFrame
{
	private static final long serialVersionUID = 3389476239431661943L;
	private DPFPCapture capturer = DPFPGlobal.getCaptureFactory().createCapture();
	private JLabel picture = new JLabel();
	private JTextField prompt = new JTextField();
//	private JTextArea log = new JTextArea();
	private JTextField status = new JTextField("");
	private JButton badFingerprint = new JButton("Skip Fingerprinting");
//	public static final String FINGERPRINT_SERVER = "https://localhost/upload.php";
	public static final String FINGERPRINT_SERVER = "http://192.168.132.15/Patient_Registration/upload.php";


	public CaptureForm() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);

		setLayout(new BorderLayout());
		rootPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		picture.setPreferredSize(new Dimension(240, 280));
		picture.setBorder(BorderFactory.createLoweredBevelBorder());
		prompt.setFont(UIManager.getFont("Panel.font"));
		prompt.setEditable(false);
		prompt.setColumns(30);
		prompt.setFont(new Font("Ariel",Font.BOLD, 20));
		prompt.setMaximumSize(prompt.getPreferredSize());
		prompt.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), ""),
						BorderFactory.createLoweredBevelBorder()));
//		log.setColumns(40);
//		log.setEditable(false);
//		log.setFont(UIManager.getFont("Panel.font"));
//		JScrollPane logpane = new JScrollPane(log);
//		logpane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0), "Status:"), BorderFactory.createLoweredBevelBorder()));

		status.setEditable(false);
		status.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		status.setFont(UIManager.getFont("Panel.font"));
		
		badFingerprint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { onSkipFingerprint(); }});
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(badFingerprint);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));

		JPanel right = new JPanel(new BorderLayout());
		right.setBackground(Color.getColor("control"));
		right.add(prompt, BorderLayout.PAGE_START);
//		right.add(logpane, BorderLayout.CENTER);
		
		
		JPanel center = new JPanel(new BorderLayout());
		center.setBackground(Color.getColor("control"));
		center.add(right, BorderLayout.CENTER);
		center.add(picture, BorderLayout.LINE_START);
		center.add(status, BorderLayout.PAGE_END);

		setLayout(new BorderLayout());
		add(center, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.PAGE_END);

		this.addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				init();
				start();
			}
			public void componentHidden(ComponentEvent e) {
				stop();
			}

		});
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	protected void init()
	{

		capturer.addDataListener(new DPFPDataAdapter() {
			public void dataAcquired(final DPFPDataEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					makeReport("The fingerprint sample was captured.");
					setPrompt("Scan the same finger again.");
					process(e.getSample());
				}});
			}
		});
		capturer.addReaderStatusListener(new DPFPReaderStatusAdapter() {
			public void readerConnected(final DPFPReaderStatusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					setPrompt("Scan the patient fingerprint.");
				}});
			}
			public void readerDisconnected(final DPFPReaderStatusEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					setPrompt("Connect Fingerprint Reader.");
				}});
			}
		});
		capturer.addSensorListener(new DPFPSensorAdapter() {
			public void fingerTouched(final DPFPSensorEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					makeReport("The fingerprint reader was touched.");
				}});
			}
			public void fingerGone(final DPFPSensorEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					makeReport("The finger was removed from the fingerprint reader.");
				}});
			}
		});
		capturer.addImageQualityListener(new DPFPImageQualityAdapter() {
			public void onImageQuality(final DPFPImageQualityEvent e) {
				SwingUtilities.invokeLater(new Runnable() {	public void run() {
					if (e.getFeedback().equals(DPFPCaptureFeedback.CAPTURE_FEEDBACK_GOOD))
						makeReport("The quality of the fingerprint sample is good.");
					else
						makeReport("The quality of the fingerprint sample is poor.");
				}});
			}
		});
	}

	protected void process(DPFPSample sample)
	{
		// Draw fingerprint sample image.
		drawPicture(convertSampleToBitmap(sample));
	}

	protected void start()
	{
		capturer.startCapture();
		setPrompt("Scan patient's fingerprint");
	}

	protected void stop()
	{
		capturer.stopCapture();
	}

	public void setStatus(String string) {
		status.setText(string);
	}
	public void setPrompt(String string) {
		prompt.setText(string);
	}
	public void makeReport(String string) {
//		log.append(string + "\n");
	}

	public void drawPicture(Image image) {
		picture.setIcon(new ImageIcon(
				image.getScaledInstance(picture.getWidth(), picture.getHeight(), Image.SCALE_DEFAULT)));
	}

	protected Image convertSampleToBitmap(DPFPSample sample) {
		return DPFPGlobal.getSampleConversionFactory().createImage(sample);
	}

	protected DPFPFeatureSet extractFeatures(DPFPSample sample, DPFPDataPurpose purpose)
	{
		DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
		try {
			return extractor.createFeatureSet(sample, purpose);
		} catch (DPFPImageQualityException e) {
			return null;
		}
	}

	//For enrollment template, use *.fpt for file format
	//For verification feature, use *.fpp
	protected void writeFile(String filepath, byte[] data) {
		try {
			FileOutputStream out = new FileOutputStream(new File(filepath));
			out.write(data);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* 
	 * For patient with bad/missing fingerprints, we ask the clerk to enter a validation number 
	 * to skip the procedure (to punish lazy clerks who don't want to enter fingerprints).
	 */
	protected void onSkipFingerprint() {
//		System.out.println("Clicked Bad Fingerprint");
		Random rand = new Random();
		int validationCode = 0;
		int input = -1;
		while (validationCode != input) {
			validationCode = rand.nextInt(899999)+100000;
			String inputString = JOptionPane.showInputDialog(this, "Enter Validation Code: " + validationCode);
			try {
				input = Integer.parseInt(inputString);
			} catch(NumberFormatException e) {
				input = -1;
			}
		}
		System.out.println(VerificationForm.VERIFICATION_RESULT_SKIPPED);
		VerificationForm.notifyHTML(VerificationForm.VERIFICATION_RESULT_SKIPPED);
		System.exit(0);
	}
}
