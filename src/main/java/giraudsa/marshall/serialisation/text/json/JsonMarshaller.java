package giraudsa.marshall.serialisation.text.json;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.TextMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonDictionary;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonObject;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonBoolean;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonDate;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonInteger;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonString;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonVoid;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.Constants;

public class JsonMarshaller extends TextMarshaller {
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonMarshaller.class);

	// /////METHODES PUBLIQUES STATIQUES
	public static <U> void toJson(U obj, Writer output) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
	SecurityException, IOException, NotImplementedSerializeException {
		JsonMarshaller v = new JsonMarshaller(output, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), false);
		LOGGER.debug("debut de sérialisation de " + obj.getClass());
		v.marshall(obj);
		LOGGER.debug("fin de sérialisation de " + obj.getClass());
	}

	public static <U> String toJson(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
	IOException, NotImplementedSerializeException {
		try (StringWriter sw = new StringWriter()) {
			toJson(obj, sw);
			return sw.toString();
		}
	}

	public static <U> void toJson(U obj, Writer output, DateFormat df) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
	SecurityException, IOException, NotImplementedSerializeException {
		JsonMarshaller v = new JsonMarshaller(output, df, false);
		v.marshall(obj);
	}

	public static <U> String toJson(U obj, DateFormat df) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
	IOException, NotImplementedSerializeException {
		try (StringWriter sw = new StringWriter()) {
			toJson(obj, sw, df);
			return sw.toString();
		}
	}
	public static <U> void toCompleteJson(U obj, Writer output) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		JsonMarshaller v = new JsonMarshaller(output, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"), true);
		v.marshall(obj);
	}


	public static <U> String toCompleteJson(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException{
		try(StringWriter sw = new StringWriter()){
			toCompleteJson(obj, sw);
			return sw.toString();
		}
	}

	public static <U> void toCompleteJson(U obj, Writer output, DateFormat df) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		JsonMarshaller v = new JsonMarshaller(output, df, true);
		v.marshall(obj);
	}
	public static <U> String toCompleteJson(U obj, DateFormat df) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException{
		try(StringWriter sw = new StringWriter()){
			toCompleteJson(obj, sw, df);
			return sw.toString();
		}
	}

	// ///CONSTRUCTEUR
	private JsonMarshaller(Writer output, DateFormat df, boolean isCompleteSerialisation) throws IOException {
		super(output, df, isCompleteSerialisation);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void initialiseDico() {
		dicoTypeToAction.put(Date.class, new ActionJsonDate(this));
		dicoTypeToAction.put(Boolean.class, new ActionJsonBoolean(this));
		dicoTypeToAction.put(Collection.class, new ActionJsonCollectionType(this));
		dicoTypeToAction.put(Map.class, new ActionJsonDictionary(this));
		dicoTypeToAction.put(Object.class, new ActionJsonObject(this));
		dicoTypeToAction.put(void.class, new ActionJsonVoid(this));
		dicoTypeToAction.put(Integer.class, new ActionJsonInteger(this));
		dicoTypeToAction.put(Enum.class, new ActionJsonSimpleWithQuote<Enum>(this));
		dicoTypeToAction.put(UUID.class, new ActionJsonSimpleWithQuote<UUID>(this));
		dicoTypeToAction.put(String.class, new ActionJsonString(this));
		dicoTypeToAction.put(Byte.class, new ActionJsonSimpleWithoutQuote<Byte>(this));
		dicoTypeToAction.put(Float.class, new ActionJsonSimpleWithoutQuote<Float>(this));
		dicoTypeToAction.put(Double.class, new ActionJsonSimpleWithoutQuote<Double>(this));
		dicoTypeToAction.put(Long.class, new ActionJsonSimpleWithoutQuote<Long>(this));
		dicoTypeToAction.put(Short.class, new ActionJsonSimpleWithoutQuote<Short>(this));
	}

	// ///ME
	
	void ecritClef(String nomClef) throws IOException {
		if(nomClef != null)	writer.write("\"" + nomClef + "\":");
	}

	void ecritType(Class<?> type) throws IOException {
		ecritClef(Constants.CLEF_TYPE);
		String stringType = Constants.getSmallNameType(type);
		writeWithQuote(stringType);
	}

	public void writeWithQuote(String string) throws IOException {
		writer.write("\"" + string + "\"");
	}

	public void writeSeparator() throws IOException {
		writer.write(",");
	}
}
