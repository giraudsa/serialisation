package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.TextMarshaller;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlCollectionType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlDate;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlDictionaryType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlObject;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlSimpleComportement;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlVoid;

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

import utils.Constants;

public class XmlMarshaller extends TextMarshaller {
	/////METHODES STATICS PUBLICS
	public static <U> void toXml(U obj, Writer output) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException  {
		XmlMarshaller v = new XmlMarshaller(output, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"), false);
		v.marshall(obj);
	}
	public static <U> String ToXml(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		try(StringWriter sw = new StringWriter()){
			toXml(obj, sw);
			return sw.toString();
		}
	}
	public static <U> void toXml(U obj, Writer output, DateFormat df) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException  {
		XmlMarshaller v = new XmlMarshaller(output, df, false);
		v.marshall(obj);
	}
	public static <U> String ToXml(U obj, DateFormat df) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
		try(StringWriter sw = new StringWriter()){
			toXml(obj, sw, df);
			return sw.toString();
		}
	}	
	public static <U> void toCompleteXml(U obj, Writer output) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		XmlMarshaller v = new XmlMarshaller(output, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"), true);
		v.marshall(obj);
	}
	public static <U> String toCompleteXml(U obj) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException{
		try(StringWriter sw = new StringWriter()){
			toCompleteXml(obj, sw);
			return sw.toString();
		}
	}

	public static <U> void toCompleteXml(U obj, StringWriter output, DateFormat df) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException{
		XmlMarshaller v = new XmlMarshaller(output, df, true);
		v.marshall(obj);
	}
	public static <U> String toCompleteXml(U obj, DateFormat df) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException{
		try(StringWriter sw = new StringWriter()){
			toCompleteXml(obj, sw, df);
			return sw.toString();
		}
	}
	
	//////CONSTRUCTEUR
	private XmlMarshaller(Writer output, DateFormat df, boolean isCompleteSerialisation) throws IOException {
		super(output, df, isCompleteSerialisation);
		writeHeader();
	}
	
	@SuppressWarnings("rawtypes")
	@Override protected void initialiseDico() {
		dicoTypeToAction.put(Date.class, new ActionXmlDate(this));
		dicoTypeToAction.put(Boolean.class, new ActionXmlSimpleComportement<Boolean>(this));
		dicoTypeToAction.put(Collection.class, new ActionXmlCollectionType(this));
		dicoTypeToAction.put(Map.class, new ActionXmlDictionaryType(this));
		dicoTypeToAction.put(Object.class, new ActionXmlObject(this));
		dicoTypeToAction.put(void.class, new ActionXmlVoid(this));
		dicoTypeToAction.put(Integer.class, new ActionXmlSimpleComportement<Integer>(this));
		dicoTypeToAction.put(Enum.class, new ActionXmlSimpleComportement<Enum>(this));
		dicoTypeToAction.put(UUID.class, new ActionXmlSimpleComportement<UUID>(this));
		dicoTypeToAction.put(String.class, new ActionXmlSimpleComportement<String>(this));
		dicoTypeToAction.put(Byte.class, new ActionXmlSimpleComportement<Byte>(this));
		dicoTypeToAction.put(Float.class, new ActionXmlSimpleComportement<Float>(this));
		dicoTypeToAction.put(Double.class, new ActionXmlSimpleComportement<Double>(this));
		dicoTypeToAction.put(Long.class, new ActionXmlSimpleComportement<Long>(this));
		dicoTypeToAction.put(Short.class, new ActionXmlSimpleComportement<Short>(this));
	};

	private void writeHeader() throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}

	void openTag(String name, Class<?> type) throws IOException {
		writer.write("<");
		writer.write(name);
		if(type != null){
			writer.write(" type=\"");
			writer.write(Constants.getSmallNameType(type));
			writer.write("\"");
		}
		writer.write(">");
	}

	void closeTag(String name) throws IOException {
		writer.write("</");
		writer.write(name);
		writer.write('>');
	}

}
