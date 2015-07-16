package com.actemium.marshall.serialisation.json;

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

import com.actemium.marshall.annotations.TypeRelation;
import com.actemium.marshall.deserialisation.json.Container;
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.json.actions.ActionJsonCollectionType;
import com.actemium.marshall.serialisation.json.actions.ActionJsonDate;
import com.actemium.marshall.serialisation.json.actions.ActionJsonDictionary;
import com.actemium.marshall.serialisation.json.actions.ActionJsonSimpleComportement;
import com.actemium.marshall.serialisation.json.actions.ActionJsonString;
import com.actemium.marshall.serialisation.json.actions.ActionJsonVoid;

public class JsonMarshaller extends Marshaller {
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
		this.writer = output;
		behaviors.put(Date.class, ActionJsonDate.class);
		behaviors.put(Timestamp.class, ActionJsonDate.class);
		behaviors.put(Boolean.class, ActionJsonSimpleComportement.class);
		behaviors.put(Collection.class, ActionJsonCollectionType.class);
		behaviors.put(Map.class, ActionJsonDictionary.class);
		behaviors.put(Object.class, ActionJson.class);
		behaviors.put(void.class, ActionJsonVoid.class);
		behaviors.put(Integer.class, ActionJsonSimpleComportement.class);
		behaviors.put(Enum.class, ActionJsonString.class);
		behaviors.put(UUID.class, ActionJsonString.class);
		behaviors.put(String.class, ActionJsonString.class);
		behaviors.put(Byte.class, ActionJsonSimpleComportement.class);
		behaviors.put(Float.class, ActionJsonSimpleComportement.class);
		behaviors.put(Double.class, ActionJsonSimpleComportement.class);
		behaviors.put(Long.class, ActionJsonSimpleComportement.class);
		behaviors.put(Short.class, ActionJsonSimpleComportement.class);
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
		Class<? extends ActionJson> behaviorJson = (Class<? extends ActionJson>)getBehavior(obj);
		ActionJson<T> action =  (ActionJson<T>)behaviorJson.getConstructor(Class.class, String.class).newInstance(typeObj, nom);
		action.marshall(obj, relation, this, aSerialiser);
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
