package com.externalize.demojunits;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.externalize.demojunits.controller.TestRestController;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { TestRestController.class }, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext
public class SampleE2ETests {

	@Value("${mock.data.path}")
	private String mockfilePath;
	
	@Test
	public void testSample() {
		System.out.print("mockfilePath ="+mockfilePath);
		readJsonString();
	}
	
	private String readJsonString() {
		String jsonString = null;
		try {
			jsonString = FileUtil.readJsonFile(mockfilePath);
		} catch (Exception e) {
			System.out.print("final execption ="+e.getMessage());
		}
		System.out.print("final jsonString ="+jsonString);
		return jsonString;
	}
}
