package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.exception.MarshallExeption;
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
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.ConfigurationMarshalling;
import utils.Constants;

public class XmlMarshaller extends TextMarshaller {
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlMarshaller.class);
	//prettyPrint 
	private int niveau = 0;
	private boolean lastIsOpen = false;
	
	//info id universal
	private boolean isWrittenUniversal = false;
	
	//////CONSTRUCTEUR
	private XmlMarshaller(Writer output, boolean isCompleteSerialisation) throws IOException {
		super(output, isCompleteSerialisation, ConfigurationMarshalling.getDateFormatXml());
		writeHeader();
	}
	/////METHODES STATICS PUBLICS
	public static <U> void toXml(U obj, Writer output) throws MarshallExeption {
		try {
			XmlMarshaller v = new XmlMarshaller(output, false);
			v.marshall(obj);
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("impossible de sérialiser " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
		
	}
	public static <U> String toXml(U obj) throws MarshallExeption{
		try(StringWriter sw = new StringWriter()){
			toXml(obj, sw);
			return sw.toString();
		} catch (IOException e) {
			LOGGER.error("impossible de sérialiser en String " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}
	public static <U> void toCompleteXml(U obj, Writer output) throws MarshallExeption{
		try {
			XmlMarshaller v = new XmlMarshaller(output, true);
			v.marshall(obj);
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("impossible de sérialiser completement " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}
	public static <U> String toCompleteXml(U obj) throws MarshallExeption{
		try(StringWriter sw = new StringWriter()){
			toCompleteXml(obj, sw);
			return sw.toString();
		} catch (IOException e) {
			LOGGER.error("impossible de sérialiser completement en String " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
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
	}

	private void writeHeader() throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}

	protected void openTag(String name, Class<?> type) throws IOException {
		if(isPrettyPrint()){
			prettyPrintOpenTag();
		}
		writer.write("<");
		writer.write(name);
		if(type != null){
			writeType(type);
		}
		if(!isWrittenUniversal){
			writeInfoUniversal();
		}
		writer.write(">");
	}
	private void writeInfoUniversal() throws IOException {
		isWrittenUniversal = true;
		boolean isUniversal = ConfigurationMarshalling.getEstIdUniversel();
		if(isUniversal){
			writer.write(" typeId=\"");
			writer.write("universal");
			writer.write("\"");
		}
	}
	protected void closeTag(String name) throws IOException {
		if(isPrettyPrint()){
			prettyPrintCloseTag();
		}
		writer.write("</");
		writer.write(name);
		writer.write('>');
	}
	private void writeType(Class<?> type) throws IOException {
		writer.write(" type=\"");
		writer.write(Constants.getSmallNameType(type));
		writer.write("\"");
	}
	private void prettyPrintOpenTag() throws IOException {
		writer.write(System.lineSeparator());
		for(int j = 0; j < niveau ; j++){
			writer.write("   ");
		}
		++niveau;
		lastIsOpen = true;
	}
	private void prettyPrintCloseTag() throws IOException {
		--niveau;
		if(!lastIsOpen){
			writer.write(System.lineSeparator());
			for(int j = 0; j < niveau ; j++){
				writer.write("   ");
			}
		}
		lastIsOpen = false;
	}

}
