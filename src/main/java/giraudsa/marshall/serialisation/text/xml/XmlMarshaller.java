package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.TextMarshaller;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlCollectionType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlDate;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlDictionaryType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlSimpleComportement;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlVoid;

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

public class XmlMarshaller extends TextMarshaller {
	/////METHODES STATICS PUBLICS
	public static <U> void toXml(U obj, Writer output) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException  {
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
	private XmlMarshaller(Writer output) throws IOException {
		super(output);
		writeHeader();
		dicoTypeToTypeAction.put(Date.class, ActionXmlDate.class);
		dicoTypeToTypeAction.put(Timestamp.class, ActionXmlDate.class);
		dicoTypeToTypeAction.put(Boolean.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(Collection.class, ActionXmlCollectionType.class);
		dicoTypeToTypeAction.put(Map.class, ActionXmlDictionaryType.class);
		dicoTypeToTypeAction.put(Object.class, ActionXml.class);
		dicoTypeToTypeAction.put(void.class, ActionXmlVoid.class);
		dicoTypeToTypeAction.put(Integer.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(Enum.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(UUID.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(String.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(Byte.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(Float.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(Double.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(Long.class, ActionXmlSimpleComportement.class);
		dicoTypeToTypeAction.put(Short.class, ActionXmlSimpleComportement.class);
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
		Class<? extends ActionXml> behaviorXml = (Class<? extends ActionXml>)getTypeAction(obj);
		ActionXml<T> action =  (ActionXml<T>)behaviorXml.getConstructor(Class.class, XmlMarshaller.class, String.class).newInstance(typeObj, this, nom);
		action.marshall(obj, relation);
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
