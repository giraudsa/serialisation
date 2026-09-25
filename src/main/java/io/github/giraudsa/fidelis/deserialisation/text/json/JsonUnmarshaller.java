package io.github.giraudsa.fidelis.deserialisation.text.json;

import java.lang.System.Logger.Level;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.zip.DataFormatException;
import java.util.concurrent.ConcurrentHashMap;


import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.CacheIdNonUniversel;
import io.github.giraudsa.fidelis.deserialisation.text.ActionText;
import io.github.giraudsa.fidelis.deserialisation.text.TextUnmarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonArrayType;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonAtomicIntegerArray;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonAtomicLongArray;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonBitSet;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonCalendar;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonCollectionType;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonCurrency;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonDate;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonDictionaryType;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonEnum;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonInetAddress;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonLocale;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonObject;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonSimpleComportement;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonUUID;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonVoid;
import io.github.giraudsa.fidelis.exception.EntityManagerImplementationException;
import io.github.giraudsa.fidelis.exception.FabriqueInstantiationException;
import io.github.giraudsa.fidelis.exception.InstanciationException;
import io.github.giraudsa.fidelis.exception.JsonHandlerException;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.exception.SetValueException;
import io.github.giraudsa.fidelis.exception.UnmarshallExeption;
import io.github.giraudsa.fidelis.utils.ConfigurationMarshalling;
import io.github.giraudsa.fidelis.utils.CopieFormatDate;
import io.github.giraudsa.fidelis.utils.Constants;
import io.github.giraudsa.fidelis.utils.EntityManager;
import io.github.giraudsa.fidelis.utils.TypeExtension;

public class JsonUnmarshaller<T> extends TextUnmarshaller<T> {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = new ConcurrentHashMap<>();

	/** prototype d'action de chaque classe, résolu une fois (ClassValue : plus rapide qu'une map concurrente). */
	private static final ClassValue<ActionAbstrait<?>> ACTIONS = new ClassValue<>() {
		@Override
		protected ActionAbstrait<?> computeValue(final Class<?> type) {
			final ActionAbstrait<?> action = dicoTypeToAction.get(type);
			if (action != null)
				return action;
			try {
				return choisiAction(dicoTypeToAction, type);
			} catch (final NotImplementedSerializeException e) {
				throw new IllegalStateException(e);
			}
		}
	};

	/** @return le prototype d'action de la classe (sans l'instancier). */
	static ActionAbstrait<?> prototype(final Class<?> type) throws NotImplementedSerializeException {
		if (type == null)
			return null;
		try {
			return ACTIONS.get(type);
		} catch (final IllegalStateException e) {
			if (e.getCause() instanceof NotImplementedSerializeException)
				throw (NotImplementedSerializeException) e.getCause();
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected <U> ActionAbstrait getAction(final Class<U> type) throws NotImplementedSerializeException {
		if (type == null)
			return null;
		try {
			return ACTIONS.get(type).getNewInstance((Class) type, this);
		} catch (final IllegalStateException e) {
			if (e.getCause() instanceof NotImplementedSerializeException)
				throw (NotImplementedSerializeException) e.getCause();
			throw e;
		}
	}
	private static final System.Logger LOGGER = System.getLogger(JsonUnmarshaller.class.getName());
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
			if (entity == null)
				return lit(new String(litTout(reader)));
			final JsonUnmarshaller<U> w = new JsonUnmarshaller<>(reader, entity);
			return w.parse();
		} catch (FabriqueInstantiationException | ClassNotFoundException | IOException
				| EntityManagerImplementationException | InstanciationException | NotImplementedSerializeException
				| JsonHandlerException | IllegalAccessException | DataFormatException | SetValueException e) {
			LOGGER.log(Level.ERROR, "probleme dans la désérialisation JSON", e);
			throw new UnmarshallExeption("probleme dans la désérialisation JSON", e);
		}
	}

	public static <U> U fromJson(final String stringToUnmarshall) throws UnmarshallExeption {
		if (stringToUnmarshall == null || stringToUnmarshall.length() == 0)
			return null;
		return lit(stringToUnmarshall);
	}

	/**
	 * Lecture sans gestionnaire d'entités : directe (LecteurJsonDirect) quand le texte s'y prête, sinon par le
	 * lecteur historique.
	 */
	@SuppressWarnings("unchecked")
	private static <U> U lit(final String texte) throws UnmarshallExeption {
		final Object direct = LecteurJsonDirect.lit(texte);
		if (direct != null)
			return (U) direct;
		return litHistorique(texte);
	}

	/** lecture par le lecteur historique (événements et actions). */
	static <U> U litHistorique(final String texte) throws UnmarshallExeption {
		try {
			final JsonUnmarshaller<U> w = new JsonUnmarshaller<>(new StringReader(texte), null);
			return w.parse();
		} catch (FabriqueInstantiationException | ClassNotFoundException | IOException
				| EntityManagerImplementationException | InstanciationException | NotImplementedSerializeException
				| JsonHandlerException | IllegalAccessException | DataFormatException | SetValueException e) {
			LOGGER.log(Level.ERROR, "probleme dans la désérialisation JSON", e);
			throw new UnmarshallExeption("probleme dans la désérialisation JSON", e);
		}
	}

	private static char[] litTout(final Reader reader) throws IOException {
		char[] texte = new char[8192];
		int taille = 0;
		int lu;
		while ((lu = reader.read(texte, taille, texte.length - taille)) != -1) {
			taille += lu;
			if (taille == texte.length)
				texte = Arrays.copyOf(texte, taille * 2);
		}
		return Arrays.copyOf(texte, taille);
	}

	//////// accès pour LecteurJsonDirect

	static JsonUnmarshaller<?> pourLectureDirecte() throws FabriqueInstantiationException {
		return new JsonUnmarshaller<>(ConfigurationMarshalling.getDatFormatJson());
	}

	/** format de date de la lecture directe, copié à la première date qui n'est pas au format ISO UTC. */
	private SimpleDateFormat formatSource;
	private DateFormat formatCopie;

	private JsonUnmarshaller(final SimpleDateFormat formatSource) throws FabriqueInstantiationException {
		super(null, formatSource);
		this.formatSource = formatSource;
	}

	static Class<?> classeDepuisNom(final String nom) throws ClassNotFoundException {
		return getTypeDepuisNom(nom);
	}

	Object objetParId(final String id, final Class<?> type)
			throws EntityManagerImplementationException, InstanciationException {
		if (entity == null && cacheObject instanceof CacheIdNonUniversel)
			return ((CacheIdNonUniversel) cacheObject).obtient(type, id, this::newInstance);
		return getObject(id, type);
	}

	void choisitCache(final boolean isIdUniversel) {
		setCache(isIdUniversel);
	}

	Map<Object, UUID> fakeIds() {
		return getDicoObjToFakeId();
	}

	DateFormat formatDate() {
		if (df != null)
			return df;
		if (formatCopie == null)
			formatCopie = CopieFormatDate.copie(formatSource);
		return formatCopie;
	}

	boolean datesIsoUtc() {
		return dateIsoUtc;
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

	@SuppressWarnings("unchecked")
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
		if (!waitingForType && ActionJsonSimpleComportement.estActionDe(prototype(typeAction))) {
			// valeur simple (nombre, booléen, chaîne...) : même résultat que l'action empilée, remplie puis intégrée
			// par integreObject, sans l'allouer ni l'empiler
			final Object valeurLue = ActionJsonSimpleComportement.construit(typeAction, valeur);
			final String nom = clefEnCours;
			clefEnCours = null;
			waitingForAction = false;
			if (pileAction.isEmpty())
				obj = (T) valeurLue;
			else
				integreObjet(getActionEnCours(), nom, valeurLue);
			return;
		}
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
