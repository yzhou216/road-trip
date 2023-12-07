package io.yiyuzhou.trip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Borders {
	private HashMap<String, HashMap<String, Integer>> graph = new HashMap<>();

	public HashMap<String, HashMap<String, Integer>> getGraph() {
		return graph;
	}

	public Borders() throws IOException, ParseException {
		String[] lines = getBorderFileLines();
		parseCountryBorders(lines);
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

	private List<String> extractCountries(String input) {
		List<String> ret;
		if (input == null) {
			ret = null;
		} else {
			List<String> countries = new ArrayList<>();
			ret = countries;
			Pattern pattern = Pattern.compile("[A-Za-z\\s\\(\\)]+(?=\\s\\d+[,.]?\\d*\\s*km)");
			Matcher matcher = pattern.matcher(input);

			while (matcher.find()) {
				String country = matcher.group().trim(); /* trim to remove leading/trailing spaces */
				countries.add(country);
			}
		}

		return ret;
	}

	private void parseCountryBorders(String[] lines) throws IOException {
		Capdists capdists = new Capdists();
		StateName stateName = new StateName();

		for (String line : lines) {
			String[] parts = line.split(" = ");
			String country = parts[0];
			String adjacencies;
			if (parts.length > 1)
				adjacencies = parts[1];
			else
				adjacencies = null;

			/* TODO: fix country name clean up */
			/* remove parentheses and spaces around them */

			List<String> adjs = extractCountries(adjacencies);

			if (adjs != null) {
				HashMap<String, Integer> dest = new HashMap<>(); /* value for graph HashMap */
				for (int i = 0; i < adjs.size(); i++) {
					String countryId = stateName.getId(country);
					String adjId = stateName.getId(adjs.get(i));
					int dist = capdists.getDistance(countryId, adjId);
					if (countryId != null && adjId != null && dist != -1)
						dest.put(adjs.get(i), capdists.getDistance(countryId, adjId));
				}

				graph.put(country, dest);
			}
		}
	}
}
