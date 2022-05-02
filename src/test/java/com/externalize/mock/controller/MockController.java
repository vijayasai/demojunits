package com.externalize.mock.controller;


import com.externalize.mock.config.MockHelper;
import com.externalize.mock.exception.MockException;
import com.externalize.mock.model.MockCase;
import com.externalize.mock.model.MockHeader;
import com.externalize.mock.model.MockQueryParam;
import com.externalize.mock.model.MockValidation;
import com.externalize.mock.utils.FileUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class MockController {
	@Autowired
	@Qualifier("MOCK_CASES")
	private List<MockCase> mockCases;

	@RequestMapping(value = "/mock/**", method = RequestMethod.GET, headers="Accept=*/*")
	public ResponseEntity<String> getMock(@RequestParam Map<String, String> reqParam, HttpServletRequest request)throws Exception{
		return getMockResponse(reqParam, request);
	}

	@RequestMapping(value = "/mock/**", method = RequestMethod.POST, headers="Accept=*/*")
	public ResponseEntity<String> postMock(@RequestParam Map<String, String> reqParam, HttpServletRequest request)throws Exception{
		return getMockResponse(reqParam, request);
	}

	@RequestMapping(value = "/mock/**", method = RequestMethod.PUT, headers="Accept=*/*")
	public ResponseEntity<String> putMock(@RequestParam Map<String, String> reqParam, HttpServletRequest request)throws Exception{
		return getMockResponse(reqParam, request);
	}

	@RequestMapping(value = "/mock/**", method = RequestMethod.PATCH, headers="Accept=*/*")
	public ResponseEntity<String> patchMock(@RequestParam Map<String, String> reqParam, HttpServletRequest request)throws Exception{
		return getMockResponse(reqParam, request);
	}

	@RequestMapping(value = "/mock/**", method = RequestMethod.DELETE, headers="Accept=*/*")
	public ResponseEntity<String> deleteMock(@RequestParam Map<String, String> reqParam, HttpServletRequest request)throws Exception{
		return getMockResponse(reqParam, request);
	}

	private ResponseEntity<String> getMockResponse(Map<String, String> reqParam, HttpServletRequest request) throws MockException {
		String responseStr="";
		MockHelper mockHelper = MockHelper.getInstance(mockCases);
		String requestUri = request.getServletPath();
		String httpMehod = request.getMethod();
		String bodyStr = null;
		List<MockQueryParam> queryParamList = new LinkedList<>();
		if(reqParam!=null&&reqParam.size()>0) {
			reqParam.forEach((x, y) -> {
				MockQueryParam mockQueryParam = new MockQueryParam(x, y);
				queryParamList.add(mockQueryParam);
			});
		}
		if(httpMehod.equals("POST")){
			try {
				bodyStr = getBody(request);
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		// retrieves headers from request
		Enumeration<String> headerNames = request.getHeaderNames();
		List<MockHeader> requestHeaderList = new LinkedList<>();
		String headers = "";
		while (headerNames.hasMoreElements()) {
			String headerStr = headerNames.nextElement();
			String headerValue = request.getHeader(headerStr);
			MockHeader mockHeader = new MockHeader(headerStr, headerValue);
			requestHeaderList.add(mockHeader);
			headers = headers + " " + headerStr;
		}

		MockCase mockCase = mockHelper.getMockCaseByUri(httpMehod, requestUri, bodyStr, queryParamList, requestHeaderList);
		if(mockCase==null){
			mockCase = mockHelper.getMockCaseByRegEx(httpMehod, requestUri, bodyStr, queryParamList, requestHeaderList);
		}
		if(mockCase!=null){
			MockValidation validation = mockCase.getRequest().getValidation();
			if(validation!=null){
				List<MockHeader> mockHeaderList = validation.getHeader();
				if(mockHeaderList!=null&&!mockHeaderList.isEmpty()) {
					if (!requestHeaderList.containsAll(mockHeaderList)) {
						int httpStatus = mockCase.getResponse().getError().getHttpStatus();
						String errorMessage = mockCase.getResponse().getError().getMessage();
						if (errorMessage != null && httpStatus > 0) {
							errorMessage = errorMessage + " - Missing Headers: " + getMissingHeaders(mockHeaderList,	requestHeaderList);
							throw new MockException(errorMessage, httpStatus);
						} else {
							throw new MockException("Invalid Headers -  Request: " + headers + " vs. Mock Case: " + mockCase.getRequest().getValidation().getHeaderListString(), 500);
						}
					}
				}
			}
			int httpStatusCode = mockCase.getResponse().getHttpStatus();
			String responseFile = mockCase.getResponse().getBody().getFile();
			try {
				responseStr = FileUtil.readFile(responseFile);
			}catch (Exception e){
				throw new MockException("unable to read file: " + responseFile, 500);
			}
			List<MockHeader> mockHeaders = mockCase.getResponse().getHeader();

			return new ResponseEntity<>(responseStr, getHeaders(mockHeaders), HttpStatus.valueOf(httpStatusCode));
		}
		responseStr = "Resource not found for requestUri: " + requestUri;
		return new ResponseEntity<String>(responseStr, HttpStatus.NOT_FOUND);
	}

	public static String getMissingHeaders(List<MockHeader> mockHeaderList, List<MockHeader> requestHeaderList ){
		StringBuilder stringBuilder=new StringBuilder();
		mockHeaderList.forEach(
				x->{
					if(!requestHeaderList.contains(x)) {
						stringBuilder.append(x.toString() + " ");
					}
				}
		);
		return stringBuilder.toString();
	}

	public static HttpHeaders getHeaders(List<MockHeader> mockHeaders){
		HttpHeaders responseHeaders = new HttpHeaders();
		if(mockHeaders!=null) {
			mockHeaders.forEach(x->responseHeaders.add(x.getName(), x.getValue()));
		}
		return responseHeaders;
	}

	public static String getBody(HttpServletRequest request) throws IOException {
		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}
}