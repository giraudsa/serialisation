package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.text.TextMarshaller;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlArrayType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlAtomicArrayIntegerType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlAtomicArrayLongType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlBitSet;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlCalendar;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlCollectionType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlCurrency;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlDate;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlDictionaryType;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlInetAdress;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlObject;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlSimpleComportement;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlUri;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlUrl;
import giraudsa.marshall.serialisation.text.xml.actions.ActionXmlVoid;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.ConfigurationMarshalling;
import utils.Constants;

public class XmlMarshaller extends TextMarshaller {
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlMarshaller.class);
	
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
	static {
		dicoTypeToAction.put(Date.class, new ActionXmlDate());
		dicoTypeToAction.put(Boolean.class, new ActionXmlSimpleComportement<Boolean>());
		dicoTypeToAction.put(Collection.class, new ActionXmlCollectionType());
		dicoTypeToAction.put(Array.class, new ActionXmlArrayType());
		dicoTypeToAction.put(Map.class, new ActionXmlDictionaryType());
		dicoTypeToAction.put(Object.class, new ActionXmlObject());
		dicoTypeToAction.put(void.class, new ActionXmlVoid());
		dicoTypeToAction.put(Integer.class, new ActionXmlSimpleComportement<Integer>());
		dicoTypeToAction.put(Enum.class, new ActionXmlSimpleComportement<Enum>());
		dicoTypeToAction.put(UUID.class, new ActionXmlSimpleComportement<UUID>());
		dicoTypeToAction.put(String.class, new ActionXmlSimpleComportement<String>());
		dicoTypeToAction.put(Byte.class, new ActionXmlSimpleComportement<Byte>());
		dicoTypeToAction.put(Float.class, new ActionXmlSimpleComportement<Float>());
		dicoTypeToAction.put(Double.class, new ActionXmlSimpleComportement<Double>());
		dicoTypeToAction.put(Long.class, new ActionXmlSimpleComportement<Long>());
		dicoTypeToAction.put(Short.class, new ActionXmlSimpleComportement<Short>());
		dicoTypeToAction.put(AtomicBoolean.class, new ActionXmlSimpleComportement<AtomicBoolean>());
		dicoTypeToAction.put(AtomicInteger.class, new ActionXmlSimpleComportement<AtomicInteger>());
		dicoTypeToAction.put(AtomicLong.class, new ActionXmlSimpleComportement<AtomicLong>());
		dicoTypeToAction.put(AtomicIntegerArray.class, new ActionXmlAtomicArrayIntegerType());
		dicoTypeToAction.put(AtomicLongArray.class, new ActionXmlAtomicArrayLongType());
		dicoTypeToAction.put(BigDecimal.class, new ActionXmlSimpleComportement<BigDecimal>());
		dicoTypeToAction.put(BigInteger.class, new ActionXmlSimpleComportement<BigInteger>());
		dicoTypeToAction.put(URI.class, new ActionXmlUri());
		dicoTypeToAction.put(URL.class, new ActionXmlUrl());
		dicoTypeToAction.put(Currency.class, new ActionXmlCurrency());
		dicoTypeToAction.put(Locale.class, new ActionXmlSimpleComportement<Locale>());
		dicoTypeToAction.put(InetAddress.class, new ActionXmlInetAdress());
		dicoTypeToAction.put(BitSet.class, new ActionXmlBitSet());
		dicoTypeToAction.put(Calendar.class, new ActionXmlCalendar());
		dicoTypeToAction.put(StringBuilder.class, new ActionXmlSimpleComportement<StringBuilder>());
		dicoTypeToAction.put(StringBuffer.class, new ActionXmlSimpleComportement<StringBuffer>());
	}

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
		} catch (Exception e) {
			LOGGER.error("impossible de sérialiser " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
		
	}
	public static <U> String toXml(U obj) throws MarshallExeption{
		StringWriter sw = new StringWriter();
		toXml(obj, sw);
		String ret = sw.toString();
		try {
			sw.close();
		} catch (IOException e) {
			LOGGER.error("impossible de fermer le stringwriter lors de la sérialisation en xml de " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
		return ret;
	}
	public static <U> void toCompleteXml(U obj, Writer output) throws MarshallExeption{
		try {
			XmlMarshaller v = new XmlMarshaller(output, true);
			v.marshall(obj);
		} catch (Exception e) {
			LOGGER.error("impossible de sérialiser completement " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}
	public static <U> String toCompleteXml(U obj) throws MarshallExeption{
		StringWriter sw = new StringWriter();
		toCompleteXml(obj, sw);
		String ret = sw.toString();
		try{
			sw.close();
		} catch (IOException e) {
			LOGGER.error("impossible dde fermer le stringwriter lors de la sérialisation complete en xml de " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
		return ret;
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
	
	protected void prettyPrintOpenTag() throws IOException {
		writer.write("\n");
		for(int j = 0; j < niveau ; j++){
			writer.write("   ");
		}
		++niveau;
		lastIsOpen = true;
	}
	
	protected void prettyPrintCloseTag() throws IOException {
		--niveau;
		if(!lastIsOpen){
			writer.write("\n");
			for(int j = 0; j < niveau ; j++){
				writer.write("   ");
			}
		}
		lastIsOpen = false;
	}
	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getDicoTypeToAction() {
		return dicoTypeToAction;
	}

}
