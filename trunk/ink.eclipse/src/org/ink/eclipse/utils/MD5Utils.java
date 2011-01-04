package org.ink.eclipse.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	private static MessageDigest m;

	static{
		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}


	public static String digest(String s){
		byte[] result = m.digest(s.getBytes());
		return new BigInteger(1,result).toString(16);
	}


	public static void main(String args[]) throws Exception{
		System.out.println("MD5: "+digest("asfafdsaf"));
	}


}
