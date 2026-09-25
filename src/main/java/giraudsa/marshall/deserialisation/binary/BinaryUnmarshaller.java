package giraudsa.marshall.deserialisation.binary;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayDeque;
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
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryArray;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryCollection;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryDictionary;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryEnum;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryCollection;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryDictionary;
import giraudsa.marshall.deserialisation.binary.actions.ActionBinaryObject;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinarySimple;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicBoolean;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicInteger;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicIntegerArray;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicLong;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryAtomicLongArray;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryBigDecimal;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryBigInteger;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryBitSet;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryCalendar;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryCurrency;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryDate;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryInetAddress;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryLocale;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryStringBuffer;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryStringBuilder;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryUri;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinaryUrl;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import utils.Constants;
import utils.EntityManager;
import utils.TypeExtension;
import utils.TypeExtension.ChampsDuType;
import utils.champ.AccesChamp;
import utils.champ.Champ;
import utils.champ.FakeChamp;
import utils.champ.GenerateurSerialiseurs;
import utils.champ.LecteurChamps;
import utils.champ.FieldInformations;
import utils.headers.Header;
import utils.headers.HeaderSimpleType;
import utils.headers.HeaderTypeCourant;
import utils.headers.TypesPredefinis;
import utils.io.EntreeBinaire;
import utils.io.Primitifs;

public class BinaryUnmarshaller<T> extends Unmarshaller<T> {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = new ConcurrentHashMap<>();
	/** champ fictif de la racine du graphe (sans état propre : partagé). */
	private static final FakeChamp RACINE = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);
	private static final Logger LOGGER = LoggerFactory.getLogger(BinaryUnmarshaller.class);
	static {
		dicoTypeToAction.put(Constants.dateType, ActionBinaryDate.getInstance());
		dicoTypeToAction.put(Constants.collectionType, ActionBinaryCollection.getInstance());
		dicoTypeToAction.put(Constants.arrayType, ActionBinaryArray.getInstance());
		dicoTypeToAction.put(Constants.dictionaryType, ActionBinaryDictionary.getInstance());
		dicoTypeToAction.put(Constants.objectType, ActionBinaryObject.getInstance());
		dicoTypeToAction.put(Constants.enumType, ActionBinaryEnum.getInstance());
		dicoTypeToAction.put(AtomicBoolean.class, ActionBinaryAtomicBoolean.getInstance());
		dicoTypeToAction.put(AtomicInteger.class, ActionBinaryAtomicInteger.getInstance());
		dicoTypeToAction.put(AtomicLong.class, ActionBinaryAtomicLong.getInstance());
		dicoTypeToAction.put(AtomicIntegerArray.class, ActionBinaryAtomicIntegerArray.getInstance());
		dicoTypeToAction.put(AtomicLongArray.class, ActionBinaryAtomicLongArray.getInstance());
		dicoTypeToAction.put(BigDecimal.class, ActionBinaryBigDecimal.getInstance());
		dicoTypeToAction.put(BigInteger.class, ActionBinaryBigInteger.getInstance());
		dicoTypeToAction.put(URI.class, ActionBinaryUri.getInstance());
		dicoTypeToAction.put(URL.class, ActionBinaryUrl.getInstance());
		dicoTypeToAction.put(Currency.class, ActionBinaryCurrency.getInstance());
		dicoTypeToAction.put(Locale.class, ActionBinaryLocale.getInstance());
		dicoTypeToAction.put(InetAddress.class, ActionBinaryInetAddress.getInstance());
		dicoTypeToAction.put(BitSet.class, ActionBinaryBitSet.getInstance());
		dicoTypeToAction.put(Calendar.class, ActionBinaryCalendar.getInstance());
		dicoTypeToAction.put(StringBuilder.class, ActionBinaryStringBuilder.getInstance());
		dicoTypeToAction.put(StringBuffer.class, ActionBinaryStringBuffer.getInstance());
	}

	public static <U> U fromBinary(final InputStream reader) throws UnmarshallExeption {
		return fromBinary(reader, null);
	}

	/**
	 * Désérialise un objet à partir d'un InputStream L'entity Manager permet
	 * 
	 * @param reader
	 * @param entity
	 * @return
	 * @throws UnmarshallExeption
	 */
	public static <U> U fromBinary(final InputStream reader, final EntityManager entity) throws UnmarshallExeption {
		// une stratégie inconnue est relue par un appel imbriqué sur le même flux : on réutilise alors son
		// tampon, sans le fermer
		final boolean imbrique = reader instanceof EntreeBinaire;
		EtatLecture etat = ETATS.get();
		if (etat.enUsage) // appel imbriqué : état à part
			etat = new EtatLecture();
		etat.enUsage = true;
		try {
			final EntreeBinaire in;
			if (imbrique)
				in = (EntreeBinaire) reader;
			else {
				in = etat.entree;
				in.reinitialise(reader);
			}
			try {
				final BinaryUnmarshaller<U> w = new BinaryUnmarshaller<U>(in, entity, etat) {
				};
				return w.parse();
			} finally {
				if (!imbrique)
					in.close();
			}
		} catch (UnmarshallExeption | FabriqueInstantiationException | IOException | IllegalAccessException
				| ClassNotFoundException | NotImplementedSerializeException | InstanciationException
				| EntityManagerImplementationException | SetValueException e) {
			LOGGER.error("Impossible de désérialiser", e);
			throw new UnmarshallExeption("Impossible de désérialiser", e);
		} finally {
			etat.libere();
		}
	}

	/**
	 * Tables et tampon d'une désérialisation, réutilisés d'un appel à l'autre sur le même thread : on évite de les
	 * réallouer et de les agrandir à chaque graphe. Elles sont vidées après usage (aucun objet lu n'est retenu).
	 */
	private static final class EtatLecture {
		private final ArrayDeque<ActionBinaryObject<?>> actionsObjetLibres = new ArrayDeque<>();
		private final TableParId<Class<?>> dicoSmallIdToClazz = new TableParId<>();
		private final TableParId<Date> dicoSmallIdToDate = new TableParId<>();
		private final TableParId<Object> dicoSmallIdToObject = new TableParId<>();
		private final TableParId<String> dicoSmallIdToString = new TableParId<>();
		private final TableParId<UUID> dicoSmallIdToUUID = new TableParId<>();
		private boolean enUsage;
		private final EntreeBinaire entree = new EntreeBinaire(null);
		private boolean[] totalementLus = new boolean[256];
		/** nombre d'objets lus par le dernier appel : partie de totalementLus à effacer. */
		private int nbObjets;

		private void libere() {
			dicoSmallIdToClazz.vide();
			dicoSmallIdToDate.vide();
			dicoSmallIdToObject.vide();
			dicoSmallIdToString.vide();
			dicoSmallIdToUUID.vide();
			if (totalementLus.length > 1 << 16)
				totalementLus = new boolean[256];
			else
				Arrays.fill(totalementLus, 0, Math.min(nbObjets + 1, totalementLus.length), false);
			nbObjets = 0;
			for (final ActionBinaryObject<?> action : actionsObjetLibres)
				action.nettoie();
			if (actionsObjetLibres.size() > 1024)
				actionsObjetLibres.clear();
			entree.reinitialise(null);
			enUsage = false;
		}
	}

	private static final ThreadLocal<EtatLecture> ETATS = ThreadLocal.withInitial(EtatLecture::new);

	/** renvoyé par {@link #litValeur} quand la valeur sera transmise plus tard par l'action empilée. */
	protected static final Object EN_ATTENTE = new Object();

	/** prototype d'action de chaque classe, résolu une fois. */
	private static final ClassValue<ActionAbstrait<?>> ACTIONS = new ClassValue<>() {
		@Override
		protected ActionAbstrait<?> computeValue(final Class<?> type) {
			ActionAbstrait<?> action = dicoTypeToAction.get(type);
			if (action == null)
				try {
					action = choisiAction(dicoTypeToAction, type);
				} catch (final NotImplementedSerializeException e) {
					throw new IllegalStateException(e);
				}
			// un prototype par classe d'objet, qui porte les champs du type
			return action instanceof ActionBinaryObject ? ActionBinaryObject.prototype(type) : action;
		}
	};

	/** actions d'objet terminées, réutilisables (une action par objet lu sinon). */
	private final ArrayDeque<ActionBinaryObject<?>> actionsObjetLibres;
	private final EtatLecture etat;
	/** au-delà, les objets sont lus par la pile d'actions : pas de débordement de pile sur un graphe profond. */
	private static final int PROFONDEUR_MAX_DIRECTE = 200;
	private static final Champ[] AUCUN_CHAMP = new Champ[0];
	/** nombre de lectures directes (litObjetDirect) imbriquées en cours. */
	private int profondeurDirecte;
	private ActionBinary<?> actionSimple;
	private Class<?> typeActionSimple;
	// dernier smallId attribué : à leur première apparition, objets, dates, chaînes et UUID
	// ne portent pas leur smallId, il est attribué ici dans l'ordre de lecture (comme à l'écriture).
	private int compteurDate = 0;
	private int compteurObjet = 0;
	private int compteurString = 0;
	private int compteurUuid = 0;
	// les smallIds de types, dates, chaînes et UUID sont attribués séquentiellement
	// à partir de 1 : on les range dans des tables indexées par smallId.
	private final TableParId<Class<?>> dicoSmallIdToClazz;
	private final TableParId<Date> dicoSmallIdToDate;
	private final TableParId<Object> dicoSmallIdToObject;
	private final TableParId<String> dicoSmallIdToString;
	private final TableParId<UUID> dicoSmallIdToUUID;
	private final EntreeBinaire input;
	/** objets totalement désérialisés, indexés par smallId. */
	private boolean[] totalementLus;

	protected int profondeur = 0;

	private final StrategieDeSerialisation strategie;

	protected BinaryUnmarshaller(final EntreeBinaire input, final EntityManager entity)
			throws FabriqueInstantiationException, IOException, UnmarshallExeption {
		this(input, entity, new EtatLecture());
	}

	private BinaryUnmarshaller(final EntreeBinaire input, final EntityManager entity, final EtatLecture etat)
			throws FabriqueInstantiationException, IOException, UnmarshallExeption {
		super(entity);
		this.input = input;
		this.etat = etat;
		actionsObjetLibres = etat.actionsObjetLibres;
		dicoSmallIdToClazz = etat.dicoSmallIdToClazz;
		dicoSmallIdToDate = etat.dicoSmallIdToDate;
		dicoSmallIdToObject = etat.dicoSmallIdToObject;
		dicoSmallIdToString = etat.dicoSmallIdToString;
		dicoSmallIdToUUID = etat.dicoSmallIdToUUID;
		totalementLus = etat.totalementLus;
		strategie = readStrategie();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected <U> ActionAbstrait getAction(final Class<U> type) throws NotImplementedSerializeException {
		if (type == null)
			return null;
		try {
			final ActionAbstrait<?> prototype = ACTIONS.get(type);
			if (prototype instanceof ActionBinaryObject) {
				final ActionBinaryObject action = actionsObjetLibres.isEmpty()
						? (ActionBinaryObject) prototype.getNewInstance((Class) type, this)
						: actionsObjetLibres.pop();
				action.recycle(type, (ActionBinaryObject) prototype, this);
				return action;
			}
			return prototype.getNewInstance((Class) type, this);
		} catch (final IllegalStateException e) {
			if (e.getCause() instanceof NotImplementedSerializeException)
				throw (NotImplementedSerializeException) e.getCause();
			throw e;
		}
	}

	@Override
	protected Map<Class<?>, ActionAbstrait<?>> getdicoTypeToAction() {
		return dicoTypeToAction;
	}

	protected Object getObject(final int smallId) {
		return dicoSmallIdToObject.get(smallId);
	}

	int getProfondeur() {
		return profondeur;
	}

	StrategieDeSerialisation getStrategie() {
		return strategie;
	}

	protected void integreObject(final Object obj) throws IllegalAccessException, EntityManagerImplementationException,
			InstanciationException, SetValueException {
		pileAction.pop();
		integreObjectDirectement(obj);
	}

	@SuppressWarnings("unchecked")
	private void integreObjectDirectement(final Object obj) throws IllegalAccessException,
			EntityManagerImplementationException, InstanciationException, SetValueException {
		final ActionAbstrait<?> action = getActionEnCours();
		if (action == null)
			this.obj = (T) obj;
		else
			integreObjet(action, null, obj);
	}

	/** Rend une action d'objet terminée réutilisable. */
	public void libere(final ActionBinaryObject<?> action) {
		actionsObjetLibres.push(action);
	}

	protected boolean isDejaTotalementDeSerialise(final int smallId) {
		return smallId >= 0 && smallId < totalementLus.length && totalementLus[smallId];
	}

	protected boolean isDejaVu(final int smallId) {
		return dicoSmallIdToObject.contient(smallId);
	}

	protected boolean isDejaVuClazz(final short smallIdType) {
		return dicoSmallIdToClazz.contient(smallIdType);
	}

	protected void litObject(final FieldInformations fieldInformations)
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		final Object valeur = litValeur(fieldInformations);
		if (valeur != EN_ATTENTE)
			integreObjectDirectement(valeur);
	}

	/**
	 * Lit la valeur suivante du flux. Une valeur complète (type simple, chaîne, date, enum, référence à un objet
	 * déjà lu, BigDecimal...) est renvoyée directement ; sinon l'action qui la lira est empilée et
	 * {@link #EN_ATTENTE} est renvoyé : elle transmettra la valeur à l'action en cours une fois lue.
	 */
	protected Object litValeur(final FieldInformations fieldInformations)
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		final int nature = fieldInformations.getNaturePrimitive();
		if (nature != AccesChamp.AUCUNE) // type déclaré primitif : pas d'en-tête
			return Primitifs.lit(input, nature);
		final Header header = Header.getHeader(readByte());
		switch (header.categorie) {
		case Header.SIMPLE:
			return ((HeaderSimpleType<?>) header).read(input);
		case Header.COURANT:
			return litObjectCourant((HeaderTypeCourant) header);
		case Header.ENUM:
			return litObjectEnum(fieldInformations, header);
		default:
			return litObjetComplexe(fieldInformations, header);
		}
	}

	/** Lit la valeur d'un champ primitif (sans en-tête) et l'écrit dans l'objet, sans boxing. */
	protected boolean litPrimitif(final Champ champ, final Object objet) throws IOException {
		Primitifs.litEtAffecte(input, champ.getNaturePrimitive(), champ.getAcces(), objet);
		return true;
	}

	private Object litObjectCourant(final HeaderTypeCourant headerTypeCourant)
			throws IOException, UnmarshallExeption {
		final Class<?> clazz = headerTypeCourant.getTypeCourant();
		final boolean nouveau = headerTypeCourant.isNouveau();
		final int smallId = nouveau ? 0 : headerTypeCourant.readSmallId(input, 0);
		if (clazz == String.class) {
			if (!nouveau)
				return litReference(dicoSmallIdToString, smallId);
			final String string = readUTF();
			stockStringSmallId(string, ++compteurString);
			return string;
		}
		if (clazz == Date.class) {
			if (!nouveau)
				return litReference(dicoSmallIdToDate, smallId);
			final Date date = new Date(readLong());
			stockDateSmallId(date, ++compteurDate);
			return date;
		}
		if (!nouveau)
			return litReference(dicoSmallIdToUUID, smallId);
		final UUID id = readUUID();
		stockUuidSmallId(id, ++compteurUuid);
		return id;
	}

	private static <V> V litReference(final TableParId<V> table, final int smallId) throws UnmarshallExeption {
		final V valeur = table.get(smallId);
		if (valeur == null)
			throw new UnmarshallExeption("référence inconnue : " + smallId);
		return valeur;
	}

	private Object litObjectEnum(final FieldInformations fi, final Header header)
			throws ClassNotFoundException, IOException, UnmarshallExeption {
		Class<?> type = fi.getValueType();
		if (!header.isTypeDevinable()) { // un type devinable n'est pas numéroté (voir l'écriture)
			final short smallIdType = header.getSmallIdType(input);
			type = classeDuType(smallIdType);
		}
		// symétrique de serialisation.binary.actions.ActionBinaryEnum : ordinal non signé sur 1 octet, ou sur 2
		final Object[] enums = TypeExtension.getEnumConstants(type);
		return enums[enums.length < 254 ? readByte() & 0xFF : readShort() & 0xFFFF];
	}

	private Object litObjetComplexe(final FieldInformations fieldInformations, final Header header)
			throws NotImplementedSerializeException, ClassNotFoundException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		if (!header.isNouveau())
			return litReferenceObjet(fieldInformations, header);
		// il faut trouver le type de l'objet
		final Class<?> type = header.isTypeDevinable() ? typeDevine(fieldInformations) : typeLu(header);
		// même numérotation qu'à l'écriture (BinaryMarshaller.smallIdObjet) ; une valeur immuable n'a pas d'identité
		final int smallId = TypeExtension.isValeurImmuableBinaire(type) ? -1 : ++compteurObjet;
		etat.nbObjets = compteurObjet;
		return litObjet(fieldInformations, type, smallId);
	}

	/** Type déduit du champ : il n'est pas numéroté (voir l'écriture). */
	private static Class<?> typeDevine(final FieldInformations fieldInformations) {
		return fieldInformations.getValueType();
	}

	private Class<?> typeLu(final Header header) throws IOException, UnmarshallExeption, ClassNotFoundException {
		final short smallIdType = header.getSmallIdType(input);
		return classeDuType(smallIdType);
	}

	/**
	 * Classe d'un numéro de type : numéro fixe (TypesPredefinis), type déjà rencontré, ou nouveau type dont le nom
	 * suit dans le flux.
	 */
	private Class<?> classeDuType(final short smallIdType)
			throws IOException, UnmarshallExeption, ClassNotFoundException {
		if (smallIdType > 0 && smallIdType < TypesPredefinis.PREMIER_LIBRE)
			return TypesPredefinis.classe(smallIdType);
		if (!isDejaVuClazz(smallIdType))
			stockClass(getClasse(readUTF()), smallIdType);
		return dicoSmallIdToClazz.get(smallIdType);
	}

	/** Référence arrière : l'objet déjà lu, ou sa suite s'il n'a pas encore été lu entièrement. */
	private Object litReferenceObjet(final FieldInformations fieldInformations, final Header header)
			throws NotImplementedSerializeException, ClassNotFoundException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		final int smallId = header.readSmallId(input, 0);
		final Object dejaLu = dicoSmallIdToObject.get(smallId);
		if (dejaLu == null)
			throw new UnmarshallExeption("référence à un objet inconnu : " + smallId);
		if (isDejaTotalementDeSerialise(smallId))
			return dejaLu;
		return litObjet(fieldInformations, dejaLu.getClass(), smallId);
	}

	private Object litObjet(final FieldInformations fieldInformations, final Class<?> type, final int smallId)
			throws NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException,
			ClassNotFoundException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		if (type == BigDecimal.class) // valeur immuable, fréquente : lue directement (voir ActionBinaryBigDecimal)
			return ActionBinaryBigDecimal.lit(input);
		// objet neuf, peu profond, sans EntityManager : lu directement, par récursion (voir litObjetDirect)
		if (smallId > 0 && entity == null && profondeurDirecte < PROFONDEUR_MAX_DIRECTE && !isDejaVu(smallId)) {
			final ActionAbstrait<?> prototype = prototype(type);
			if (prototype instanceof ActionBinaryObject)
				return litObjetDirect(((ActionBinaryObject<?>) prototype).getChampsDuType(), fieldInformations, type,
						smallId);
			if (prototype instanceof ActionBinaryCollection)
				return litCollectionDirecte(fieldInformations, type, smallId);
			if (prototype instanceof ActionBinaryDictionary)
				return litMapDirecte(fieldInformations, type, smallId);
		}
		// une action simple (BigDecimal, BigInteger, Calendar...) lit tout dans set : elle n'est pas empilée et peut
		// être réutilisée pour la valeur suivante du même type
		final ActionBinary<?> action;
		if (type == typeActionSimple)
			action = actionSimple;
		else
			action = (ActionBinary<?>) getAction(type);
		action.set(fieldInformations, smallId);
		if (action instanceof ActionBinarySimple) {
			typeActionSimple = type;
			actionSimple = action;
			return action.valeurLue();
		}
		pileAction.push(action);
		return EN_ATTENTE;
	}

	/**
	 * Lit un objet neuf directement, par un appel récursif, sans action ni pile : même lecture que
	 * {@link ActionBinaryObject} pour un objet vu pour la première fois (sans EntityManager). La récursion est bornée
	 * par {@link #PROFONDEUR_MAX_DIRECTE} : au-delà, les objets passent par la pile d'actions (graphes profonds).
	 */
	private Object litObjetDirect(final ChampsDuType champsDuType, final FieldInformations fieldInformations,
			final Class<?> type, final int smallId)
			throws NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException,
			ClassNotFoundException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		final int profondeurParent = profondeur;
		final int profondeurObjet = profondeurParent + 1; // comme une action créée à cette profondeur
		profondeur = profondeurObjet;
		profondeurDirecte++;
		try {
			final Champ champId = champsDuType.getChampId();
			Object objet = null;
			if (champId.isFakeId()) {
				objet = newInstance(type);
				stockObjectSmallId(smallId, objet);
			}
			final Champ[] champs;
			final boolean deserialiseId = !champId.isFakeId();
			final boolean tout = strategie.serialiseTout(profondeurObjet, fieldInformations);
			if (tout)
				champs = deserialiseId ? champsDuType.getTableauIdEnTete() : champsDuType.getTableauSaufId();
			else
				champs = deserialiseId ? champsDuType.getTableauIdSeul() : AUCUN_CHAMP;
			if (tout && deserialiseId && champs.length > 1) {
				// cas courant : l'id, puis tous les autres champs par le lecteur généré pour la classe
				final LecteurChamps lecteur = lecteur(champsDuType, type);
				if (lecteur != null) {
					final Object id = litValeurComplete(champId);
					objet = getObject(id.toString(), type);
					stockObjectSmallId(smallId, objet);
					champId.affecte(objet, id, null);
					setDejaTotalementDeSerialise(smallId);
					litChamps(lecteur, objet, champs);
					return objet;
				}
			}
			boolean marqueTotal = false;
			for (final Champ champ : champs) {
				if (!marqueTotal && champ != champId) {
					setDejaTotalementDeSerialise(smallId);
					marqueTotal = true;
				}
				if (champ.getNaturePrimitive() != AccesChamp.AUCUNE && champ != champId && litPrimitif(champ, objet))
					continue;
				final Object valeur = litValeurComplete(champ);
				if (champ == champId) {
					objet = getObject(valeur.toString(), type);
					stockObjectSmallId(smallId, objet);
				}
				champ.affecte(objet, valeur, champ.isFakeId() ? getDicoObjToFakeId() : null);
			}
			return objet;
		} finally {
			profondeurDirecte--;
			profondeur = profondeurParent;
		}
	}

	private static LecteurChamps lecteur(final ChampsDuType champsDuType, final Class<?> type) {
		Object lecteur = champsDuType.getLecteurBinaire();
		if (lecteur == null) {
			lecteur = GenerateurSerialiseurs.lecteur(type, champsDuType.getTableauIdEnTete(), 1,
					BinaryUnmarshaller.class);
			if (lecteur == null)
				lecteur = Boolean.FALSE; // génération impossible : chemin générique
			champsDuType.setLecteurBinaire(lecteur);
		}
		return lecteur instanceof LecteurChamps ? (LecteurChamps) lecteur : null;
	}

	private void litChamps(final LecteurChamps lecteur, final Object objet, final Champ[] champs)
			throws IOException, UnmarshallExeption, NotImplementedSerializeException, InstanciationException,
			ClassNotFoundException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		try {
			lecteur.lit(objet, this, champs);
		} catch (IOException | UnmarshallExeption | NotImplementedSerializeException | InstanciationException
				| ClassNotFoundException | IllegalAccessException | EntityManagerImplementationException
				| SetValueException | RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new UnmarshallExeption("lecture impossible", e);
		}
	}

	/*
	 * Lecture d'un champ, appelée par les lecteurs générés (utils.champ.GenerateurSerialiseurs) : un primitif est
	 * lu sans en-tête, une autre valeur complètement (sous-objets compris).
	 */

	public Object litObjet(final FieldInformations champ)
			throws NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException,
			ClassNotFoundException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		return litValeurComplete(champ);
	}

	public int litInt(final FieldInformations champ) throws IOException {
		return Primitifs.unzigzag(input.readVarInt());
	}

	public long litLong(final FieldInformations champ) throws IOException {
		return Primitifs.unzigzag(input.readVarLong());
	}

	public double litDouble(final FieldInformations champ) throws IOException {
		return input.readDouble();
	}

	public float litFloat(final FieldInformations champ) throws IOException {
		return input.readFloat();
	}

	public boolean litBoolean(final FieldInformations champ) throws IOException {
		return input.readByte() != 0;
	}

	public byte litByte(final FieldInformations champ) throws IOException {
		return input.readByte();
	}

	public short litShort(final FieldInformations champ) throws IOException {
		return (short) Primitifs.unzigzag(input.readVarInt());
	}

	public char litChar(final FieldInformations champ) throws IOException {
		return (char) input.readVarInt();
	}

	/** Lecture directe d'une collection vue pour la première fois : même lecture que ActionBinaryCollection. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object litCollectionDirecte(final FieldInformations fieldInformations, final Class<?> type,
			final int smallId)
			throws NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException,
			ClassNotFoundException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		final int profondeurParent = profondeur;
		profondeur = profondeurParent + 1;
		profondeurDirecte++;
		try {
			final Collection collection = ActionBinaryCollection.nouvelleCollection(type, fieldInformations);
			stockObjectSmallId(smallId, collection);
			if (strategie.serialiseTout(profondeur, fieldInformations))
				setDejaTotalementDeSerialise(smallId);
			final FakeChamp element = fieldInformations.getChampParametre(FieldInformations.ELEMENT);
			for (int i = readVarInt(); i > 0; i--)
				collection.add(litValeurComplete(element));
			return collection;
		} finally {
			profondeurDirecte--;
			profondeur = profondeurParent;
		}
	}

	/** Lecture directe d'une map vue pour la première fois : même lecture que ActionBinaryDictionary. */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object litMapDirecte(final FieldInformations fieldInformations, final Class<?> type, final int smallId)
			throws NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException,
			ClassNotFoundException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		final int profondeurParent = profondeur;
		profondeur = profondeurParent + 1;
		profondeurDirecte++;
		try {
			final Map map = (Map) ActionBinaryDictionary.nouvelleMap(type, fieldInformations);
			stockObjectSmallId(smallId, map);
			if (strategie.serialiseTout(profondeur, fieldInformations))
				setDejaTotalementDeSerialise(smallId);
			final FakeChamp cle = fieldInformations.getChampParametre(FieldInformations.CLE);
			final FakeChamp valeur = fieldInformations.getChampParametre(FieldInformations.VALEUR);
			for (int i = readVarInt(); i > 0; i--) {
				final Object k = litValeurComplete(cle);
				map.put(k, litValeurComplete(valeur));
			}
			return map;
		} finally {
			profondeurDirecte--;
			profondeur = profondeurParent;
		}
	}

	/** Lit une valeur et, si elle a été confiée à une action, la termine (lecture directe). */
	private Object litValeurComplete(final FieldInformations fieldInformations)
			throws NotImplementedSerializeException, IOException, UnmarshallExeption, InstanciationException,
			ClassNotFoundException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		final Object valeur = litValeur(fieldInformations);
		return valeur == EN_ATTENTE ? termineValeurEnAttente() : valeur;
	}

	/**
	 * Pendant une lecture directe, une valeur a été confiée à une action (collection, map, objet profond...) : on la
	 * fait tourner jusqu'au bout, avec une action réceptrice glissée sous elle pour recueillir la valeur.
	 */
	private Object termineValeurEnAttente()
			throws ClassNotFoundException, NotImplementedSerializeException, IOException, UnmarshallExeption,
			InstanciationException, IllegalAccessException, EntityManagerImplementationException, SetValueException {
		final ActionAbstrait<?> action = pileAction.pop();
		final ActionReceptrice receptrice = new ActionReceptrice(this);
		pileAction.push(receptrice);
		pileAction.push(action);
		final int profondeurAvant = profondeur;
		while (pileAction.peek() != receptrice) {
			final ActionBinary<?> actionEnCours = (ActionBinary<?>) pileAction.peek();
			profondeur = actionEnCours.getProfondeur();
			actionEnCours.deserialisePariellement();
		}
		profondeur = profondeurAvant;
		pileAction.pop();
		return receptrice.valeur;
	}

	/** Reçoit la valeur d'une action terminée pendant une lecture directe (voir termineValeurEnAttente). */
	private static final class ActionReceptrice extends ActionBinary<Object> {
		private Object valeur;

		private ActionReceptrice(final BinaryUnmarshaller<?> unmarshaller) {
			super(Object.class, unmarshaller);
		}

		@Override
		protected void deserialisePariellement() {
			throw new IllegalStateException("action réceptrice : ne lit rien");
		}

		@SuppressWarnings("rawtypes")
		@Override
		public <U> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected void initialise() {
			// rien
		}

		@Override
		protected <W> void integreObjet(final String nom, final W objet) {
			valeur = objet;
		}
	}

	/** prototype d'action d'une classe (voir ACTIONS). */
	private static ActionAbstrait<?> prototype(final Class<?> type) throws NotImplementedSerializeException {
		try {
			return ACTIONS.get(type);
		} catch (final IllegalStateException e) {
			if (e.getCause() instanceof NotImplementedSerializeException)
				throw (NotImplementedSerializeException) e.getCause();
			throw e;
		}
	}

	private T parse()
			throws IllegalAccessException, ClassNotFoundException, IOException, NotImplementedSerializeException,
			UnmarshallExeption, InstanciationException, EntityManagerImplementationException, SetValueException {
		final FakeChamp fc = RACINE;
		litObject(fc);
		while (!pileAction.isEmpty()) {
			final ActionBinary<?> actionEnCours = (ActionBinary<?>) getActionEnCours();
			profondeur = actionEnCours.getProfondeur();
			((ActionBinary<?>) getActionEnCours()).deserialisePariellement();
		}
		return obj;
	}

	protected boolean readBoolean() throws IOException {
		return input.readBoolean();
	}

	protected byte readByte() throws IOException {
		return input.readByte();
	}

	protected char readChar() throws IOException {
		return input.readChar();
	}

	protected double readDouble() throws IOException {
		return input.readDouble();
	}

	protected float readFloat() throws IOException {
		return input.readFloat();
	}

	protected int readInt() throws IOException {
		return input.readInt();
	}

	protected long readLong() throws IOException {
		return input.readLong();
	}

	protected short readShort() throws IOException {
		return input.readShort();
	}

	private StrategieDeSerialisation readStrategie() throws IOException, UnmarshallExeption {
		final byte firstByte = readByte();
		final StrategieDeSerialisation strat = Constants.getStrategie(firstByte);
		if (strat != null)
			return strat;
		return fromBinary(input); // TODO : verifier que le input n'est pas fermé apres lecture de la strategie
	}

	protected String readUTF() throws IOException {
		return input.readString();
	}

	protected int readVarInt() throws IOException {
		return input.readVarInt();
	}

	/** @return l'entrée binaire (pour les lectures directes des actions). */
	public EntreeBinaire getEntree() {
		return input;
	}

	protected byte[] readBytes(final int taille) throws IOException {
		final byte[] octets = new byte[taille];
		input.readFully(octets);
		return octets;
	}

	private UUID readUUID() throws IOException {
		// symétrique de ActionBinaryUUID : bits de poids fort puis de poids faible
		final long mostSigBits = input.readLong();
		final long leastSigBits = input.readLong();
		return new UUID(mostSigBits, leastSigBits);
	}

	protected void setDejaTotalementDeSerialise(final int smallId) {
		final boolean[] t = totalementLus;
		if (smallId >= 0 && smallId < t.length)
			t[smallId] = true;
		else if (smallId >= 0)
			agranditTotalementLus(smallId);
	}

	private void agranditTotalementLus(final int smallId) {
		totalementLus = Arrays.copyOf(totalementLus, Math.max(smallId + 1, totalementLus.length * 2));
		totalementLus[smallId] = true;
		etat.totalementLus = totalementLus;
	}

	private void stockClass(final Class<?> type, final short smallIdType) {
		dicoSmallIdToClazz.set(smallIdType, type);
	}

	private void stockDateSmallId(final Date date, final int smallId) {
		dicoSmallIdToDate.set(smallId, date);
	}

	protected void stockObjectSmallId(final int smallId, final Object obj) {
		if (smallId >= 0)
			dicoSmallIdToObject.set(smallId, obj);
	}

	private void stockStringSmallId(final String string, final int smallId) {
		dicoSmallIdToString.set(smallId, string);
	}

	private void stockUuidSmallId(final UUID id, final int smallId) {
		dicoSmallIdToUUID.set(smallId, id);
	}

}
