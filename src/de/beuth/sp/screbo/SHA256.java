package de.beuth.sp.screbo;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
	// utility function Source: bytesToHexString function is from the IOSched project.
	// http://stackoverflow.com/questions/332079
	private static String bytesToHexString(byte[] bytes) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]).toUpperCase();
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	public static String getSHA256(String input) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(input.getBytes(StandardCharsets.UTF_8));
		return bytesToHexString(digest.digest());

	}
}
