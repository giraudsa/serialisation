package utils;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import giraudsa.marshall.annotations.IgnoreSerialise;

public class ConfigurationMarshalling {
	private SimpleDateFormat dateFormatJson;
	private SimpleDateFormat dateFormatXml;
	private static boolean idEstUniversel = false;
	private static boolean prettyPrint = false;
	private static Class<? extends Annotation> annotationIgnoreSerialise= IgnoreSerialise.class;
	private static final ConfigurationMarshalling instance = new ConfigurationMarshalling();
	private static boolean modelContraignant = true;
	
	private ConfigurationMarshalling(){
		TimeZone tz = TimeZone.getTimeZone("UTC");
		dateFormatJson = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatXml = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatJson.setTimeZone(tz);
		dateFormatXml.setTimeZone(tz);
	}
	
	public static void setIdUniversel(){
		idEstUniversel = true;
	}
	public static synchronized void setDateFormatJson(SimpleDateFormat dateFormatJson){
		if(dateFormatJson != null)
			instance.dateFormatJson = dateFormatJson;
	}
	
	public static synchronized void setDateFormatXml(SimpleDateFormat dateFormatXml){
		if (dateFormatXml != null)
			instance.dateFormatXml = dateFormatXml;
	}
	
	public static <T extends Annotation> void setAnnotationIgnoreSerialise(Class<T> ignoreSerialiseAnnotation){
		if(ignoreSerialiseAnnotation != null && annotationIgnoreSerialise != ignoreSerialiseAnnotation){ 
			annotationIgnoreSerialise = ignoreSerialiseAnnotation;
			TypeExtension.serializablefieldsOfType.clear();
		}
	}
	
	public static Class<? extends Annotation> getAnnotationIgnoreSerialise(){
		return annotationIgnoreSerialise;
	}
	
	public static synchronized SimpleDateFormat getDatFormatJson(){
		return instance.dateFormatJson;
	}
	public static synchronized SimpleDateFormat getDateFormatXml(){
		return instance.dateFormatXml;
	}
	public static boolean getEstIdUniversel(){
		return idEstUniversel;
	}
	public static void setPrettyPrint(){
		prettyPrint = true;
	}
	public static boolean isPrettyPrint(){
		return prettyPrint;
	}
	
	public static void setContrainteModel(boolean contraindre){
		modelContraignant = contraindre;
	}

	public static boolean isModelContraignant() {
		return modelContraignant;
	}

}
