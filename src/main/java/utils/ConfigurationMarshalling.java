package utils;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import giraudsa.marshall.annotations.IgnoreSerialise;

public class ConfigurationMarshalling {
	private static Class<? extends Annotation> annotationIgnoreSerialise = IgnoreSerialise.class;
	private static boolean idEstUniversel = false;
	private static final ConfigurationMarshalling instance = new ConfigurationMarshalling();
	private static boolean modelContraignant = true;
	private static boolean prettyPrint = false;

	public static Class<? extends Annotation> getAnnotationIgnoreSerialise() {
		return annotationIgnoreSerialise;
	}

	public static synchronized SimpleDateFormat getDateFormatXml() {
		return instance.dateFormatXml;
	}

	public static synchronized SimpleDateFormat getDatFormatJson() {
		return instance.dateFormatJson;
	}

	public static boolean getEstIdUniversel() {
		return idEstUniversel;
	}

	public static boolean isModelContraignant() {
		return modelContraignant;
	}

	public static boolean isPrettyPrint() {
		return prettyPrint;
	}

	public static <T extends Annotation> void setAnnotationIgnoreSerialise(final Class<T> ignoreSerialiseAnnotation) {
		if (ignoreSerialiseAnnotation != null && annotationIgnoreSerialise != ignoreSerialiseAnnotation) {
			annotationIgnoreSerialise = ignoreSerialiseAnnotation;
			TypeExtension.clear();
		}
	}

	public static void setContrainteModel(final boolean contraindre) {
		modelContraignant = contraindre;
	}

	public static synchronized void setDateFormatJson(final SimpleDateFormat dateFormatJson) {
		if (dateFormatJson != null)
			instance.dateFormatJson = dateFormatJson;
	}

	public static synchronized void setDateFormatXml(final SimpleDateFormat dateFormatXml) {
		if (dateFormatXml != null)
			instance.dateFormatXml = dateFormatXml;
	}

	public static void setIdUniversel() {
		idEstUniversel = true;
	}

	public static void setPrettyPrint() {
		prettyPrint = true;
	}

	private SimpleDateFormat dateFormatJson;

	private SimpleDateFormat dateFormatXml;

	private ConfigurationMarshalling() {
		final TimeZone tz = TimeZone.getTimeZone("UTC");
		dateFormatJson = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatXml = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatJson.setTimeZone(tz);
		dateFormatXml.setTimeZone(tz);
	}

}
