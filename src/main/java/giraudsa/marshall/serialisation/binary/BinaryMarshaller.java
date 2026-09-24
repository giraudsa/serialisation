package giraudsa.marshall.serialisation.binary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
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
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryArrayType;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryAtomicIntegerArray;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryAtomicLongArray;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryBitSet;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryCalendar;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryCollectionType;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryCurrency;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryDate;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryDictionaryType;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryEnum;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryInetAddress;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryObject;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryString;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryStringBuffer;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryStringBuilder;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryUUID;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryUri;
import giraudsa.marshall.serialisation.binary.actions.ActionBinaryUrl;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryAtomicBoolean;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryAtomicInteger;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryAtomicLong;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryBigDecimal;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryBigInteger;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryBoolean;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryByte;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryChar;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryDouble;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryFloat;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryInteger;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryLocale;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryLong;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryShort;
import giraudsa.marshall.serialisation.binary.actions.simple.ActionBinaryVoid;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import giraudsa.marshall.strategie.StrategieParComposition;
import giraudsa.marshall.strategie.StrategieSerialisationComplete;
import utils.Constants;
import utils.EgaliteIntMap;
import utils.IdentiteIntMap;
import utils.TypeExtension;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;
import utils.io.SortieBinaire;

public class BinaryMarshaller extends Marshaller {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = new ConcurrentHashMap<>();
	/** champ fictif de la racine du graphe (sans état propre : partagé). */
	private static final FakeChamp RACINE = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryMarshaller.class);
	static {
		dicoTypeToAction.put(void.class, new ActionBinaryVoid());
		dicoTypeToAction.put(Boolean.class, new ActionBinaryBoolean());
		dicoTypeToAction.put(Integer.class, new ActionBinaryInteger());
		dicoTypeToAction.put(Byte.class, new ActionBinaryByte());
		dicoTypeToAction.put(Float.class, new ActionBinaryFloat());
		dicoTypeToAction.put(Double.class, new ActionBinaryDouble());
		dicoTypeToAction.put(Long.class, new ActionBinaryLong());
		dicoTypeToAction.put(Short.class, new ActionBinaryShort());
		dicoTypeToAction.put(Character.class, new ActionBinaryChar());
		dicoTypeToAction.put(UUID.class, new ActionBinaryUUID());
		dicoTypeToAction.put(String.class, new ActionBinaryString());
		dicoTypeToAction.put(Date.class, new ActionBinaryDate());
		dicoTypeToAction.put(Enum.class, new ActionBinaryEnum());
		dicoTypeToAction.put(Collection.class, new ActionBinaryCollectionType());
		dicoTypeToAction.put(Array.class, new ActionBinaryArrayType());
		dicoTypeToAction.put(Map.class, new ActionBinaryDictionaryType());
		dicoTypeToAction.put(Object.class, new ActionBinaryObject());

		dicoTypeToAction.put(AtomicBoolean.class, new ActionBinaryAtomicBoolean());
		dicoTypeToAction.put(AtomicInteger.class, new ActionBinaryAtomicInteger());
		dicoTypeToAction.put(AtomicLong.class, new ActionBinaryAtomicLong());
		dicoTypeToAction.put(AtomicIntegerArray.class, new ActionBinaryAtomicIntegerArray());
		dicoTypeToAction.put(AtomicLongArray.class, new ActionBinaryAtomicLongArray());
		dicoTypeToAction.put(BigDecimal.class, new ActionBinaryBigDecimal());
		dicoTypeToAction.put(BigInteger.class, new ActionBinaryBigInteger());
		dicoTypeToAction.put(URI.class, new ActionBinaryUri());
		dicoTypeToAction.put(URL.class, new ActionBinaryUrl());
		dicoTypeToAction.put(Currency.class, new ActionBinaryCurrency());
		dicoTypeToAction.put(Locale.class, new ActionBinaryLocale());
		dicoTypeToAction.put(InetAddress.class, new ActionBinaryInetAddress());
		dicoTypeToAction.put(BitSet.class, new ActionBinaryBitSet());
		dicoTypeToAction.put(Calendar.class, new ActionBinaryCalendar());
		dicoTypeToAction.put(StringBuilder.class, new ActionBinaryStringBuilder());
		dicoTypeToAction.put(StringBuffer.class, new ActionBinaryStringBuffer());
	}

	public static <U> void toBinary(final U obj, final OutputStream output) throws MarshallExeption {
		toBinary(obj, output, new StrategieParComposition());
	}

	///// METHODES STATICS PUBLICS
	public static <U> void toBinary(final U obj, final OutputStream output, final StrategieDeSerialisation strategie)
			throws MarshallExeption {
		ecrit(obj, output, strategie, "Problème lors de la sérialisation binaire");
	}

	public static <U> void toCompleteBinary(final U obj, final OutputStream output) throws MarshallExeption {
		ecrit(obj, output, new StrategieSerialisationComplete(), "Problème lors de la sérialisation binaire complète");
	}

	private static <U> void ecrit(final U obj, final OutputStream output, final StrategieDeSerialisation strategie,
			final String messageErreur) throws MarshallExeption {
		EtatEcriture etat = ETATS.get();
		if (etat.enUsage) // appel imbriqué (stratégie inconnue sérialisée en tête de flux)
			etat = new EtatEcriture();
		etat.enUsage = true;
		try {
			etat.sortie.reinitialise(output);
			final BinaryMarshaller v = new BinaryMarshaller(etat, strategie);
			v.marshall(obj);
			etat.sortie.flush();
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.error(messageErreur, e);
			throw new MarshallExeption(e);
		} finally {
			etat.libere();
		}
	}

	/**
	 * Tables et tampons d'une sérialisation, réutilisés d'un appel à l'autre sur le même thread : on évite de les
	 * réallouer et de les agrandir à chaque graphe.
	 */
	private static final class EtatEcriture {
		private final EgaliteIntMap dejaVuDate = new EgaliteIntMap(64);
		private final EgaliteIntMap dejaVuString = new EgaliteIntMap(1024);
		private final IdentiteIntMap dejaVuType = new IdentiteIntMap(16);
		private final EgaliteIntMap dejaVuUuid = new EgaliteIntMap(64);
		private boolean enUsage;
		private FieldInformations[] pileChamps = new FieldInformations[256];
		private Object[] pileValeurs = new Object[256];
		private final SortieBinaire sortie = new SortieBinaire(null);
		private final IdentiteIntMap smallIds = new IdentiteIntMap(1024);
		private final BitSet totalementSerialises = new BitSet();

		private void libere() {
			dejaVuDate.vide();
			dejaVuString.vide();
			dejaVuType.vide();
			dejaVuUuid.vide();
			smallIds.vide();
			totalementSerialises.clear();
			if (pileValeurs.length > 1 << 16) {
				pileValeurs = new Object[256];
				pileChamps = new FieldInformations[256];
			}
			sortie.reinitialise(null);
			enUsage = false;
		}
	}

	private static final ThreadLocal<EtatEcriture> ETATS = ThreadLocal.withInitial(EtatEcriture::new);

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

	private int compteur = 1;
	private int compteurDate = 1;
	private int compteurString = 1;
	private short compteurType = 1;
	private int compteurUuid = 1;
	private final EgaliteIntMap dejaVuDate;
	private final EgaliteIntMap dejaVuString;
	private final IdentiteIntMap dejaVuType;
	private final EgaliteIntMap dejaVuUuid;
	private final EtatEcriture etat;
	protected final SortieBinaire output;
	private final IdentiteIntMap smallIds;
	/** objets totalement sérialisés, indexés par smallId. */
	private final BitSet totalementSerialises;
	/** dernier objet dont l'en-tête a été écrit, et son smallId : évite de le rechercher à nouveau. */
	private Object objetCourant;
	private int smallIdCourant;

	/**
	 * Pile des valeurs à sérialiser (tableaux parallèles : aucune allocation par valeur). Un champ null marque la fin
	 * d'un objet : la profondeur diminue.
	 */
	private FieldInformations[] pileChamps;
	private Object[] pileValeurs;
	private int hautPile;
	/** début, dans la pile, des valeurs de l'objet courant en attente (-1 si aucune). */
	int debutAttente = -1;

	private BinaryMarshaller(final EtatEcriture etat, final StrategieDeSerialisation strategie)
			throws IOException, MarshallExeption {
		super(strategie, null);
		this.etat = etat;
		output = etat.sortie;
		dejaVuDate = etat.dejaVuDate;
		dejaVuString = etat.dejaVuString;
		dejaVuType = etat.dejaVuType;
		dejaVuUuid = etat.dejaVuUuid;
		smallIds = etat.smallIds;
		totalementSerialises = etat.totalementSerialises;
		pileChamps = etat.pileChamps;
		pileValeurs = etat.pileValeurs;
		writeSpecialisation();
	}

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

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getDicoTypeToAction() {
		return dicoTypeToAction;
	}

	/**
	 * Les méthodes smallIdXxx renvoient le smallId déjà attribué (> 0), ou l'opposé du smallId qu'elles viennent
	 * d'attribuer à une première apparition (< 0) : un seul accès à la table.
	 */
	protected int smallIdDate(final Date date) {
		final int existant = dejaVuDate.putIfAbsent(date, compteurDate);
		return existant != IdentiteIntMap.ABSENT ? existant : -compteurDate++;
	}

	protected int smallIdString(final String string) {
		final int existant = dejaVuString.putIfAbsent(string, compteurString);
		return existant != IdentiteIntMap.ABSENT ? existant : -compteurString++;
	}

	protected int smallIdType(final Class<?> type) {
		final int existant = dejaVuType.putIfAbsent(type, compteurType);
		return existant != IdentiteIntMap.ABSENT ? existant : -compteurType++;
	}

	protected int smallIdUUID(final UUID id) {
		final int existant = dejaVuUuid.putIfAbsent(id, compteurUuid);
		return existant != IdentiteIntMap.ABSENT ? existant : -compteurUuid++;
	}

	protected int smallIdObjet(final Object obj) {
		final int existant = smallIds.putIfAbsent(obj, compteur);
		final int smallId = existant != IdentiteIntMap.ABSENT ? existant : compteur++;
		objetCourant = obj;
		smallIdCourant = smallId;
		return existant != IdentiteIntMap.ABSENT ? existant : -smallId;
	}

	private int smallIdConnu(final Object obj) {
		return obj == objetCourant ? smallIdCourant : smallIds.get(obj);
	}

	@Override
	protected <T> boolean isDejaTotalementSerialise(final T obj) {
		final int smallId = smallIdConnu(obj);
		if (smallId > 0)
			return totalementSerialises.get(smallId);
		return super.isDejaTotalementSerialise(obj);
	}

	@Override
	protected <T> void setDejaTotalementSerialise(final T obj) {
		final int smallId = smallIdConnu(obj);
		if (smallId > 0)
			totalementSerialises.set(smallId);
		else
			super.setDejaTotalementSerialise(obj);
	}

	/** Empile une valeur ; un champ null marque la fin d'un objet (la profondeur diminuera). */
	void empile(final Object valeur, final FieldInformations fieldInformations) {
		if (hautPile == pileValeurs.length) {
			pileValeurs = Arrays.copyOf(pileValeurs, hautPile * 2);
			pileChamps = Arrays.copyOf(pileChamps, hautPile * 2);
			etat.pileValeurs = pileValeurs;
			etat.pileChamps = pileChamps;
		}
		pileValeurs[hautPile] = valeur;
		pileChamps[hautPile++] = fieldInformations;
	}

	/** Met une valeur en attente : elle est empilée, l'ordre des valeurs en attente sera inversé à la fin. */
	void differe(final Object valeur, final FieldInformations fieldInformations) {
		if (debutAttente < 0)
			debutAttente = hautPile;
		empile(valeur, fieldInformations);
	}

	/** Inverse les valeurs en attente pour qu'elles soient dépilées dans leur ordre d'origine. */
	void empileDifferes() {
		if (debutAttente < 0)
			return;
		for (int i = debutAttente, j = hautPile - 1; i < j; i++, j--) {
			final Object v = pileValeurs[i];
			pileValeurs[i] = pileValeurs[j];
			pileValeurs[j] = v;
			final FieldInformations c = pileChamps[i];
			pileChamps[i] = pileChamps[j];
			pileChamps[j] = c;
		}
		debutAttente = -1;
	}

	protected boolean isSmallIdDefined(final Object obj) {
		return smallIds.get(obj) != IdentiteIntMap.ABSENT;
	}

	///// METHODES
	private <T> void marshall(final T obj)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		final FakeChamp fieldsInfo = RACINE;
		marshall(obj, fieldsInfo);
		while (hautPile > 0) {
			final int i = --hautPile;
			final FieldInformations champ = pileChamps[i];
			final Object valeur = pileValeurs[i];
			pileChamps[i] = null;
			pileValeurs[i] = null;
			if (champ == null)
				--profondeur;
			else
				marshall(valeur, champ);
		}
	}

	//////////
	protected void writeBoolean(final boolean v) throws IOException {
		output.writeBoolean(v);
	}

	protected void writeByte(final byte v) throws IOException {
		output.writeByte(v);

	}

	protected void writeByteArray(final byte[] v) throws IOException {
		output.write(v);
	}

	protected void writeChar(final char v) throws IOException {
		output.writeChar(v);

	}

	protected void writeDouble(final double v) throws IOException {
		output.writeDouble(v);

	}

	protected void writeFloat(final float v) throws IOException {
		output.writeFloat(v);

	}

	protected void writeInt(final int v) throws IOException {
		output.writeInt(v);

	}

	protected void writeLong(final long v) throws IOException {
		output.writeLong(v);

	}

	protected void writeShort(final short v) throws IOException {
		output.writeShort(v);

	}

	private void writeSpecialisation() throws IOException, MarshallExeption {
		final byte firstByte = Constants.getFirstByte(strategie);
		writeByte(firstByte);
		if (firstByte == Constants.STRATEGIE_INCONNUE) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			toCompleteBinary(strategie, out);
			writeByteArray(out.toByteArray());
		}
	}

	protected void writeUTF(final String s) throws IOException {
		output.writeString(s);
	}

	protected void writeVarInt(final int v) throws IOException {
		output.writeVarInt(v);
	}

}
