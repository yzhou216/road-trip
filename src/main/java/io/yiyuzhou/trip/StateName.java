package io.yiyuzhou.trip;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class StateName {
	private HashMap<String, Integer> countries;

	public StateName() {
		countries = new HashMap<>();
		parseStateNames();
	}

	public int getNumber(String name) {
		return countries.get(name);
	}

	private void parseStateNames() {
		try {
			/* load the TSV file from the resources directory */
			InputStream inputStream = StateName.class.getClassLoader().getResourceAsStream("state_name.tsv");
			Reader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			/* define the TSV format and create the parser */
			CSVFormat format = CSVFormat.TDF.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
			try (CSVParser csvParser = new CSVParser(reader, format)) {
				for (CSVRecord record : csvParser) {
					String stateId = record.get("stateid");
					String stateNumber = record.get("statenumber");
					countries.put(stateId, Integer.parseInt(stateNumber));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
