package io.yiyuzhou.trip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Borders {
	private HashMap<String, HashMap<String, Float>> countryBorders;

	public Borders() throws IOException, ParseException {
		String[] lines = getBorderFileLines();
		countryBorders = new HashMap<>();
		parseCountryBorders(lines, countryBorders);
	}

	public float getBorder(String country0, String country1) {
		float ret;

		HashMap<String, Float> borders = countryBorders.get(country0);
		if (borders != null && borders.containsKey(country1))
			ret = borders.get(country1);
		else
			ret = -1; /* entry not found */

		return ret;
	}

	private String[] getBorderFileLines() throws IOException {
		InputStream is = Borders.class.getClassLoader().getResourceAsStream("borders.txt");
		if (is == null)
			throw new IllegalArgumentException("File not found!");

		List<Object> linesList;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			linesList = reader.lines().collect(Collectors.toList());
		}

		String[] lines = new String[linesList.size()];
		for (int i = 0; i < lines.length; i++)
			lines[i] = linesList.get(i).toString();

		return lines;
	}

	private void parseCountryBorders(String[] lines, HashMap<String, HashMap<String, Float>> countryBorders) {
		for (String line : lines) {
			String[] parts = line.split(" = ");

			/*
			 * There might be more than one countries on the left of the "=", separated by
			 * the word "and".
			 */
			String[] countries = parts[0].split(" and ");

			/* TODO: fix country name clean up */
			/* remove parentheses and spaces around them */
			// for (String country : countries)
			// 	country.replaceAll("\\(.*?\\)", "");

			for (String country : countries)
				countryBorders.put(country, new HashMap<String, Float>());

			if (parts.length > 1) {
				String[] adjBorders = parts[1].split("; ");
				for (String adjBorder : adjBorders) {
					String[] words = adjBorder.split("\\s+");
					for (int i = 0; i < words.length; i++)
						words[i] = words[i].replaceAll(" ", "");

					float borderLen = Float.parseFloat(words[words.length - 2].replace(",", ""));
					for (String country : countries)
						countryBorders.get(country).put(words[0], borderLen);
				}
			} else {
				/* if the country doesn't have any adjacent countries */
				for (String country : countries) {
					countryBorders.get(country).put(null, null);
					// System.out.println(country);
				}
			}
		}
	}
}
