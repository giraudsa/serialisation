package giraudsa.marshall.serialisation.text.json;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.text.json.Container;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.TextMarshaller;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonDate;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonDictionary;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleComportement;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonString;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonVoid;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import utils.Constants;

public class JsonMarshaller extends TextMarshaller {
	// /////METHODES PUBLIQUES STATIQUES
	public static <U> void toJson(U obj, Writer output) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
			SecurityException, IOException, NotImplementedSerializeException {
		JsonMarshaller v = new JsonMarshaller(output);
		v.marshall(obj);
	}

	public static <U> String ToJson(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
			IOException, NotImplementedSerializeException {
		try (StringWriter sw = new StringWriter()) {
			toJson(obj, sw);
			return sw.toString();
		}
	}

	public static <U> void toCompleteJson(U obj, Writer output) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		JsonMarshaller v = new JsonMarshaller(output);
		v.marshallAll(obj);
	}
	public static <U> String toCompleteJson(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException{
		try(StringWriter sw = new StringWriter()){
			toCompleteJson(obj, sw);
			return sw.toString();
		}
	}
	
	// ///CONSTRUCTEUR
	private JsonMarshaller(Writer output) throws IOException {
		super(output);
		dicoTypeToTypeAction.put(Date.class, ActionJsonDate.class);
		dicoTypeToTypeAction.put(Timestamp.class, ActionJsonDate.class);
		dicoTypeToTypeAction.put(Boolean.class, ActionJsonSimpleComportement.class);
		dicoTypeToTypeAction.put(Collection.class, ActionJsonCollectionType.class);
		dicoTypeToTypeAction.put(Map.class, ActionJsonDictionary.class);
		dicoTypeToTypeAction.put(Object.class, ActionJson.class);
		dicoTypeToTypeAction.put(void.class, ActionJsonVoid.class);
		dicoTypeToTypeAction.put(Integer.class, ActionJsonSimpleComportement.class);
		dicoTypeToTypeAction.put(Enum.class, ActionJsonString.class);
		dicoTypeToTypeAction.put(UUID.class, ActionJsonString.class);
		dicoTypeToTypeAction.put(String.class, ActionJsonString.class);
		dicoTypeToTypeAction.put(Byte.class, ActionJsonSimpleComportement.class);
		dicoTypeToTypeAction.put(Float.class, ActionJsonSimpleComportement.class);
		dicoTypeToTypeAction.put(Double.class, ActionJsonSimpleComportement.class);
		dicoTypeToTypeAction.put(Long.class, ActionJsonSimpleComportement.class);
		dicoTypeToTypeAction.put(Short.class, ActionJsonSimpleComportement.class);
	}

	// ///ME
	private <U> void marshall(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		if (obj == null)
			return;
		marshallSpecialise(obj, TypeRelation.COMPOSITION, null);
	}

	private <U> void marshallAll(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		if (obj == null)
			return;
		aSerialiser = new SetQueue<Object>();
		writer.write("{");
		ecritType(Container.class);
		writer.write(", ");
		marshallSpecialise(obj, TypeRelation.COMPOSITION, "__principal");
		writer.write(",\"__references\":[");
		while (!aSerialiser.isEmpty()) {
			writer.write(",");
			marshallSpecialise(aSerialiser.poll(), TypeRelation.COMPOSITION, null);
		}
		writer.write("]}");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> void marshallSpecialise(T obj, TypeRelation relation, String nom) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		Class<T> typeObj = (Class<T>) obj.getClass();
		Class<? extends ActionJson> behaviorJson = (Class<? extends ActionJson>)getTypeAction(obj);
		ActionJson<T> action =  (ActionJson<T>)behaviorJson.getConstructor(Class.class, JsonMarshaller.class, String.class).newInstance(typeObj, this, nom);
		action.marshall(obj, relation);
	}

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
}
