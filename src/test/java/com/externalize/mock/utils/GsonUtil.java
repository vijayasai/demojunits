package com.externalize.mock.utils;

import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.*;



/**
 * Gson Utilities for NGP  
 * @author sso
 *
 */
public class GsonUtil {
	private static final Pattern jsonDatePattern = Pattern.compile("\\/Date\\(([-+]?\\d+)([-+]\\d+)?\\)\\/");
	private static final Pattern jsonAccNumPattern = Pattern.compile("\\d*");

	public static final String DATATYPE_CONFIGURATION_EXCEPTION_DATE =
			"DatatypeConfigurationException deserializing Date object";

	public static final String DATATYPE_CONFIGURATION_EXCEPTION_NUMBER =
			"DatatypeConfigurationException deserializing number";


	public static final Gson gson = new GsonBuilder().disableHtmlEscaping()
			.registerTypeAdapter(Date.class,
					new DateSerializer())
			.registerTypeAdapter(Date.class,
					new DateDeserializer())
			.registerTypeAdapter(HashMap.class, new JsonDeserializer<HashMap>() {
				public HashMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
						throws JsonParseException{
					HashMap<String,Object> resultMap=new HashMap<>();
					JsonObject jsonObject = json.getAsJsonObject();
					Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
					for (Map.Entry<String, JsonElement> entry : entrySet) {
						resultMap.put(entry.getKey(),entry.getValue());
					}
					return resultMap;
				}
			})
			.create();

	private GsonUtil(){

	}

	public static <T> T getObjectFromJson(String json, Class<T> classOfT) {
//		String methodName = "getObjectFromJson";
//		long startTime = System.currentTimeMillis();
		T retVal = gson.fromJson(json, classOfT);
//		long endTime = System.currentTimeMillis();
		return retVal;
	}

	public static <T> T getObjectFromJson(JsonObject json, Class<T> classOfT) {
//		String methodName = "getObjectFromJson";
//		long startTime = System.currentTimeMillis();
		T retVal = gson.fromJson(json, classOfT);
//		long endTime = System.currentTimeMillis();
		return retVal;
	}

	public static <T> T getObjectFromJson(Reader json, Class<T> classOfT) {
		T retVal = gson.fromJson(json, classOfT);
		return retVal;
	}

	public static String getJsonFromObject(Object obj) {
		String retVal = gson.toJson(obj);
		return retVal;
	}

	public static <T> T[] getObjectFromJsonArray(JsonArray jsonArray, Class<T[]> classOfT) {
		T[] retVal = gson.fromJson(jsonArray, classOfT);
		return retVal;
	}

	private static class DateSerializer implements
			JsonSerializer<Date> {
		public JsonElement serialize(Date src, Type typeOfSrc,
									 JsonSerializationContext context) {
			Date date = (Date) src;
			long milliseconds = date.getTime();
			//get the default time zone of the host
			TimeZone defaultTZ = TimeZone.getDefault();
			//get the offset of this time zone from UTC at the specified date
			int zoneOffsetMillisecond = defaultTZ.getOffset(milliseconds);
			String sign = "+";
			if (zoneOffsetMillisecond < 0) { // negative offset
				sign = "-";
				zoneOffsetMillisecond *= -1;
			}
			int minute = (int) (zoneOffsetMillisecond % (60 * 60 * 1000));
			int hour = (zoneOffsetMillisecond / 1000 / 60 / 60);
			//the number of milliseconds since January 1, 1970, 00:00:00 GMT represented by this date.
			DecimalFormat formatter = new DecimalFormat("#00.###");
			return new JsonPrimitive("/Date(" + milliseconds + sign + formatter.format(hour) + formatter.format(minute) + ")/");
		}
	}

	private static class DateDeserializer implements
			JsonDeserializer<Date> {
		public Date deserialize(JsonElement json, Type typeOfT,
								JsonDeserializationContext context) throws JsonParseException {
			try {
				String dateString = json.getAsString();
				Matcher matcher = jsonDatePattern.matcher(dateString);
				if (!matcher.find()){
					//Not in .net date string format
					return TimestampUtils.parseDate(dateString, TimestampUtils.ISO_8601_DATE_TIME_ZONE_FORMAT);
				}
				long milliseconds = Long.parseLong(matcher.group(1));
				String timezone = matcher.group(2);
				if (timezone==null||timezone.isEmpty()){
					timezone = "+0000";
				}

				// return new Date(long) which takes milliseconds since 1970 in UTC
				Date date = new Date (milliseconds);
				return date;
			} catch (Exception e) {
				throw new JsonParseException(DATATYPE_CONFIGURATION_EXCEPTION_DATE);
			}
		}
	}

//	private static class AcctNumDeserializer implements JsonDeserializer<EncryptedString> {
//		public EncryptedString deserialize(JsonElement json, Type typeOfT,
//			JsonDeserializationContext context) throws JsonParseException {
//			try {
//				String numString = json.getAsString();
//				Matcher matcher = jsonAccNumPattern.matcher(numString);
//				if (!matcher.find()){
//			        throw new ParseException("wrong number format " + numString , 0);
//			    }
//			    return new EncryptedString(EncryptDecryptUtil.encrypt(numString));
//			} catch (Exception e) {
//				throw new JsonParseException(DATATYPE_CONFIGURATION_EXCEPTION_NUMBER);
//			}
//		}
//	}

	public static Set<String> detectUnknownFieldNames(String json, Class<?> jsonClass) {
		if (json == null) {
			return  new HashSet<String>();
		}
		Set<String> privateFields = new HashSet<String>();
		Field[] allFields = jsonClass.getDeclaredFields();
		for (Field field : allFields) {
			if (Modifier.isPrivate(field.getModifiers())) {
				privateFields.add(field.getName());
			}
		}
		Map<String, Object> jsonAsMap = GsonUtil.getObjectFromJson(json, HashMap.class);
		if (jsonAsMap == null) {
			return  new HashSet<String>();
		}
		Set<String> jsonFields = jsonAsMap.keySet();
		jsonFields.removeAll(privateFields);
		return jsonFields;
	}
}
