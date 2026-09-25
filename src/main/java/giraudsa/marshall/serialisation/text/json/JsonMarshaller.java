package giraudsa.marshall.serialisation.text.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
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
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.annotations.TypeRelation;
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
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;
import utils.io.DatesIso;
import utils.io.SortieTexte;

public class JsonMarshaller extends TextMarshaller {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = new ConcurrentHashMap<>();
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
		dicoTypeToAction.put(Character.class, new ActionJsonSimpleWithQuote<Character>());
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
		// sortie non synchronisée qui accumule le texte (StringWriter repose sur un StringBuffer synchronisé)
		final SortieTexte sortie = SortieTexte.pourChaine();
		toCompleteJson(obj, sortie, null);
		return sortie.termine();
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
		// sortie non synchronisée qui accumule le texte (StringWriter repose sur un StringBuffer synchronisé)
		final SortieTexte sortie = SortieTexte.pourChaine();
		toJson(obj, sortie, strategie, entityManager, writeType);
		return sortie.termine();
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
	/**
	 * Au-delà de cette profondeur de récursion, les valeurs sont mises sur la pile au lieu d'être écrites par un
	 * appel récursif : pas de débordement de pile sur un graphe profond.
	 */
	static final int RECURSION_MAX = 200;
	/** nombre d'écritures récursives d'objets imbriquées en cours. */
	int recursion;

	/** Évalue les comportements empilés au-dessus de base (dans l'ordre), y compris ceux qu'ils empilent. */
	void videPileJusqua(final int base)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		while (aFaire.size() > base)
			deserialisePile();
	}

	int hauteurPile() {
		return aFaire.size();
	}

	/** champ de la valeur racine, comme TextMarshaller.marshall (immuable, partagé). */
	private static final FakeChamp RACINE = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);

	/**
	 * Hors mise en forme (pretty print), les valeurs courantes sont écrites par {@link EcrivainJsonDirect} : même
	 * texte que les actions.
	 */
	@Override
	protected <U> void marshall(final U obj)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		if (obj == null || isPrettyPrint() || !direct) {
			super.marshall(obj);
			return;
		}
		try {
			new EcrivainJsonDirect(this).ecrit(obj, RACINE, false);
			while (!aFaire.isEmpty())
				deserialisePile();
			writer.flush();
		} finally {
			rendTables();
		}
	}

	/** false : tout par les actions (comparaison avec l'écriture directe dans les tests). */
	private boolean direct = true;

	/** Écrit par les seules actions, sans {@link EcrivainJsonDirect} (tests). */
	static <U> String toJsonParActions(final U obj, final StrategieDeSerialisation strategie, final boolean writeType)
			throws MarshallExeption {
		final SortieTexte sortie = SortieTexte.pourChaine();
		try {
			final JsonMarshaller v = new JsonMarshaller(sortie, strategie, null, writeType);
			v.direct = false;
			v.marshall(obj);
		} catch (ChampNotFound | IOException | InstantiationException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException e) {
			throw new MarshallExeption(e);
		}
		return sortie.termine();
	}

	//////// accès pour EcrivainJsonDirect

	/** @return l'action de la classe (sans instance). */
	static ActionAbstrait<?> actionDe(final Class<?> type) throws NotImplementedSerializeException {
		try {
			return ACTIONS.get(type);
		} catch (final IllegalStateException e) {
			if (e.getCause() instanceof NotImplementedSerializeException)
				throw (NotImplementedSerializeException) e.getCause();
			throw e;
		}
	}

	/** Écrit la valeur par son action (séparateur déjà écrit), valeurs empilées comprises. */
	void ecritParAction(final Object valeur, final FieldInformations fi)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		final int base = hauteurPile();
		marshall(valeur, fi);
		videPileJusqua(base);
	}

	SortieTexte sortie() {
		return writer;
	}

	boolean idUniversel() {
		return isUniversalId;
	}

	Map<Object, UUID> fakeIds() {
		return getDicoObjToFakeId();
	}

	boolean dejaVu(final Object o) {
		return isDejaVu(o);
	}

	boolean totalementSerialise(final Object o) {
		return isDejaTotalementSerialise(o);
	}

	void marqueDejaVu(final Object o) {
		setDejaVu(o);
	}

	void marqueTotalementSerialise(final Object o) {
		setDejaTotalementSerialise(o);
	}

	boolean serialiseTout(final FieldInformations fi) {
		return strategie.serialiseTout(profondeur, fi);
	}

	DateFormat formatDate() {
		return getDateFormat();
	}

	/** action de chaque classe, résolue une fois (ClassValue est plus rapide qu'une map concurrente). */
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

	@SuppressWarnings("rawtypes")
	@Override
	protected <T> ActionAbstrait getAction(final T obj) throws NotImplementedSerializeException {
		if (obj == null)
			return dicoTypeToAction.get(void.class);
		try {
			return ACTIONS.get(TypeExtension.getClasseASerialiser(obj));
		} catch (final IllegalStateException e) {
			if (e.getCause() instanceof NotImplementedSerializeException)
				throw (NotImplementedSerializeException) e.getCause();
			throw e;
		}
	}

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
			writer.writeClef(nomClef);
	}

	/** clé d'un champ : ses octets préparés quand c'est possible. */
	protected void ecritClef(final Champ champ) throws IOException {
		final byte[] clef = champ.getClefJson();
		if (clef == null) {
			ecritClef(champ.getName());
			return;
		}
		if (isPrettyPrint())
			aLaLigne();
		writer.writeClef(clef, champ.getName());
	}

	protected void ecritType(final Class<?> type) throws IOException {
		if (isPrettyPrint())
			aLaLigne();
		final ClassValue<byte[]> octets = clefType == Constants.CLEF_TYPE ? TYPES : TYPES_ID_UNIVERSEL; // NOSONAR
		final byte[] texte = octets.get(type);
		if (texte != null && writer.writeOctets(texte))
			return;
		writer.writeClef(clefType);
		final String stringType = Constants.getSmallNameType(type);
		writeWithQuote(stringType);
	}

	/** "clé de type":"nom du type" en octets Latin-1 par classe (null si le texte n'est pas Latin-1). */
	private static ClassValue<byte[]> typesEnOctets(final String clef) {
		return new ClassValue<>() {
			@Override
			protected byte[] computeValue(final Class<?> type) {
				final String texte = '"' + clef + "\":\"" + Constants.getSmallNameType(type) + '"';
				for (int i = 0; i < texte.length(); i++)
					if (texte.charAt(i) > 0xFF)
						return null;
				return texte.getBytes(StandardCharsets.ISO_8859_1);
			}
		};
	}

	private static final ClassValue<byte[]> TYPES = typesEnOctets(Constants.CLEF_TYPE);
	private static final ClassValue<byte[]> TYPES_ID_UNIVERSEL = typesEnOctets(Constants.CLEF_TYPE_ID_UNIVERSEL);

	protected void fermeAccolade() throws IOException {
		--profondeur;
		if (isPrettyPrint())
			aLaLigne();
		writer.write('}');
	}

	protected void fermeCrochet(final boolean aLaLigne) throws IOException {
		profondeur--;
		if (isPrettyPrint() && aLaLigne)
			aLaLigne();
		writer.write(']');
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getDicoTypeToAction() {
		return dicoTypeToAction;
	}

	protected void ouvreAccolade() throws IOException {
		++profondeur;
		writer.write('{');
	}

	protected void ouvreCrochet() throws IOException {
		++profondeur;
		writer.write('[');
	}

	/** Écrit la valeur brute (nombre : rien à échapper). */
	void ecritBrut(final String valeur) throws IOException {
		writer.write(valeur);
	}

	void ecritEntier(final long valeur) throws IOException {
		writer.writeLong(valeur);
	}

	/** @return true si la date est écrite par le chemin rapide (format ISO UTC par défaut, années 1583 à 9999). */
	boolean ecritDateRapide(final long millis) throws IOException {
		if (!dateIsoUtc || millis < DatesIso.MIN || millis > DatesIso.MAX)
			return false;
		writer.writeDateIso(millis);
		return true;
	}

	/** Écrit "chaine" entre guillemets, échappée selon la table de remplacements. */
	void ecritEntreGuillemets(final String chaine, final String[] remplacements) throws IOException {
		writer.writeEntreGuillemets(chaine, remplacements);
	}

	protected void writeQuote() throws IOException {
		writer.write('"');
	}

	protected void writeSeparator() throws IOException {
		writer.write(',');
	}

	protected void writeWithQuote(final String string) throws IOException {
		writeQuote();
		writer.write(string);
		writeQuote();
	}
}
