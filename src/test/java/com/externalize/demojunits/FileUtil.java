package com.externalize.demojunits;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

@Component
public class FileUtil {

	public static String readJsonFile(String filePath) {
		String jsonString = null;
		try (Reader reader = new InputStreamReader(new FileInputStream(filePath), "UTF-8")) {
			jsonString = FileCopyUtils.copyToString(reader);
		} catch (Exception e) {
			System.out.println("here in exception = " + e.getMessage());
		}
		System.out.println("jsonString= " + jsonString);
		return jsonString;
	}
}
