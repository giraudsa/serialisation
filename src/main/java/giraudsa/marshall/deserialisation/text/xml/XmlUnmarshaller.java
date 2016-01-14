package giraudsa.marshall.deserialisation.text.xml;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import utils.ConfigurationMarshalling;
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
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class XmlUnmarshaller<U> extends TextUnmarshaller<U>{
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlUnmarshaller.class);
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
			throw new UnmarshallExeption(e);
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

	
	@Override
	protected void initialiseActions() throws IOException {
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
			typeToUnmarshall = getTypeDepuisNom(attributes.getValue("type"));
			if(isFirst) 
				checkType(typeToUnmarshall);
		}else{
			typeToUnmarshall = getType(nomAttribut);
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
			throw new BadTypeUnmarshallException("not instanciable from " + typeToUnmarshall.getName());
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
	
	protected void characters(String donnees) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		rempliData(getActionEnCours(), donnees);
	}

	@SuppressWarnings("unchecked") 
	protected void endElement() throws InstantiationException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException, IOException, NotImplementedSerializeException, IllegalAccessException {
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
}


