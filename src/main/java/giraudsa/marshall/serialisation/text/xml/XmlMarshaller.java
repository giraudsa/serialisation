package giraudsa.marshall.serialisation.text.xml;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.Marshaller;
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
import java.lang.reflect.InvocationTargetException;
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
import utils.Pair;

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
	
	private XsdManager xsdManager = null;
	
	//info id universal
	private boolean isWrittenUniversal = false;
	
	private boolean	isWrittenW3XMLSchema = false;

	private int     compteur;
	private Map<String,Pair<String, Integer>> dicoPackagenameToALiasAndLevel = new HashMap<>();


	//////CONSTRUCTEUR
	private XmlMarshaller(Writer output, boolean isCompleteSerialisation) throws IOException {
		super(output, isCompleteSerialisation, ConfigurationMarshalling.getDateFormatXml());
		writeHeader();
	}
	private XmlMarshaller(Writer output, boolean isCompleteSerialisation,XsdManager xsdManager) throws IOException {
		super(output, isCompleteSerialisation, ConfigurationMarshalling.getDateFormatXml());
		writeHeader();
		this.xsdManager=xsdManager;
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
	
	public static <U> void toXml(U obj, Writer output, XsdManager xsdManager) throws MarshallExeption {
		try {
			XmlMarshaller v = new XmlMarshaller(output, false,xsdManager);
			v.marshall(obj);
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("impossible de sérialiser " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
		
	}
	public static <U> String toXml(U obj,XsdManager xsdManager) throws MarshallExeption{
		try(StringWriter sw = new StringWriter()){
			toXml(obj, sw,xsdManager);
			return sw.toString();
		} catch (IOException e) {
			LOGGER.error("impossible de sérialiser en String " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}
	public static <U> void toCompleteXml(U obj, Writer output,XsdManager xsdManager) throws MarshallExeption{
		try {
			XmlMarshaller v = new XmlMarshaller(output, true,xsdManager);
			v.marshall(obj);
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("impossible de sérialiser completement " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}
	public static <U> String toCompleteXml(U obj,XsdManager xsdManager) throws MarshallExeption{
		try(StringWriter sw = new StringWriter()){
			toCompleteXml(obj, sw,xsdManager);
			return sw.toString();
		} catch (IOException e) {
			LOGGER.error("impossible de sérialiser completement en String " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}
	private void writeHeader() throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}


	public void writeW3XMLSchema(Class<?> type) throws IOException {
		if(isWrittenW3XMLSchema)
			return;
		isWrittenW3XMLSchema = true;
		write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		write(" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"");
		if (xsdManager!=null){
			write(" xsi:noNamespaceSchemaLocation=\"");
			write(xsdManager.getUriOfXsd(type).toString());
			write("\"");
		}
	}
	public void writeInfoUniversal() throws IOException {
		if(isWrittenUniversal)
			return;
		isWrittenUniversal = true;
		boolean isUniversal = ConfigurationMarshalling.getEstIdUniversel();
		if(isUniversal){
			writer.write(" typeId=\"");
			writer.write("universal");
			writer.write("\"");
		}
	}
	protected void closeTag(String name) throws IOException {
		--niveau;
		if (isPrettyPrint()){
			prettyPrintCloseTag();
		}
		writer.write("</");
		writer.write(name);
		writer.write('>');
	}
	
	
	private void writeTypeSimple( Class<?> type) throws IOException{
		write(" type=\"");
		write( Constants.getSmallNameType(type));
		write( "\"");
	}
	
	public void writeTypeGeneral(Class<?> type) throws IOException { 
		if (Constants.getSmallNameType(type).equals(type.getName())){
			String[] nomClasseEtPackageEtAlias = new String[3];
			traiteNomClasseEtPackage(nomClasseEtPackageEtAlias,type);
			traiteAlias(nomClasseEtPackageEtAlias);
			ecrireType(nomClasseEtPackageEtAlias);
		}
		else {
			writeTypeSimple(type);
		}
	}
	
	
	private void ecrireType(String[] nomClasseEtPackageEtAlias) throws IOException {
		write(" xsi:type=\""+nomClasseEtPackageEtAlias[2]+":"+nomClasseEtPackageEtAlias[0]+"\"");
		if(!dicoPackagenameToALiasAndLevel.containsKey(nomClasseEtPackageEtAlias[1])){
			dicoPackagenameToALiasAndLevel.put(nomClasseEtPackageEtAlias[1], new Pair<String,Integer>(nomClasseEtPackageEtAlias[2],new Integer(niveau)));
			write(" xmlns:" +nomClasseEtPackageEtAlias[2]+"=\""+nomClasseEtPackageEtAlias[1]+"\"")	;
		}
	}
	
	private void traiteAlias(String[] nomClasseEtPackageEtAlias) {
		if (dicoPackagenameToALiasAndLevel.get(nomClasseEtPackageEtAlias[1])!=null){
			nomClasseEtPackageEtAlias[2] = dicoPackagenameToALiasAndLevel.get(nomClasseEtPackageEtAlias[1]).getKey();
			int portee = dicoPackagenameToALiasAndLevel.get(nomClasseEtPackageEtAlias[1]).getValue().intValue();
			if (niveau<=portee){
				dicoPackagenameToALiasAndLevel.remove(nomClasseEtPackageEtAlias[1]);	
			}
		}
		else{
			compteur++;
			nomClasseEtPackageEtAlias[2] ="ns";
			nomClasseEtPackageEtAlias[2] =  nomClasseEtPackageEtAlias[2].concat(Integer.toString(compteur));
		}
	}
	
	private void traiteNomClasseEtPackage(String[] nomClasseEtPackageEtAlias, Class<?> type) {
		nomClasseEtPackageEtAlias[0] = (Constants.getSmallNameType(type));
		nomClasseEtPackageEtAlias[1]= type.getPackage().getName();
		nomClasseEtPackageEtAlias[1]=nomClasseEtPackageEtAlias[1].replace(".", "/");
		nomClasseEtPackageEtAlias[0]=nomClasseEtPackageEtAlias[0].replace(".", "/");
		nomClasseEtPackageEtAlias[0]=nomClasseEtPackageEtAlias[0].replace(nomClasseEtPackageEtAlias[1],"");
		nomClasseEtPackageEtAlias[0]=nomClasseEtPackageEtAlias[0].replace("/","");
		String firstletter=nomClasseEtPackageEtAlias[0].substring(0, 1);
		firstletter=firstletter.toLowerCase();
		String otherletter = nomClasseEtPackageEtAlias[0].substring(1);
		nomClasseEtPackageEtAlias[0]=firstletter.concat(otherletter);
	}
	
	public void prettyPrintOpenTag() throws IOException {
		if (!isPrettyPrint())
			return;
		writer.write(System.lineSeparator());
		for(int j = 0; j < niveau ; j++){
			writer.write("   ");
		}
		
		lastIsOpen = true;
	}
	
	protected void prettyPrintCloseTag() throws IOException {
		if(!lastIsOpen){
			writer.write(System.lineSeparator());
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
	
	void openTag(ActionAbstrait<?> actionEncours, Marshaller marshaller, String nomBalise, Class<?> type) throws IOException {
		++niveau;
		prettyPrintOpenTag();
		write("<");
		write(nomBalise);
		if(type != null){
			((ActionXml<?>)actionEncours).writeType(marshaller,type);
		}
		writeW3XMLSchema(type);
		writeInfoUniversal();

		write(">");
	}

}
