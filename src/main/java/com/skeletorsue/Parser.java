package com.skeletorsue;

import java.util.ArrayList;

public class Parser {

	public static Integer get(String Input, Integer Default) {
		try {
			return Integer.parseInt(Input);
		} catch (NumberFormatException nfe) {
			return Default;
		}
	}

	public static String get(String Input, String Default) {

		if (Input != null) {
			return Input;
		}
		return Default;
	}

	public static Double round(Double Number, Integer Precision) {
		String multiplier = "1";

		while (multiplier.length() < (Precision + 1)) {
			multiplier += "0";
		}

		return (double) Math.round(Number * Parser.get(multiplier, 100)) / Parser.get(multiplier, 100);
	}

	public static Double average(ArrayList<Double> data) {
		Double sum = 0.00;

		for (Double d : data) sum += d;

		return sum / data.size();
	}

	public static Double max(ArrayList<Double> data) {
		Double result = 0.00;

		for (Double d : data) {
			if (d > result)
				result = d;
		}

		return result;
	}

	public static Double min(ArrayList<Double> data) {
		Double result = (double) Integer.MAX_VALUE;

		for (Double d : data) {
			if (d < result)
				result = d;
		}

		return result;
	}

}
