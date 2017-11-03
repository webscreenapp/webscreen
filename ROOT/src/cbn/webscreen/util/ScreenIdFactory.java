package cbn.webscreen.util;

import java.util.Random;

import org.hashids.Hashids;

public class ScreenIdFactory {

	private static String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	private static Hashids hashids = null;
	
	static {
		Random random = new Random(); 
		String salt = "";
		for (int i = 0; i < 16; i++) {
			salt = salt + alphabet.charAt(random.nextInt(alphabet.length()));
		}
		
		hashids = new Hashids(salt, 6);
	}
	
	private static int increment = 0;
	
	public static synchronized String generate() {

		increment++;
		 
		return hashids.encode(increment); 
			 
	}
	
}
