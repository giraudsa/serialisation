package giraudsa.marshall.deserialisation.text.xml;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import utils.ConfigurationMarshalling;
import utils.TypeExtension;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.text.TextUnmarshaller;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlArrayType;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlAtomicIntegerArray;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlAtomicLongArray;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlBitSet;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlCalendar;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlCollectionType;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlCurrency;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlDate;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlDictionaryType;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlEnum;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlInetAddress;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlLocale;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlObject;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlSimpleComportement;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlString;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlUUID;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlVoid;
import giraudsa.marshall.exception.BadTypeUnmarshallException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
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

public class XmlUnmarshaller<U> extends TextUnmarshaller<U>{
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlUnmarshaller.class);
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
	static {
		dicoTypeToAction.put(Date.class, ActionXmlDate.getInstance());
		dicoTypeToAction.put(Collection.class, ActionXmlCollectionType.getInstance());
		dicoTypeToAction.put(Array.class, ActionXmlArrayType.getInstance());
		dicoTypeToAction.put(Map.class, ActionXmlDictionaryType.getInstance());
		dicoTypeToAction.put(Object.class, ActionXmlObject.getInstance());
		dicoTypeToAction.put(void.class, ActionXmlVoid.getInstance());
		dicoTypeToAction.put(Void.class, ActionXmlVoid.getInstance());
		dicoTypeToAction.put(UUID.class, ActionXmlUUID.getInstance());
		dicoTypeToAction.put(Enum.class, ActionXmlEnum.getInstance());
		dicoTypeToAction.put(String.class, ActionXmlString.getInstance());
		
		dicoTypeToAction.put(Boolean.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(Byte.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(Float.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(Integer.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(Double.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(Long.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(Short.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(Character.class, ActionXmlSimpleComportement.getInstance());	
		
		dicoTypeToAction.put(AtomicBoolean.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicInteger.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicLong.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicIntegerArray.class, ActionXmlAtomicIntegerArray.getInstance());
		dicoTypeToAction.put(AtomicLongArray.class, ActionXmlAtomicLongArray.getInstance());
		dicoTypeToAction.put(BigDecimal.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(BigInteger.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(URI.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(URL.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(Currency.class, ActionXmlCurrency.getInstance());
		dicoTypeToAction.put(Locale.class, ActionXmlLocale.getInstance());
		dicoTypeToAction.put(InetAddress.class, ActionXmlInetAddress.getInstance());
		dicoTypeToAction.put(BitSet.class, ActionXmlBitSet.getInstance());
		dicoTypeToAction.put(Calendar.class, ActionXmlCalendar.getInstance());
		dicoTypeToAction.put(StringBuilder.class, ActionXmlSimpleComportement.getInstance());
		dicoTypeToAction.put(StringBuffer.class, ActionXmlSimpleComportement.getInstance());
	}

	/////ATTRIBUTS
	private boolean isFirst = true;
    /////CONSTRUCTEUR
	private XmlUnmarshaller(Reader reader, EntityManager entity) throws ClassNotFoundException, IOException {
		super(reader, entity, ConfigurationMarshalling.getDateFormatXml());
	}
	//////METHODES STATICS PUBLICS
	public static <U> U fromXml(Reader reader, EntityManager entity) throws UnmarshallExeption{
		XmlUnmarshaller<U> w;
		try {
			w = new XmlUnmarshaller<>(reader, entity);
			return w.parse();
		} catch (ClassNotFoundException | IOException | SAXException e) {
			LOGGER.error("Impossible de désérialiser", e);
			throw new UnmarshallExeption("Impossible de désérialiser", e);
		}
		
	}
	public static <U> U fromXml(Reader reader) throws UnmarshallExeption{
		return fromXml(reader, null);
	}
	public static <U> U fromXml(String stringToUnmarshall) throws UnmarshallExeption{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0)
			return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromXml(sr, null);
		}
	}
	public static  <U> U fromXml(String stringToUnmarshall, EntityManager entity) throws UnmarshallExeption {
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) 
			return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromXml(sr, entity);
		}
	}

	
	//////METHODES PRIVEES
	private U parse() throws IOException, SAXException {
		XmlUnmarshallerHandler handler =  new XmlUnmarshallerHandler(this);
		XMLReader parser = XMLReaderFactory.createXMLReader();
		parser.setContentHandler(handler);
		InputSource source = new InputSource(reader);
		source.setEncoding("UTF-8");
		parser.parse(source);
		return obj;
	}

	private Class<?> getType(Attributes attributes, String nomAttribut) throws ClassNotFoundException, BadTypeUnmarshallException {
		Class<?> typeToUnmarshall;
		String typeEcrit = attributes.getValue("type");
		if(typeEcrit != null){
			typeToUnmarshall = getTypeDepuisNom(attributes.getValue("type"));
			if(isFirst) 
				checkType(typeToUnmarshall);
		}else{
			typeToUnmarshall = getType(nomAttribut);
			typeToUnmarshall = TypeExtension.getTypeEnveloppe(typeToUnmarshall);
		}
		return typeToUnmarshall;
	}

	@SuppressWarnings("unchecked")
	private <T> void checkType(Class<T> typeToUnmarshall) throws BadTypeUnmarshallException {
		try {
			U test = (U)typeToUnmarshall.newInstance();
			test.getClass();
		} catch (Exception e) {
			LOGGER.error("le type attendu n'est pas celui du XML ou n'est pas instanciable", e);
			throw new BadTypeUnmarshallException("not instanciable from " + typeToUnmarshall.getName(), e);
		}
	}

	/////XML EVENT
	protected void startElement(String qName, Attributes attributes) throws ClassNotFoundException, BadTypeUnmarshallException, InstantiationException, IllegalAccessException, NotImplementedSerializeException {
		setCache(attributes);
		Class<?> type = getType(attributes, qName);
		isFirst = false;
		if(type != null){
			ActionXml<?> action = (ActionXml<?>) getAction(type);
			setNom(action, qName);
			setFieldInformation(action);
			pileAction.push(action);
		}
	}

	private void setCache(Attributes attributes) {
		if(isFirst){
			String typeId = attributes.getValue("typeId");
			boolean isIdUniversal = typeId != null ? true : false;
			setCache(isIdUniversal);
		}
	}
	
	protected void characters(String donnees) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException, UnmarshallExeption {
		rempliData(getActionEnCours(), donnees);
	}

	@SuppressWarnings("unchecked") 
	protected void endElement() throws InstantiationException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IOException, NotImplementedSerializeException, IllegalAccessException, UnmarshallExeption {
		construitObjet(getActionEnCours());
		ActionXml<?> actionATraiter = (ActionXml<?>) pileAction.pop();
		if(pileAction.isEmpty()){
			obj = obj == null ? (U) getObjet(actionATraiter) : obj;
		}else{
			String nom = getNom(actionATraiter);
			Object objet = getObjet(actionATraiter);
			integreObjet(getActionEnCours(), nom, objet);
		}
	}
	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction() {
		return dicoTypeToAction;
	}
}


