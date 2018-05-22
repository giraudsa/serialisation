package giraudsa.marshall.deserialisation.text.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
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
import java.util.zip.DataFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.text.ActionText;
import giraudsa.marshall.deserialisation.text.TextUnmarshaller;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonArrayType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonAtomicIntegerArray;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonAtomicLongArray;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonBitSet;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonCalendar;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonCurrency;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonDate;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonDictionaryType;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonEnum;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonInetAddress;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonLocale;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonObject;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonSimpleComportement;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonUUID;
import giraudsa.marshall.deserialisation.text.json.actions.ActionJsonVoid;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.JsonHandlerException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import utils.ConfigurationMarshalling;
import utils.Constants;
import utils.EntityManager;
import utils.TypeExtension;

public class JsonUnmarshaller<T> extends TextUnmarshaller<T> {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = Collections
			.synchronizedMap(new HashMap<Class<?>, ActionAbstrait<?>>());
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUnmarshaller.class);
	static {
		dicoTypeToAction.put(Date.class, ActionJsonDate.getInstance());
		dicoTypeToAction.put(Collection.class, ActionJsonCollectionType.getInstance());
		dicoTypeToAction.put(Array.class, ActionJsonArrayType.getInstance());
		dicoTypeToAction.put(Map.class, ActionJsonDictionaryType.getInstance());
		dicoTypeToAction.put(Object.class, ActionJsonObject.getInstance());
		dicoTypeToAction.put(UUID.class, ActionJsonUUID.getInstance());
		dicoTypeToAction.put(Enum.class, ActionJsonEnum.getInstance());
		dicoTypeToAction.put(void.class, ActionJsonVoid.getInstance());
		dicoTypeToAction.put(Void.class, ActionJsonVoid.getInstance());

		dicoTypeToAction.put(String.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Boolean.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Byte.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Float.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Integer.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Double.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Long.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Short.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Character.class, ActionJsonSimpleComportement.getInstance());

		dicoTypeToAction.put(AtomicBoolean.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicInteger.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicLong.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(AtomicIntegerArray.class, ActionJsonAtomicIntegerArray.getInstance());
		dicoTypeToAction.put(AtomicLongArray.class, ActionJsonAtomicLongArray.getInstance());
		dicoTypeToAction.put(BigDecimal.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(BigInteger.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(URI.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(URL.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(Currency.class, ActionJsonCurrency.getInstance());
		dicoTypeToAction.put(Locale.class, ActionJsonLocale.getInstance());
		dicoTypeToAction.put(InetAddress.class, ActionJsonInetAddress.getInstance());
		dicoTypeToAction.put(BitSet.class, ActionJsonBitSet.getInstance());
		dicoTypeToAction.put(Calendar.class, ActionJsonCalendar.getInstance());
		dicoTypeToAction.put(StringBuilder.class, ActionJsonSimpleComportement.getInstance());
		dicoTypeToAction.put(StringBuffer.class, ActionJsonSimpleComportement.getInstance());
	}

	public static <U> U fromJson(final Reader reader) throws UnmarshallExeption {
		return fromJson(reader, null);
	}

	public static <U> U fromJson(final Reader reader, final EntityManager entity) throws UnmarshallExeption {
		try {
			final JsonUnmarshaller<U> w = new JsonUnmarshaller<>(reader, entity);
			return w.parse();
		} catch (FabriqueInstantiationException | ClassNotFoundException | IOException
				| EntityManagerImplementationException | InstanciationException | NotImplementedSerializeException
				| JsonHandlerException | IllegalAccessException | DataFormatException | SetValueException e) {
			LOGGER.error("probleme dans la désérialisation JSON", e);
			throw new UnmarshallExeption("probleme dans la désérialisation JSON", e);
		}
	}

	public static <U> U fromJson(final String stringToUnmarshall) throws UnmarshallExeption {
		if (stringToUnmarshall == null || stringToUnmarshall.length() == 0)
			return null;
		try (StringReader sr = new StringReader(stringToUnmarshall)) {
			return fromJson(sr);
		}
	}

	public static <U> U fromJson(final String stringToUnmarshall, final EntityManager entity)
			throws UnmarshallExeption {
		if (stringToUnmarshall == null || stringToUnmarshall.length() == 0)
			return null;
		try (StringReader sr = new StringReader(stringToUnmarshall)) {
			return fromJson(sr, entity);
		}
	}

	/////// methodes public de désérialisation

	private String clefEnCours;

	private String clefType;

	private boolean waitingForAction;

	///// ATTRIBUTS
	private boolean waitingForType;

	private JsonUnmarshaller(final Reader reader, final EntityManager entity) throws FabriqueInstantiationException {
		super(reader, entity, ConfigurationMarshalling.getDatFormatJson());
	}

	protected void fermeAccolade() throws EntityManagerImplementationException, InstanciationException,
			IllegalAccessException, SetValueException {
		integreObject();
	}

	protected void fermeCrocher() throws EntityManagerImplementationException, InstanciationException,
			IllegalAccessException, SetValueException {
		integreObject();
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction() {
		return dicoTypeToAction;
	}

	@SuppressWarnings("unchecked")
	private void integreObject() throws EntityManagerImplementationException, InstanciationException,
			IllegalAccessException, SetValueException {
		construitObjet(getActionEnCours());
		final ActionJson<?> actionATraiter = (ActionJson<?>) pileAction.pop();
		if (pileAction.isEmpty())
			obj = (T) getObjet(actionATraiter);
		else {
			final String nom = getNom(actionATraiter);
			final Object objet = getObjet(actionATraiter);
			integreObjet(getActionEnCours(), nom, objet);
		}
	}

	private boolean isClefType(final String clef) {
		if (clef.equals(clefType) || clef.equals(Constants.CLEF_TYPE)
				|| clef.equals(Constants.CLEF_TYPE_ID_UNIVERSEL)) {
			if (clefType == null) {
				// récuperation de la configuration idUniversel de celui qui a encodé
				final boolean isIdUniversel = clef.equals(Constants.CLEF_TYPE_ID_UNIVERSEL) ? true : false;
				clefType = clef;
				setCache(isIdUniversel);
			}
			return true;
		}
		return false;
	}

	protected void ouvreAccolade() {
		waitingForAction = true;
	}

	protected void ouvreChrochet() throws NotImplementedSerializeException {
		Class<?> type = getActionEnCours() == null ? ArrayList.class : getType(clefEnCours);
		if (type == null)
			type = ArrayList.class;
		final ActionJson<?> action = (ActionJson<?>) getAction(type);
		setNom(action, clefEnCours);
		setFieldInformation(action);
		clefEnCours = null;
		pileAction.push(action);
	}

	private T parse() throws ClassNotFoundException, IOException, EntityManagerImplementationException,
			InstanciationException, NotImplementedSerializeException, JsonHandlerException, UnmarshallExeption,
			IllegalAccessException, DataFormatException, SetValueException {
		final JsonUnmarshallerHandler handler = new JsonUnmarshallerHandler(this);
		handler.parse(reader);
		if (obj == null)
			throw new DataFormatException("le format n'est pas un json");
		return obj;
	}

	protected void setClef(final String clef) throws NotImplementedSerializeException {
		if (isClefType(clef))
			waitingForType = true;
		else if (!pileAction.isEmpty()) {
			if (waitingForAction) {
				Class<?> typeToUnmarshall = getType(clefEnCours);
				typeToUnmarshall = TypeExtension.getTypeEnveloppe(typeToUnmarshall);
				final ActionText<?> action = (ActionText<?>) getAction(typeToUnmarshall);
				setNom(action, clefEnCours);
				setFieldInformation(action);
				pileAction.push(action);
				waitingForAction = false;
			}
			clefEnCours = clef;
		}
	}

	protected void setValeur(final String valeur, final Class<?> typeGuess)
			throws EntityManagerImplementationException, InstanciationException, ClassNotFoundException,
			NotImplementedSerializeException, IllegalAccessException, SetValueException {
		Class<?> type;
		if (waitingForType)
			type = getTypeDepuisNom(valeur);
		else
			type = getType(clefEnCours);
		Class<?> typeAction = typeGuess;
		if (typeGuess != Void.class && type != null && !type.isAssignableFrom(typeGuess))
			typeAction = type;
		final ActionJson<?> action = (ActionJson<?>) getAction(typeAction);
		setNom(action, clefEnCours);
		setFieldInformation(action);
		clefEnCours = null;
		pileAction.push(action);
		if (!waitingForType) {
			rempliData(getActionEnCours(), valeur);
			integreObject();
		}
		waitingForType = false;
		waitingForAction = false;
	}
}
