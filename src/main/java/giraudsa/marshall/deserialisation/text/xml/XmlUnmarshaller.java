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
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlObject;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlSimpleComportement;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlString;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlUUID;
import giraudsa.marshall.deserialisation.text.xml.actions.ActionXmlVoid;
import giraudsa.marshall.exception.BadTypeUnmarshallException;

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
	private Stack<ActionXml<?>> pileAction = new Stack<ActionXml<?>>();
	private ActionXml<?> getActionEnCours(){
		return pileAction.peek();
	}

	/////CONSTRUCTEUR
	private XmlUnmarshaller(Reader reader, EntityManager entity, DateFormat df) throws ClassNotFoundException {
		super(reader, entity, df);
		typesAction.put(Date.class, ActionXmlDate.class);
		typesAction.put(Collection.class, ActionXmlCollectionType.class);
		typesAction.put(Map.class, ActionXmlDictionaryType.class);
		typesAction.put(Object.class, ActionXmlObject.class);
		typesAction.put(void.class, ActionXmlVoid.class);
		typesAction.put(Boolean.class, ActionXmlSimpleComportement.class);
		typesAction.put(Enum.class, ActionXmlSimpleComportement.class);
		typesAction.put(UUID.class, ActionXmlUUID.class);
		typesAction.put(String.class, ActionXmlString.class);
		typesAction.put(Byte.class, ActionXmlSimpleComportement.class);
		typesAction.put(Float.class, ActionXmlSimpleComportement.class);
		typesAction.put(Integer.class, ActionXmlSimpleComportement.class);
		typesAction.put(Double.class, ActionXmlSimpleComportement.class);
		typesAction.put(Long.class, ActionXmlSimpleComportement.class);
		typesAction.put(Short.class, ActionXmlSimpleComportement.class);
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

	private Class<?> getType(Attributes attributes) throws ClassNotFoundException, BadTypeUnmarshallException {
		Class<?> typeToUnmarshall =  Class.forName(Constants.getNameType(attributes.getValue("type")));
		if(isFirst) checkType(typeToUnmarshall);
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

	@SuppressWarnings({ "unchecked", "rawtypes" }) void startElement(String uri, String localName, String qName,
			Attributes attributes) throws Exception {
		Class<?> type = getType(attributes);
		if(type != null){
			Class<? extends ActionXml> behavior = (Class<? extends ActionXml>) getTypeAction(type);
			ActionXml<?> action = behavior.getConstructor(Class.class, String.class, XmlUnmarshaller.class).newInstance(type, qName, this);
			pileAction.push(action);
		}
	}

	void characters(String donnees) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ParseException {
		rempliData(getActionEnCours(), donnees);
	}

	@SuppressWarnings("unchecked") void endElement(String uri, String localName, String qName) throws IllegalArgumentException, IllegalAccessException, InstantiationException, InvocationTargetException {
		construitObjet(getActionEnCours());
		ActionXml<?> actionATraiter = pileAction.pop();
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


