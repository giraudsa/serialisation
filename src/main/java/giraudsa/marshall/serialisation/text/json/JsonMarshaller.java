package giraudsa.marshall.serialisation.text.json;

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
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonArrayType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonAtomicArrayIntegerType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonAtomicArrayLongType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonBitSet;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonCalendar;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonDictionary;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonObject;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonBoolean;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonCurrency;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonDate;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonInetAddress;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonInteger;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonString;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonUri;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonUrl;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonVoid;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import giraudsa.marshall.strategie.StrategieParComposition;
import giraudsa.marshall.strategie.StrategieSerialisationComplete;
import utils.ConfigurationMarshalling;
import utils.Constants;
import utils.EntityManager;

public class JsonMarshaller extends TextMarshaller {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections
			.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonMarshaller.class);

	static {
		dicoTypeToAction.put(Date.class, new ActionJsonDate());
		dicoTypeToAction.put(Boolean.class, new ActionJsonBoolean());
		dicoTypeToAction.put(Collection.class, new ActionJsonCollectionType());
		dicoTypeToAction.put(Array.class, new ActionJsonArrayType());
		dicoTypeToAction.put(Map.class, new ActionJsonDictionary());
		dicoTypeToAction.put(Object.class, new ActionJsonObject());
		dicoTypeToAction.put(void.class, new ActionJsonVoid());
		dicoTypeToAction.put(Integer.class, new ActionJsonInteger());
		dicoTypeToAction.put(Enum.class, new ActionJsonSimpleWithQuote<Enum>());
		dicoTypeToAction.put(UUID.class, new ActionJsonSimpleWithQuote<UUID>());
		dicoTypeToAction.put(String.class, new ActionJsonString());
		dicoTypeToAction.put(Byte.class, new ActionJsonSimpleWithoutQuote<Byte>());
		dicoTypeToAction.put(Float.class, new ActionJsonSimpleWithoutQuote<Float>());
		dicoTypeToAction.put(Double.class, new ActionJsonSimpleWithoutQuote<Double>());
		dicoTypeToAction.put(Long.class, new ActionJsonSimpleWithoutQuote<Long>());
		dicoTypeToAction.put(Short.class, new ActionJsonSimpleWithoutQuote<Short>());
		dicoTypeToAction.put(AtomicBoolean.class, new ActionJsonSimpleWithoutQuote<AtomicBoolean>());
		dicoTypeToAction.put(AtomicInteger.class, new ActionJsonSimpleWithoutQuote<AtomicInteger>());
		dicoTypeToAction.put(AtomicLong.class, new ActionJsonSimpleWithoutQuote<AtomicLong>());
		dicoTypeToAction.put(AtomicIntegerArray.class, new ActionJsonAtomicArrayIntegerType());
		dicoTypeToAction.put(AtomicLongArray.class, new ActionJsonAtomicArrayLongType());
		dicoTypeToAction.put(BigDecimal.class, new ActionJsonSimpleWithoutQuote<BigDecimal>());
		dicoTypeToAction.put(BigInteger.class, new ActionJsonSimpleWithoutQuote<BigInteger>());
		dicoTypeToAction.put(URI.class, new ActionJsonUri());
		dicoTypeToAction.put(URL.class, new ActionJsonUrl());
		dicoTypeToAction.put(Currency.class, new ActionJsonCurrency());
		dicoTypeToAction.put(Locale.class, new ActionJsonSimpleWithQuote<Locale>());
		dicoTypeToAction.put(InetAddress.class, new ActionJsonInetAddress());
		dicoTypeToAction.put(BitSet.class, new ActionJsonBitSet());
		dicoTypeToAction.put(Calendar.class, new ActionJsonCalendar());
		dicoTypeToAction.put(StringBuilder.class, new ActionJsonSimpleWithQuote<StringBuilder>());
		dicoTypeToAction.put(StringBuffer.class, new ActionJsonSimpleWithQuote<StringBuffer>());
	}

	public static <U> String toCompleteJson(final U obj) throws MarshallExeption {
		try (StringWriter sw = new StringWriter()) {
			toCompleteJson(obj, sw, null);
			return sw.toString();
		} catch (final IOException e) {
			LOGGER.debug("Problème à la création d'un StringWriter", e);
			throw new MarshallExeption(e);
		}
	}

	public static <U> void toCompleteJson(final U obj, final Writer output, final EntityManager entityManager)
			throws MarshallExeption {
		try {
			final JsonMarshaller v = new JsonMarshaller(output, new StrategieSerialisationComplete(), entityManager,
					true);
			v.marshall(obj);
		} catch (ChampNotFound | IOException | InstantiationException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.debug("probleme de sérialisation complète en json de " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}

	public static <U> String toJson(final U obj) throws MarshallExeption {
		return toJson(obj, new StrategieParComposition(), null, true);
	}

	public static <U> String toJson(final U obj, final EntityManager entityManager) throws MarshallExeption {
		return toJson(obj, new StrategieParComposition(), entityManager, true);
	}

	public static <U> String toJson(final U obj, final StrategieDeSerialisation strategie,
			final EntityManager entityManager, final boolean writeType) throws MarshallExeption {
		try (StringWriter sw = new StringWriter()) {
			toJson(obj, sw, strategie, entityManager, writeType);
			return sw.toString();
		} catch (final IOException e) {
			LOGGER.debug("Problème à la création d'un StringWriter", e);
			throw new MarshallExeption(e);
		}
	}

	// /////METHODES PUBLIQUES STATIQUES
	public static <U> void toJson(final U obj, final Writer output, final EntityManager entityManager)
			throws MarshallExeption {
		toJson(obj, output, new StrategieParComposition(), entityManager, true);
	}

	public static <U> void toJson(final U obj, final Writer output, final StrategieDeSerialisation strategie,
			final EntityManager entityManager, final boolean writeType) throws MarshallExeption {
		try {
			final JsonMarshaller v = new JsonMarshaller(output, strategie, entityManager, writeType);
			v.marshall(obj);
		} catch (ChampNotFound | IOException | InstantiationException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.debug("probleme de sérialisation json de " + obj.toString(), e);
			throw new MarshallExeption(e);
		}
	}

	private final String clefType;

	private boolean isFirst = true;
	final boolean writeType;

	// ///CONSTRUCTEUR
	private JsonMarshaller(final Writer output, final StrategieDeSerialisation strategie,
			final EntityManager entityManager, final boolean writeType) throws IOException {
		super(output, ConfigurationMarshalling.getDatFormatJson(), strategie, entityManager);
		this.writeType = writeType;
		clefType = ConfigurationMarshalling.getEstIdUniversel() ? Constants.CLEF_TYPE_ID_UNIVERSEL
				: Constants.CLEF_TYPE;
	}

	// ///ME

	/// prettyPrint methode
	private void aLaLigne() throws IOException {
		if (isFirst) {
			isFirst = false;
			return;
		}
		writer.write(System.lineSeparator());
		for (int j = 0; j < profondeur; j++)
			writer.write("   ");
	}

	protected void ecritClef(final String nomClef) throws IOException {
		if (isPrettyPrint())
			aLaLigne();
		if (nomClef != null)
			writer.write("\"" + nomClef + "\":");
	}

	protected void ecritType(final Class<?> type) throws IOException {
		ecritClef(clefType);
		final String stringType = Constants.getSmallNameType(type);
		writeWithQuote(stringType);
	}

	protected void fermeAccolade() throws IOException {
		--profondeur;
		if (isPrettyPrint())
			aLaLigne();
		writer.write("}");
	}

	protected void fermeCrochet(final boolean aLaLigne) throws IOException {
		profondeur--;
		if (isPrettyPrint() && aLaLigne)
			aLaLigne();
		writer.write("]");
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getDicoTypeToAction() {
		return dicoTypeToAction;
	}

	protected void ouvreAccolade() throws IOException {
		++profondeur;
		writer.write("{");
	}

	protected void ouvreCrochet() throws IOException {
		++profondeur;
		writer.write("[");
	}

	protected void writeQuote() throws IOException {
		writer.write("\"");
	}

	protected void writeSeparator() throws IOException {
		writer.write(",");
	}

	protected void writeWithQuote(final String string) throws IOException {
		writeQuote();
		writer.write(string);
		writeQuote();
	}
}
