package nl.idgis.geoportaal.conversie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Mapper {

	private String file;
	private Map<String, String> mapping;

	public Mapper(String file) {
		this.file = file;
	}

	private Map<String, String> getMapping() throws Exception {
		if (mapping != null)
			return mapping;

		mapping = new HashMap<>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(Mapper.class.getClassLoader().getResourceAsStream(file)));

		String line = null;
		try {
			while((line = reader.readLine()) != null) {
				int i = line.indexOf(',');
				String stringOne = line.substring(0, i).replace("\"", "");
				String stringTwo = line.substring(i + 1).replace("\"", "");;
				mapping.put(stringOne, stringTwo);
			}
		} catch (IOException e) {
			throw new Exception("kon regel na " + line + " niet lezen");
		}

		return mapping;
	}

	public String get(String key) throws Exception {
		return getMapping().get(key);
	}
}
