package giraudsa.marshall.deserialisation.text.xml;



import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import utils.Constants;
import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.text.TextUnmarshaller;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlCollectionType;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlDate;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlDictionaryType;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlEnum;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlObject;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlSimpleComportement;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlString;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlUUID;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlVoid;
import giraudsa.marshall.exception.BadTypeUnmarshallException;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class XmlUnmarshaller<U> extends TextUnmarshaller<U>{
	//////METHODES STATICS PUBLICS
	public static <U> U fromXml(Reader reader, EntityManager entity) throws IOException, SAXException, ClassNotFoundException{
		XmlUnmarshaller<U> w = new XmlUnmarshaller<U>(reader, entity, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")){};
		return w.parse();
	}
	public static <U> U fromXml(Reader reader) throws IOException, SAXException, ClassNotFoundException{
		return fromXml(reader, null, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
	}
	public static <U> U fromXml(String stringToUnmarshall)  throws IOException, SAXException, ClassNotFoundException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromXml(sr, null, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		}
	}
	public static  <U> U fromXml(String stringToUnmarshall, EntityManager entity)  throws IOException, SAXException, ClassNotFoundException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromXml(sr, entity);
		}
	}
	public static <U> U fromXml(Reader reader, EntityManager entity, DateFormat df) throws IOException, SAXException, ClassNotFoundException{
		XmlUnmarshaller<U> w = new XmlUnmarshaller<U>(reader, entity, df){};
		return w.parse();
	}
	public static <U> U fromXml(Reader reader, DateFormat df) throws IOException, SAXException, ClassNotFoundException{
		return fromXml(reader, null, df);
	}
	public static <U> U fromXml(String stringToUnmarshall, DateFormat df)  throws IOException, SAXException, ClassNotFoundException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromXml(sr, null, df);
		}
	}
	public static  <U> U fromXml(String stringToUnmarshall, EntityManager entity, DateFormat df)  throws IOException, SAXException, ClassNotFoundException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromXml(sr, entity, df);
		}
	}
	
	/////ATTRIBUTS
	private boolean isFirst = true;

	/////CONSTRUCTEUR
	private XmlUnmarshaller(Reader reader, EntityManager entity, DateFormat df) throws ClassNotFoundException {
		super(reader, entity, df);
		actions.put(Date.class, ActionXmlDate.getInstance(this));
		actions.put(Collection.class, ActionXmlCollectionType.getInstance(this));
		actions.put(Map.class, ActionXmlDictionaryType.getInstance(this));
		actions.put(Object.class, ActionXmlObject.getInstance(this));
		actions.put(Void.class, ActionXmlVoid.getInstance(this));
		actions.put(UUID.class, ActionXmlUUID.getInstance(this));
		actions.put(Enum.class, ActionXmlEnum.getInstance(this));
		actions.put(String.class, ActionXmlString.getInstance(this));
		
		actions.put(Boolean.class, ActionXmlSimpleComportement.getInstance(Boolean.class,this));
		actions.put(Byte.class, ActionXmlSimpleComportement.getInstance(Byte.class,this));
		actions.put(Float.class, ActionXmlSimpleComportement.getInstance(Float.class,this));
		actions.put(Integer.class, ActionXmlSimpleComportement.getInstance(Integer.class,this));
		actions.put(Double.class, ActionXmlSimpleComportement.getInstance(Double.class,this));
		actions.put(Long.class, ActionXmlSimpleComportement.getInstance(Long.class,this));
		actions.put(Short.class, ActionXmlSimpleComportement.getInstance(Short.class,this));
		actions.put(Character.class, ActionXmlSimpleComportement.getInstance(Character.class,this));
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
			typeToUnmarshall =  Class.forName(Constants.getNameType(attributes.getValue("type")));
			if(isFirst) checkType(typeToUnmarshall);
		}else{
			typeToUnmarshall = getType((ActionXml<?>)getActionEnCours(), nomAttribut);
		}
		return typeToUnmarshall;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private <T> void checkType(Class<T> typeToUnmarshall) throws BadTypeUnmarshallException {
		isFirst = false;
		try {
			U test = (U)typeToUnmarshall.newInstance();
		} catch (Exception e) {
			throw new BadTypeUnmarshallException("not instanciable from " + typeToUnmarshall.getName());
		}
	}

	/////XML EVENT
	void startDocument() {}

	void startElement(String uri, String localName, String qName,
			Attributes attributes) throws Exception {
		Class<?> type = getType(attributes, qName);
		if(type != null){
			ActionXml<?> action = (ActionXml<?>) getAction(type);
			setNom(action, qName);
			pileAction.push(action);
		}
	}

	void characters(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParseException {
		rempliData(getActionEnCours(), donnees);
	}

	@SuppressWarnings("unchecked") void endElement(String uri, String localName, String qName) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, SecurityException, IOException, NotImplementedSerializeException {
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

	void endDocument() {}
}


