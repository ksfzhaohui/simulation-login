package org.simulationlogin.util;

import java.security.MessageDigest;

/**
 * 对密码进行加密
 * 
 * @author zhaohui
 * 
 */
public class PwEncryption {

	public static String encryption(String type, String originalText) throws Exception {
		byte buf[] = originalText.getBytes("ISO-8859-1");
		StringBuffer hexString = new StringBuffer();
		String result = "";
		String digit = "";
		try {
			MessageDigest algorithm = MessageDigest.getInstance(type);
			algorithm.reset();
			algorithm.update(buf);
			byte[] digest = algorithm.digest();
			for (int i = 0; i < digest.length; i++) {
				digit = Integer.toHexString(0xFF & digest[i]);

				if (digit.length() == 1) {
					digit = "0" + digit;
				}

				hexString.append(digit);
			}

			result = hexString.toString();
		} catch (Exception ex) {
			result = "";
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(encryption("MD5", "111111"));
		System.out.println(encryption("sha1", "111111"));
	}
}
