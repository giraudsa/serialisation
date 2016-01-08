package utils;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import giraudsa.marshall.annotations.IgnoreSerialise;

public class ConfigurationMarshalling {
	private static SimpleDateFormat dateFormatJson;
	private static SimpleDateFormat dateFormatXml;
	private static boolean idEstUniversel;
	
	private static Class<? extends Annotation> annotationIgnoreSerialise;
	static{
		idEstUniversel = false;
		TimeZone tz = TimeZone.getTimeZone("UTC");
		dateFormatJson = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatXml = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatJson.setTimeZone(tz);
		dateFormatXml.setTimeZone(tz);
		annotationIgnoreSerialise = IgnoreSerialise.class;
		
	}
	public static void setIdUniversel(){
		idEstUniversel = true;
	}
	public static void setDateFormatJson(SimpleDateFormat dateFormatJson){
		ConfigurationMarshalling.dateFormatJson = dateFormatJson;
	}
	
	public static void setDateFormatXml(SimpleDateFormat dateFormatXml){
		ConfigurationMarshalling.dateFormatXml = dateFormatXml;
	}
	
	public static <T extends Annotation> void setAnnotationIgnoreSerialise(Class<T> ignoreSerialiseAnnotation){
		if(ignoreSerialiseAnnotation != null) 
			annotationIgnoreSerialise = ignoreSerialiseAnnotation;
	}
	
	public static Class<? extends Annotation> getAnnotationIgnoreSerialise(){
		return annotationIgnoreSerialise;
	}
	
	public static SimpleDateFormat getDatFormatJson(){
		return dateFormatJson;
	}
	public static SimpleDateFormat getDateFormatXml(){
		return dateFormatXml;
	}
	public static boolean getEstIdUniversel(){
		return idEstUniversel;
	}

}
