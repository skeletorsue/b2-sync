package com.skeletorsue;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Hash {
	public static String Name(String Algorithm, String Name) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance(Algorithm);
		md.update(Name.getBytes());
		return String.format("%064x", new java.math.BigInteger(1, md.digest()));
	}

	public static String File(String Algorithm, File file) throws NoSuchAlgorithmException, IOException {
		final MessageDigest messageDigest = MessageDigest.getInstance(Algorithm);

		try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
			final byte[] buffer = new byte[1024];
			for (int read; (read = is.read(buffer)) != -1; ) {
				messageDigest.update(buffer, 0, read);
			}
		}

		try (Formatter formatter = new Formatter()) {
			for (final byte b : messageDigest.digest()) {
				formatter.format("%02x", b);
			}
			return formatter.toString();
		}
	}
}
