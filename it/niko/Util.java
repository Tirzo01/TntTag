package it.niko;

import java.util.Random;

public class Util {
	
	public static int randomNumber(int low, int high) {
		Random r = new Random();
		return r.nextInt((high+1)-low) + low;
	}
}
