package com.actemium.marshall.serialisation.xml;

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
import com.actemium.marshall.exception.NotImplementedSerializeException;
import com.actemium.marshall.serialisation.Marshaller;
import com.actemium.marshall.serialisation.xml.actions.ActionXmlCollectionType;
import com.actemium.marshall.serialisation.xml.actions.ActionXmlDate;
import com.actemium.marshall.serialisation.xml.actions.ActionXmlDictionaryType;
import com.actemium.marshall.serialisation.xml.actions.ActionXmlSimpleComportement;
import com.actemium.marshall.serialisation.xml.actions.ActionXmlVoid;

public class XmlMarshaller extends Marshaller {
	/////METHODES STATICS PUBLICS
	public static <U> void toXml(U obj, StringWriter output) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException  {
		XmlMarshaller v = new XmlMarshaller(output);
		v.marshall(obj);
	}
	public static <U> String ToXml(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		try(StringWriter sw = new StringWriter()){
			toXml(obj, sw);
			return sw.toString();
		}
	}
	public static <U> void toCompleteXml(U obj, StringWriter output) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		XmlMarshaller v = new XmlMarshaller(output);
		v.marshallAll(obj);
	}
	public static <U> String toCompleteXml(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException{
		try(StringWriter sw = new StringWriter()){
			toCompleteXml(obj, sw);
			return sw.toString();
		}
	}

	
	//////CONSTRUCTEUR
	private XmlMarshaller(Writer writer) throws IOException {
		this.writer = writer;
		writeHeader();
		behaviors.put(Date.class, ActionXmlDate.class);
		behaviors.put(Timestamp.class, ActionXmlDate.class);
		behaviors.put(Boolean.class, ActionXmlSimpleComportement.class);
		behaviors.put(Collection.class, ActionXmlCollectionType.class);
		behaviors.put(Map.class, ActionXmlDictionaryType.class);
		behaviors.put(Object.class, ActionXml.class);
		behaviors.put(void.class, ActionXmlVoid.class);
		behaviors.put(Integer.class, ActionXmlSimpleComportement.class);
		behaviors.put(Enum.class, ActionXmlSimpleComportement.class);
		behaviors.put(UUID.class, ActionXmlSimpleComportement.class);
		behaviors.put(String.class, ActionXmlSimpleComportement.class);
		behaviors.put(Byte.class, ActionXmlSimpleComportement.class);
		behaviors.put(Float.class, ActionXmlSimpleComportement.class);
		behaviors.put(Double.class, ActionXmlSimpleComportement.class);
		behaviors.put(Long.class, ActionXmlSimpleComportement.class);
		behaviors.put(Short.class, ActionXmlSimpleComportement.class);
	}

	
	/////METHODES 
	private <T> void marshall(T obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		if (obj == null) return ;
		marshallSpecialise(obj, TypeRelation.COMPOSITION, null);
	}
	
	private <T> void marshallAll(T obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException, IOException{
		if (obj == null) return ;
		aSerialiser = new SetQueue<>();
		marshallSpecialise(obj, TypeRelation.COMPOSITION, null);
		while(!aSerialiser.isEmpty()){
			marshallSpecialise(aSerialiser.poll(), TypeRelation.COMPOSITION, null);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected <T> void marshallSpecialise(T obj, TypeRelation relation, String nom) throws NotImplementedSerializeException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, IOException {
		Class<T> typeObj = (Class<T>) obj.getClass();
		Class<? extends ActionXml> behaviorXml = (Class<? extends ActionXml>)getBehavior(obj);
		ActionXml<T> action =  (ActionXml<T>)behaviorXml.getConstructor(Class.class, String.class).newInstance(typeObj, nom);
		action.marshall(obj, relation, this, aSerialiser);
	}
	
	private void writeHeader() throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}

	void openTag(String name, Class<?> type) throws IOException {
		writer.write("<");
		writer.write(name);
		writer.write(" type=\"");
		writer.write(Constants.getSmallNameType(type));
		writer.write("\">");
	}

	void closeTag(String name) throws IOException {
		writer.write("</");
		writer.write(name);
		writer.write('>');
	}

}
