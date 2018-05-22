package giraudsa.marshall.serialisation.text.xml;

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

import giraudsa.marshall.exception.ChampNotFound;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
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
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import giraudsa.marshall.strategie.StrategieParComposition;
import giraudsa.marshall.strategie.StrategieSerialisationComplete;
import utils.ConfigurationMarshalling;
import utils.Constants;
import utils.EntityManager;

public class XmlMarshaller extends TextMarshaller {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections
			.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());

	private static final Logger LOGGER = LoggerFactory.getLogger(XmlMarshaller.class);
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

	public static <U> String toCompleteXml(final U obj) throws MarshallExeption {
		try (StringWriter sw = new StringWriter()) {
			toCompleteXml(obj, sw, null);
			return sw.toString();
		} catch (final IOException e) {
			LOGGER.error("impossible de sérialiser completement en String " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}

	public static <U> void toCompleteXml(final U obj, final Writer output, final EntityManager entityManager)
			throws MarshallExeption {
		try {
			final XmlMarshaller v = new XmlMarshaller(output, new StrategieSerialisationComplete(), entityManager);
			v.marshall(obj);
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | NotImplementedSerializeException | ChampNotFound e) {
			LOGGER.error("impossible de sérialiser completement " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}

	public static <U> String toXml(final U obj) throws MarshallExeption {
		return toXml(obj, new StrategieParComposition(), null);
	}

	public static <U> String toXml(final U obj, final EntityManager entityManager) throws MarshallExeption {
		return toXml(obj, new StrategieParComposition(), entityManager);
	}

	public static <U> String toXml(final U obj, final StrategieDeSerialisation strategie,
			final EntityManager entityManager) throws MarshallExeption {
		try (StringWriter sw = new StringWriter()) {
			toXml(obj, sw, strategie, entityManager);
			return sw.toString();
		} catch (final IOException e) {
			LOGGER.error("impossible de sérialiser en String " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}

	///// METHODES STATICS PUBLICS
	public static <U> void toXml(final U obj, final Writer output, final EntityManager entityManager)
			throws MarshallExeption {
		toXml(obj, output, new StrategieParComposition(), entityManager);
	}

	public static <U> void toXml(final U obj, final Writer output, final StrategieDeSerialisation strategie,
			final EntityManager entityManager) throws MarshallExeption {
		try {
			final XmlMarshaller v = new XmlMarshaller(output, strategie, entityManager);
			v.marshall(obj);
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error("impossible de sérialiser " + obj.toString(), e);
			throw new MarshallExeption(e);
		}

	}

	// info id universal
	private boolean isWrittenUniversal = false;

	////// CONSTRUCTEUR
	private XmlMarshaller(final Writer output, final StrategieDeSerialisation strategie,
			final EntityManager entityManager) throws IOException {
		super(output, ConfigurationMarshalling.getDateFormatXml(), strategie, entityManager);
		writeHeader();
	}

	protected void closeTag(final String name) throws IOException {
		if (isPrettyPrint())
			prettyPrintCloseTag();
		writer.write("</");
		writer.write(name);
		writer.write('>');
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getDicoTypeToAction() {
		return dicoTypeToAction;
	}

	protected void openTag(final String name, final Class<?> type) throws IOException {
		if (isPrettyPrint())
			prettyPrintOpenTag();
		writer.write("<");
		writer.write(name);
		if (type != null)
			writeType(type);
		if (!isWrittenUniversal)
			writeInfoUniversal();
		writer.write(">");
	}

	protected void openTagAddIdIfNotNullAndCloseTag(final String name, final Class<?> type, final String id)
			throws IOException {
		if (isPrettyPrint())
			prettyPrintOpenTag();
		writer.write("<");
		writer.write(name);
		if (type != null)
			writeType(type);
		if (!isWrittenUniversal)
			writeInfoUniversal();
		if (id != null) {
			writer.write(" id=\"");
			writer.write(id);
			writer.write("\"");
		}
		writer.write("/>");
		if (isPrettyPrint())
			prettyPrintCloseTag();
	}

	protected void prettyPrintCloseTag() throws IOException {
		--profondeur;
		if (!lastIsOpen) {
			writer.write(System.lineSeparator());
			for (int j = 0; j < profondeur; j++)
				writer.write("   ");
		}
		lastIsOpen = false;
	}

	protected void prettyPrintOpenTag() throws IOException {
		writer.write(System.lineSeparator());
		for (int j = 0; j < profondeur; j++)
			writer.write("   ");
		++profondeur;
		lastIsOpen = true;
	}

	private void writeHeader() throws IOException {
		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}

	private void writeInfoUniversal() throws IOException {
		isWrittenUniversal = true;
		final boolean isUniversal = ConfigurationMarshalling.getEstIdUniversel();
		if (isUniversal) {
			writer.write(" typeId=\"");
			writer.write("universal");
			writer.write("\"");
		}
	}

	private void writeType(final Class<?> type) throws IOException {
		writer.write(" type=\"");
		writer.write(Constants.getSmallNameType(type));
		writer.write("\"");
	}

}
