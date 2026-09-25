package io.github.giraudsa.fidelis.serialisation.binary;

import java.lang.System.Logger.Level;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.RandomAccess;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.ConcurrentHashMap;


import io.github.giraudsa.fidelis.annotations.TypeRelation;
import io.github.giraudsa.fidelis.exception.MarshallExeption;
import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.serialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryArrayType;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryAtomicIntegerArray;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryAtomicLongArray;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryBitSet;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryCalendar;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryCollectionType;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryCurrency;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryDate;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryDictionaryType;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryEnum;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryInetAddress;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryObject;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryString;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryStringBuffer;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryStringBuilder;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryUUID;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryUri;
import io.github.giraudsa.fidelis.serialisation.binary.actions.ActionBinaryUrl;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryAtomicBoolean;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryAtomicInteger;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryAtomicLong;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryBigDecimal;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryBigInteger;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryBoolean;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryByte;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryChar;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryDouble;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryFloat;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryInteger;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryLocale;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryLong;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryShort;
import io.github.giraudsa.fidelis.serialisation.binary.actions.simple.ActionBinaryVoid;
import io.github.giraudsa.fidelis.strategie.StrategieDeSerialisation;
import io.github.giraudsa.fidelis.strategie.StrategieParComposition;
import io.github.giraudsa.fidelis.strategie.StrategieSerialisationComplete;
import io.github.giraudsa.fidelis.utils.Constants;
import io.github.giraudsa.fidelis.utils.EgaliteIntMap;
import io.github.giraudsa.fidelis.utils.IdentiteIntMap;
import io.github.giraudsa.fidelis.utils.TypeExtension;
import io.github.giraudsa.fidelis.utils.TypeExtension.ChampsDuType;
import io.github.giraudsa.fidelis.utils.champ.EcrivainChamps;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.Header;
import io.github.giraudsa.fidelis.utils.headers.TypesPredefinis;
import io.github.giraudsa.fidelis.utils.io.Primitifs;
import io.github.giraudsa.fidelis.utils.io.SortieBinaire;

public class BinaryMarshaller extends Marshaller {
	private static final Map<Class<?>, ActionAbstrait<?>> dicoTypeToAction = new ConcurrentHashMap<>();
	/** champ fictif de la racine du graphe (sans état propre : partagé). */
	private static final FakeChamp RACINE = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);
	private static final System.Logger LOGGER = System.getLogger(BinaryMarshaller.class.getName());
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
			try {
				v.marshall(obj);
			} finally {
				v.rendTables();
			}
			etat.sortie.flush();
		} catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | NotImplementedSerializeException e) {
			LOGGER.log(Level.ERROR, messageErreur, e);
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

	/**
	 * Plan d'écriture d'une classe, résolu une fois : de quoi écrire directement un objet (écrivain généré) ou une
	 * collection vus pour la première fois, sans passer par l'aiguillage générique des actions.
	 */
	static final class Plan {
		private static final int AUTRE = 0;
		private static final int OBJET = 1;
		private static final int COLLECTION = 2;
		/** action de la classe (null si aucune : NotImplementedSerializeException au moment d'écrire). */
		final ActionAbstrait<?> action;
		private final ChampsDuType champs;
		private final EcrivainChamps ecrivain;
		/** l'action écrit sa valeur sans rien empiler (voir ActionBinary.isFeuille). */
		final boolean feuille;
		/** configuration des champs pour laquelle le plan a été calculé (voir TypeExtension.getGeneration). */
		private final int generation;
		private final int genre;

		private Plan(final ActionAbstrait<?> action, final int genre, final ChampsDuType champs,
				final EcrivainChamps ecrivain) {
			this.action = action;
			feuille = action instanceof ActionBinary && ((ActionBinary<?>) action).isFeuille();
			this.genre = genre;
			this.champs = champs;
			this.ecrivain = ecrivain;
			generation = TypeExtension.getGeneration();
		}
	}

	private static final ClassValue<Plan> PLANS = new ClassValue<>() {
		@Override
		protected Plan computeValue(final Class<?> type) {
			// classe sous laquelle l'objet est sérialisé (celle de l'enum pour une constante avec corps)
			final Class<?> classe = TypeExtension.isEnum(type) && !type.isEnum() ? type.getSuperclass() : type;
			ActionAbstrait<?> action;
			try {
				action = ACTIONS.get(classe);
			} catch (final IllegalStateException e) {
				action = null; // type non géré : l'erreur sera levée par le chemin générique
			}
			// valeur immuable, proxy Hibernate... : chemin générique
			if (action == null || TypeExtension.isEnum(type) || TypeExtension.isValeurImmuableBinaire(type)
					|| TypeExtension.isHibernate(type))
				return new Plan(action, Plan.AUTRE, null, null);
			if (action instanceof ActionBinaryObject) {
				final ChampsDuType champs = TypeExtension.getChampsDuType(type);
				final EcrivainChamps ecrivain = champs.getChampId().isFakeId() ? null
						: ActionBinaryObject.ecrivain(champs, type);
				return new Plan(action, ecrivain == null ? Plan.AUTRE : Plan.OBJET, champs, ecrivain);
			}
			if (action instanceof ActionBinaryCollectionType)
				return new Plan(action, Plan.COLLECTION, null, null);
			return new Plan(action, Plan.AUTRE, null, null);
		}
	};

	/**
	 * Écrit directement un objet (par son écrivain généré) ou une collection vus pour la première fois : mêmes octets
	 * que le chemin des actions (ActionBinary.marshall, ActionBinaryObject, ActionBinaryCollectionType), sans
	 * l'aiguillage générique.
	 *
	 * @return false si le chemin direct ne s'applique pas (rien n'a été écrit) : il faut passer par les actions.
	 */
	/** @return le plan d'écriture de la classe de la valeur (non nulle). */
	static Plan plan(final Object valeur) {
		return PLANS.get(valeur.getClass());
	}

	@SuppressWarnings("rawtypes")
	boolean ecritDirect(final Plan plan, final Object valeur, final FieldInformations champ)
			throws NotImplementedSerializeException, MarshallExeption {
		if (plan.genre == Plan.AUTRE || plan.generation != TypeExtension.getGeneration())
			return false;
		// un objet dont la stratégie ne veut pas tous les champs passe par le chemin générique
		if (plan.genre == Plan.OBJET && !strategie.serialiseTout(profondeur + 1, champ))
			return false;
		final int id = smallIdObjet(valeur);
		if (id > 0)
			return false; // déjà vu : le chemin générique retrouve son smallId et écrit la référence
		final int smallId = -id;
		try {
			ecritEnTeteNouveau(champ.isTypeDevinable(valeur), valeur.getClass(), smallId);
			++profondeur;
			if (plan.genre == Plan.OBJET || strategie.serialiseTout(profondeur, champ))
				totalementSerialises.set(smallId);
			final int base = hautPile;
			recursion++;
			try {
				if (plan.genre == Plan.OBJET)
					plan.ecrivain.ecrit(valeur, this, plan.champs.getTableauChamps());
				else {
					final Collection collection = (Collection) valeur;
					final FakeChamp element = champ.getChampParametre(FieldInformations.ELEMENT);
					output.writeVarInt(collection.size());
					if (collection instanceof List && collection instanceof RandomAccess) {
						// liste à accès direct : par index, sans itérateur
						final List liste = (List) collection;
						final int taille = liste.size();
						for (int i = 0; i < taille; i++)
							ActionBinary.ecritOuDiffere(this, liste.get(i), element);
					} else
						for (final Object e : collection)
							ActionBinary.ecritOuDiffere(this, e, element);
				}
				empileDifferes();
				videPileJusqua(base);
			} finally {
				recursion--;
			}
			--profondeur;
			return true;
		} catch (NotImplementedSerializeException | MarshallExeption | RuntimeException e) {
			throw e;
		} catch (final Exception e) {
			throw new MarshallExeption(e);
		}
	}

	/**
	 * En-tête d'une première apparition. Un type devinable (celui du champ) n'est pas écrit et ne reçoit pas de
	 * numéro : seuls les types écrits sont numérotés, dans l'ordre (même règle à la lecture).
	 */
	void ecritEnTeteNouveau(final boolean typeDevinable, final Class<?> typeObj, final int smallId)
			throws IOException {
		if (typeDevinable) {
			Header.getHeader(false, true, smallId, (short) 0).write(output, smallId, (short) 0, true, typeObj);
			return;
		}
		final int idType = smallIdType(typeObj);
		final short smallIdType = (short) Math.abs(idType);
		Header.getHeader(false, false, smallId, smallIdType).write(output, smallId, smallIdType, idType > 0,
				typeObj);
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

	private int compteur = 1;
	private int compteurDate = 1;
	private int compteurString = 1;
	private short compteurType = TypesPredefinis.PREMIER_LIBRE;
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
	/**
	 * Au-delà de cette profondeur de récursion, les sous-objets sont mis sur la pile au lieu d'être écrits par un
	 * appel récursif : pas de débordement de pile sur un graphe profond.
	 */
	static final int RECURSION_MAX = 200;
	/** nombre d'écritures récursives d'objets imbriquées en cours. */
	int recursion;

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

	/**
	 * Chaîne d'un champ dont les valeurs ne se répètent pas : écrite comme nouvelle sans être cherchée ni gardée
	 * (le lecteur la numérote comme toute nouvelle chaîne). @return l'opposé du smallId attribué.
	 */
	protected int nouveauSmallIdStringSansDedoublonnage() {
		return -compteurString++;
	}

	protected int smallIdType(final Class<?> type) {
		final short predefini = TypesPredefinis.numero(type);
		if (predefini > 0)
			return predefini; // numéro fixe, connu du lecteur : « déjà vu », le nom n'est pas écrit
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

	/*
	 * Écriture d'un champ, appelée par les écrivains générés (io.github.giraudsa.fidelis.utils.champ.GenerateurSerialiseurs) et par
	 * ActionBinaryObject : un primitif est écrit sans en-tête s'il peut l'être tout de suite, sinon mis en attente
	 * (il sera écrit avec le même codage par l'action de son type enveloppe).
	 */

	public void ecritObjet(final Object valeur, final FieldInformations champ)
			throws NotImplementedSerializeException, MarshallExeption {
		ActionBinary.ecritOuDiffere(this, valeur, champ);
	}

	public void ecritInt(final int valeur, final FieldInformations champ) throws IOException {
		if (debutAttente < 0)
			output.writeVarInt(Primitifs.zigzag(valeur));
		else
			differe(valeur, champ);
	}

	public void ecritLong(final long valeur, final FieldInformations champ) throws IOException {
		if (debutAttente < 0)
			output.writeVarLong(Primitifs.zigzag(valeur));
		else
			differe(valeur, champ);
	}

	public void ecritDouble(final double valeur, final FieldInformations champ) throws IOException {
		if (debutAttente < 0)
			output.writeDouble(valeur);
		else
			differe(valeur, champ);
	}

	public void ecritFloat(final float valeur, final FieldInformations champ) throws IOException {
		if (debutAttente < 0)
			output.writeFloat(valeur);
		else
			differe(valeur, champ);
	}

	public void ecritBoolean(final boolean valeur, final FieldInformations champ) throws IOException {
		if (debutAttente < 0)
			output.write(valeur ? 1 : 0);
		else
			differe(valeur, champ);
	}

	public void ecritByte(final byte valeur, final FieldInformations champ) throws IOException {
		if (debutAttente < 0)
			output.write(valeur);
		else
			differe(valeur, champ);
	}

	public void ecritShort(final short valeur, final FieldInformations champ) throws IOException {
		if (debutAttente < 0)
			output.writeVarInt(Primitifs.zigzag(valeur));
		else
			differe(valeur, champ);
	}

	public void ecritChar(final char valeur, final FieldInformations champ) throws IOException {
		if (debutAttente < 0)
			output.writeVarInt(valeur);
		else
			differe(valeur, champ);
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

	int hautPile() {
		return hautPile;
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
		videPileJusqua(0);
	}

	/**
	 * Traite les valeurs empilées au-dessus de base (dans l'ordre), y compris celles qu'elles empilent à leur tour.
	 */
	void videPileJusqua(final int base)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
			IOException, NotImplementedSerializeException, MarshallExeption {
		while (hautPile > base) {
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
