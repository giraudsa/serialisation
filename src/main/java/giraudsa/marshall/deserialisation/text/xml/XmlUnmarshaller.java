package giraudsa.marshall.deserialisation.text.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
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
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.ConfigurationMarshalling;
import utils.EntityManager;

public class XmlUnmarshaller<U> extends TextUnmarshaller<U> {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections
			.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());

	private static final String FEATURE_DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlUnmarshaller.class);
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

	public static <U> U fromXml(final Reader reader) throws UnmarshallExeption {
		return fromXml(reader, null);
	}

	////// METHODES STATICS PUBLICS
	public static <U> U fromXml(final Reader reader, final EntityManager entity) throws UnmarshallExeption {
		XmlUnmarshaller<U> w;
		try {
			w = new XmlUnmarshaller<>(reader, entity);
			return w.parse();
		} catch (IOException | SAXException | FabriqueInstantiationException e) {
			LOGGER.error("Impossible de désérialiser", e);
			throw new UnmarshallExeption("Impossible de désérialiser", e);
		}

	}

	public static <U> U fromXml(final String stringToUnmarshall) throws UnmarshallExeption {
		if (stringToUnmarshall == null || stringToUnmarshall.length() == 0)
			return null;
		try (StringReader sr = new StringReader(stringToUnmarshall)) {
			return fromXml(sr, null);
		}
	}

	public static <U> U fromXml(final String stringToUnmarshall, final EntityManager entity) throws UnmarshallExeption {
		if (stringToUnmarshall == null || stringToUnmarshall.length() == 0)
			return null;
		try (StringReader sr = new StringReader(stringToUnmarshall)) {
			return fromXml(sr, entity);
		}
	}

	///// ATTRIBUTS
	private boolean isFirst = true;

	///// CONSTRUCTEUR
	private XmlUnmarshaller(final Reader reader, final EntityManager entity) throws FabriqueInstantiationException {
		super(reader, entity, ConfigurationMarshalling.getDateFormatXml());
	}

	protected void characters(final String donnees) throws InstanciationException {
		rempliData(getActionEnCours(), donnees);
	}

	@SuppressWarnings("unchecked")
	private <T> void checkType(final Class<T> typeToUnmarshall) throws ClassNotFoundException {
		try {
			final U test = (U) createInstance(typeToUnmarshall);
			test.getClass();
		} catch (final Exception e) {
			LOGGER.error("le type attendu n'est pas celui du XML ou n'est pas instanciable", e);
			throw new ClassNotFoundException("not instanciable from " + typeToUnmarshall.getName(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void endElement() throws EntityManagerImplementationException, InstanciationException,
			IllegalAccessException, SetValueException {
		construitObjet(getActionEnCours());
		final ActionXml<?> actionATraiter = (ActionXml<?>) pileAction.pop();
		if (pileAction.isEmpty())
			obj = obj == null ? (U) getObjet(actionATraiter) : obj;
		else {
			final String nom = getNom(actionATraiter);
			final Object objet = getObjet(actionATraiter);
			integreObjet(getActionEnCours(), nom, objet);
		}
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction() {
		return dicoTypeToAction;
	}

	private Class<?> getType(final Attributes attributes, final String nomAttribut) throws ClassNotFoundException {
		Class<?> typeToUnmarshall;
		final String typeEcrit = attributes.getValue("type");
		if (typeEcrit != null) {
			typeToUnmarshall = getTypeDepuisNom(attributes.getValue("type"));
			if (isFirst)
				checkType(typeToUnmarshall);
		} else
			typeToUnmarshall = getType(nomAttribut);
		return typeToUnmarshall;
	}

	////// METHODES PRIVEES
	private U parse() throws IOException, SAXException, UnmarshallExeption {
		final XmlUnmarshallerHandler handler = new XmlUnmarshallerHandler(this);
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			factory.setFeature(FEATURE_DISALLOW_DOCTYPE, true);
			final SAXParser parser = factory.newSAXParser();
			final InputSource source = new InputSource(reader);
			source.setEncoding("UTF-8");
			parser.parse(source, handler);
			return obj;
		} catch (final ParserConfigurationException e) {
			throw new UnmarshallExeption("Impossible de creer le parseur", e);
		}
	}

	private void setCache(final Attributes attributes) {
		if (isFirst) {
			final String typeId = attributes.getValue("typeId");
			final boolean isIdUniversal = typeId != null ? true : false;
			setCache(isIdUniversal);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void setId(final Attributes attributes, final ActionXml<?> action)
			throws InstanciationException, EntityManagerImplementationException {
		if (!(action instanceof ActionXmlObject<?>))
			return;
		final String id = attributes.getValue("id");
		if (id != null) {
			final ActionXmlObject<T> actionObject = (ActionXmlObject<T>) action;
			actionObject.setId(id);
		}
	}

	///// XML EVENT
	protected void startElement(final String qName, final Attributes attributes) throws ClassNotFoundException,
			NotImplementedSerializeException, InstanciationException, EntityManagerImplementationException {
		setCache(attributes);
		final Class<?> type = getType(attributes, qName);
		isFirst = false;
		if (type != null) {
			final ActionXml<?> action = (ActionXml<?>) getAction(type);
			setNom(action, qName);
			setFieldInformation(action);
			setId(attributes, action);
			pileAction.push(action);
		}
	}
}
