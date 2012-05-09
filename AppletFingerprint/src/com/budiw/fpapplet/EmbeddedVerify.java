package com.budiw.fpapplet;

public class EmbeddedVerify {
	private static String databaseTemplateName = "";
	public static void main(String[] args) {
		
		if(args.length < 1) {
			System.err.println("ARGS:\n [0]: featureName\n");
		} else
			databaseTemplateName = args[0];
		
		new VerificationForm(databaseTemplateName);
	}
}