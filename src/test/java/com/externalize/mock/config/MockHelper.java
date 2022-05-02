package com.externalize.mock.config;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.externalize.mock.exception.MockException;
import com.externalize.mock.model.MockCase;
import com.externalize.mock.model.MockHeader;
import com.externalize.mock.model.MockQueryParam;
import com.externalize.mock.utils.GsonUtil;

public class MockHelper {
    private static Map<String, List<MockCase>> mockCasesByUri;
    private static Map<String, MockCase> mockCaseByRegEx;
    private static List<MockCase> mockCaseWithRegExList;
    private static List<MockCase> mockCaseWithUriList;
    private static MockHelper singleInstance = null;
    private static String METHOD_KEY_SEPARATOR="-";

    public static MockHelper getInstance(List<MockCase> mockCases){
        if(singleInstance==null){
            singleInstance = new MockHelper();
            mockCasesByUri = new HashMap<>();
            mockCaseWithUriList = mockCases.stream().filter(x-> x.getRequest().getUri().getExactMatch() != null).collect(Collectors.toList());
            mockCaseWithUriList.forEach(x ->{
                        String uriKey = x.getRequest().getHttpMethod().toLowerCase() + METHOD_KEY_SEPARATOR + x.getRequest().getUri().getExactMatch();
                        List<MockCase> mockCaseList = mockCasesByUri.get(uriKey);
                        if(mockCaseList==null){
                            mockCaseList = new LinkedList<>();
                            mockCasesByUri.put(uriKey, mockCaseList);
                        }
                        mockCaseList.add(x);
                    });
            mockCaseByRegEx = new HashMap<>();
            mockCaseWithRegExList = mockCases.stream().filter(x-> x.getRequest().getUri().getRegEx() != null).collect(Collectors.toList());
            mockCaseWithRegExList.forEach(x ->{
                        String regEx = x.getRequest().getUri().getRegEx();
                        mockCaseByRegEx.put(x.getRequest().getHttpMethod().toLowerCase() + METHOD_KEY_SEPARATOR + regEx, x);
                    });
        }
        return singleInstance;
    }


    public List<MockCase> getMockCasesByUri(String httpMethod, String uri){
        if(httpMethod == null || uri == null){
            return null;
        }
        List<MockCase> mockCases;
        mockCases = mockCasesByUri.get(httpMethod.toLowerCase() + METHOD_KEY_SEPARATOR + uri);
        return mockCases;
    }

    public MockCase getMockCaseByUri(String httpMethod, String uri, String bodyStr, List<MockQueryParam> queryParamList, List<MockHeader> requestHeaderList) throws MockException{
        if(httpMethod == null || uri == null){
            return null;
        }
        MockCase mockCase = null;
        List<MockCase> mockCases = getMockCasesByUri(httpMethod, uri);
        if(mockCases!=null){
            if(mockCases.size()==1){
                mockCase = mockCases.get(0);
            }else if(mockCases.size()>0){
                mockCase = getMockCaseByBodyStr(mockCases, bodyStr, queryParamList, requestHeaderList);
            }
        }
        return mockCase;
    }

    /**
     *
     * @param mockCases
     * @param bodyStr
     * @param queryParamList
     * @param requestHeaderList
     * @return
     * @throws MockException
     * this method iterates through all the Mock cases and filter out based on mockcasse queryParamslist, requestHeaderList,
     * targetUriText, & TargetUrlMethodVale which matched with bodyString queryParamslist, requestHeaderList,
     * targetUriText, & TargetUrlMethodVales if anything matches will return specific Mock cases or else return null
     *
     */
    private MockCase getMockCaseByBodyStr(List<MockCase> mockCases, String bodyStr, List<MockQueryParam> queryParamList, List<MockHeader> requestHeaderList) throws MockException{
        MockCase mockCase = null;
        List<MockCase> subMockCases = mockCases;
        if(queryParamList!=null && queryParamList.size()>0){
            subMockCases = mockCases.stream().filter(x->{
                boolean isMatched=true;
                List<MockQueryParam> mockQueryParams = x.getRequest().getQueryParam();
                if(mockQueryParams!=null && mockQueryParams.size()>0){
                     if(!queryParamList.containsAll(mockQueryParams)){
                         isMatched = false;
                     }
                }
                return isMatched;
            }).collect(Collectors.toList());
        }else{
            subMockCases = mockCases.stream().filter(x->{
                boolean isMatched=false;
                List<MockQueryParam> mockQueryParams = x.getRequest().getQueryParam();
                if(mockQueryParams==null || mockQueryParams.size()==0){
                    isMatched=true;
                }
                return isMatched;
            }).collect(Collectors.toList());
            if(subMockCases==null || subMockCases.size()==0){
                subMockCases = mockCases;
            }
        }
        if(subMockCases==null || subMockCases.size()==0){
            throw new MockException("Resource not found", 404);
        }
        if(bodyStr == null) {
            mockCase = subMockCases.get(0);
            if(subMockCases.size()>1) {
                subMockCases = filterCasesWithHeaders(subMockCases, requestHeaderList);
                if(subMockCases!=null && subMockCases.size()>0){
                    mockCase = subMockCases.get(0);
                }
            }
        }else{
            // below method iterate through the mock cases and compare the mock case targetURI, targetMethod with bodyString
            // targetURI, targetMethod values if both are same then return the specific mock case or else continue with other
            // conditions.
            List<MockCase> matchedBodyCases = isTargetMatchesWithMockCase(subMockCases, bodyStr);
            if(matchedBodyCases == null) {
                matchedBodyCases = new LinkedList<>();
                // iterate through all the mock cases and compare the mock case Pattern, body exact match url with body string
                // if any matches return specific Mock case or else return null.
                for (MockCase mCase : subMockCases) {
                    Pattern pattern = mCase.getRequest().getBody().getPattern();
                    String exactMatchStr = mCase.getRequest().getBody().getExactMatch();
                    if(pattern!=null){
                        if(pattern.matcher(bodyStr).matches()){
                            mockCase = mCase;
                            matchedBodyCases.add(0, mockCase);
                        } else if(bodyStr.contains(exactMatchStr)){
                            mockCase = mCase;
                            matchedBodyCases.add(mockCase);
                        }
                    }else if(exactMatchStr != null){
                        if(bodyStr.contains(exactMatchStr)) {
                            mockCase = mCase;
                            matchedBodyCases.add(mockCase);
                            matchedBodyCases.add(0, mockCase);
                        }
                    }else{
                        mockCase = mCase;
                        matchedBodyCases.add(mockCase);
                    }
                }
            }
            matchedBodyCases = filterCasesWithHeaders(matchedBodyCases, requestHeaderList);
            if(matchedBodyCases!=null && matchedBodyCases.size()>0){
                mockCase = matchedBodyCases.get(0);
            }
        }
        return mockCase;
    }

    public List<MockCase> filterCasesWithHeaders(List<MockCase> mockCases, List<MockHeader> requestHeaderList){
        if(requestHeaderList==null || mockCases==null){
            return mockCases;
        }
        List<MockCase> subMockCases =  mockCases.stream().filter(mCase -> {
            boolean isHeaderMatched = false;
            List<MockHeader> headers = mCase.getRequest().getHeader();
            if(headers!=null && headers.size()>0) {
                isHeaderMatched = requestHeaderList.containsAll(headers);
            }
            return isHeaderMatched;
        }).collect(Collectors.toList());
        if(subMockCases==null || subMockCases.size()==0){
            subMockCases = mockCases;
        }
        return subMockCases;
    }

    public MockCase getMockCaseByRegEx(String httpMethod, String uri, String bodyStr, List<MockQueryParam> queryParamList, List<MockHeader> requestHeaderList) throws MockException{
    	if(httpMethod == null || uri == null){
            return null;
        }
        MockCase mockCase = null;
        List<MockCase> mockCases = getMockCasesByRegEx(httpMethod, uri);
        if(mockCases!=null){
            if(mockCases.size()==1){
                mockCase = mockCases.get(0);
            }else if(mockCases.size()>0){
                mockCase = getMockCaseByBodyStr(mockCases, bodyStr, queryParamList, requestHeaderList);
            }
        }
        return mockCase;
    }

    public List<MockCase> getMockCasesByRegEx(String httpMethod, String uri){
    	if(httpMethod == null || uri == null){
            return null;
        }
        List<MockCase> mockCases=new LinkedList<>();
        for(MockCase regExCase: mockCaseWithRegExList){
            if(httpMethod.equalsIgnoreCase(regExCase.getRequest().getHttpMethod())) {
                Pattern pattern = regExCase.getRequest().getUri().getPattern();
                if (pattern.matcher(uri).matches()){
                    mockCases.add(regExCase);
                }
            }
        }
        return mockCases;
    }

    /**
    *
    * @param matchedBodyCases
    * @param bodyStr
    * @return
    * this method itherate through the mockcasess and compare the mockacse targetURI, targetMethod with bodyString targetURI,
    * targetMethod values if both are same then return the specific mockcase or else return null
    */
   public List<MockCase> isTargetMatchesWithMockCase(List<MockCase> matchedBodyCases, String bodyStr){
       Map<String, String> bodyStrMap = convertStringToJsonMap(bodyStr);
       List<MockCase> mockCases = null;
       if(bodyStrMap != null){
           String targetUriTxt = bodyStrMap.get("targetUriTxt");
           String targetHttpMethodVal = bodyStrMap.get("targetHttpMethodVal");
           if(targetUriTxt != null && targetHttpMethodVal != null){
        	   
               Predicate<MockCase> patternPredicate =
                       item -> item.getRequest().getBody().getPattern() !=null &&
                               item.getRequest().getBody().getPattern().matcher(bodyStr).matches() &&
                               item.getRequest().getBody().getTargetUriList() !=null &&
                               item.getRequest().getBody().getTargetHttpMethodValList() !=null &&
                               item.getRequest().getBody().getTargetUriList().stream().anyMatch(uriText -> uriText.equals(targetUriTxt)) &&
                               item.getRequest().getBody().getTargetHttpMethodValList().stream().anyMatch(uriHttpMethodVal -> uriHttpMethodVal.equals(targetHttpMethodVal));
               mockCases = matchedBodyCases.stream().filter(patternPredicate).collect(Collectors.toList());
               if(mockCases.isEmpty()){
                   Predicate<MockCase> targetPredicateListCond =
                           item -> item.getRequest().getBody().getTargetUriList() !=null &&
                                   item.getRequest().getBody().getTargetHttpMethodValList() !=null &&
                                   item.getRequest().getBody().getTargetUriList().stream().anyMatch(uriText -> uriText.equals(targetUriTxt)) &&
                                   item.getRequest().getBody().getTargetHttpMethodValList().stream().anyMatch(uriHttpMethodVal -> uriHttpMethodVal.equals(targetHttpMethodVal));
                   mockCases = matchedBodyCases.stream().filter(targetPredicateListCond).collect(Collectors.toList());
               }
           }
       }
       return mockCases;
   }

   /**
    *
    * @param bodyStr
    * @return
    */
   public Map<String, String> convertStringToJsonMap(String bodyStr) {

       try {
           return  GsonUtil.getObjectFromJson(bodyStr, Map.class);
       } catch (Exception e) {
           //System.err.println("In convertStringToJsonMap error message:::"+e.getMessage());
           return null;
       }
   }
 
}
