package com.actemium.marshall.deserialisation.xml;



import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import utils.Constants;

import com.actemium.marshall.deserialisation.EntityManager;
import com.actemium.marshall.deserialisation.Unmarshaller;
import com.actemium.marshall.deserialisation.xml.actions.ActionXmlCollectionType;
import com.actemium.marshall.deserialisation.xml.actions.ActionXmlDate;
import com.actemium.marshall.deserialisation.xml.actions.ActionXmlDictionaryType;
import com.actemium.marshall.deserialisation.xml.actions.ActionXmlObject;
import com.actemium.marshall.deserialisation.xml.actions.ActionXmlSimpleComportement;
import com.actemium.marshall.deserialisation.xml.actions.ActionXmlString;
import com.actemium.marshall.deserialisation.xml.actions.ActionXmlUUID;
import com.actemium.marshall.deserialisation.xml.actions.ActionXmlVoid;
import com.actemium.marshall.exception.BadTypeUnmarshallException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

public class XmlUnmarshaller<U> extends Unmarshaller<U>{
	//////METHODES STATICS PUBLICS
	public static <U> U fromXml(StringReader reader, EntityManager entity) throws IOException, SAXException, ClassNotFoundException{
		XmlUnmarshaller<U> w = new XmlUnmarshaller<U>(reader, entity){};
		return w.parse();
	}
	public static <U> U fromXml(StringReader reader) throws IOException, SAXException, ClassNotFoundException{
		return fromXml(reader, null);
	}
	public static <U> U fromXml(String stringToUnmarshall)  throws IOException, SAXException, ClassNotFoundException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromXml(sr, null);
		}
	}
	public static  <U> U fromXml(String stringToUnmarshall, EntityManager entity)  throws IOException, SAXException, ClassNotFoundException{
		if(stringToUnmarshall == null || stringToUnmarshall.length() == 0) return null;
		try(StringReader sr = new StringReader(stringToUnmarshall)){
			return fromXml(sr, entity);
		}
	}
	
	
	/////ATTRIBUTS
	private boolean isFirst = true;
	private Stack<ActionXml<?>> pileAction = new Stack<ActionXml<?>>();
	private ActionXml<?> getActionEnCours(){
		return pileAction.peek();
	}

	/////CONSTRUCTEUR
	private XmlUnmarshaller(Reader reader, EntityManager entity) throws ClassNotFoundException {
		super(reader, entity);
		behaviors.put(Date.class, ActionXmlDate.class);
		behaviors.put(Collection.class, ActionXmlCollectionType.class);
		behaviors.put(Map.class, ActionXmlDictionaryType.class);
		behaviors.put(Object.class, ActionXmlObject.class);
		behaviors.put(void.class, ActionXmlVoid.class);
		behaviors.put(Boolean.class, ActionXmlSimpleComportement.class);
		behaviors.put(Enum.class, ActionXmlSimpleComportement.class);
		behaviors.put(UUID.class, ActionXmlUUID.class);
		behaviors.put(String.class, ActionXmlString.class);
		behaviors.put(Byte.class, ActionXmlSimpleComportement.class);
		behaviors.put(Float.class, ActionXmlSimpleComportement.class);
		behaviors.put(Integer.class, ActionXmlSimpleComportement.class);
		behaviors.put(Double.class, ActionXmlSimpleComportement.class);
		behaviors.put(Long.class, ActionXmlSimpleComportement.class);
		behaviors.put(Short.class, ActionXmlSimpleComportement.class);
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
			Class<? extends ActionXml> behavior = (Class<? extends ActionXml>) getBehavior(type);
			ActionXml<?> action = behavior.getConstructor(Class.class, String.class).newInstance(type, qName);
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


