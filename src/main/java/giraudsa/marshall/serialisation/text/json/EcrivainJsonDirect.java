package giraudsa.marshall.serialisation.text.json;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.ActionAbstrait;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonArrayType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonCollectionType;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonDictionary;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonObject;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithQuote;
import giraudsa.marshall.serialisation.text.json.actions.ActionJsonSimpleWithoutQuote;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonBoolean;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonDate;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonInteger;
import giraudsa.marshall.serialisation.text.json.actions.simple.ActionJsonString;
import utils.Constants;
import utils.EntityManager;
import utils.TypeExtension;
import utils.champ.AccesChamp;
import utils.champ.Champ;
import utils.champ.FakeChamp;
import utils.champ.EcrivainChamps;
import utils.champ.FieldInformations;
import utils.champ.GenerateurSerialiseurs;
import utils.io.SortieTexte;

/**
 * Écriture JSON directe des valeurs courantes (objets, collections, maps, chaînes, nombres, booléens, dates, enums,
 * UUID) : mêmes étapes, dans le même ordre, que les actions (ActionJsonObject, ActionJsonCollectionType,
 * ActionJsonDictionary, ActionJsonSimple...), donc le même texte, mais sans comportement alloué, sans recherche
 * d'action par valeur ni champ d'éléments reconstruit pour chaque collection. Les autres valeurs, et au-delà d'une
 * profondeur, sont écrites par leur action.
 */
public final class EcrivainJsonDirect {

	/** au-delà, les valeurs imbriquées sont confiées aux actions (qui passent par une pile explicite). */
	private static final int PROFONDEUR_MAX = 100;
	/** mode données : pas de repli possible ; au-delà, le graphe est sans doute cyclique. */
	private static final int PROFONDEUR_DONNEES_MAX = 1000;

	private static final int AUTRE = 0;
	private static final int CHAINE = 1;
	private static final int ENTIER = 2;
	private static final int BOOLEEN = 3;
	/** nombre sans guillemets, enveloppé si son type n'est pas devinable */
	private static final int NOMBRE = 4;
	/** texte entre guillemets (enum, UUID, caractère...), enveloppé si son type n'est pas devinable */
	private static final int TEXTE = 5;
	private static final int DATE = 6;
	private static final int OBJET = 7;
	private static final int COLLECTION = 8;
	private static final int MAP = 9;
	private static final int TABLEAU = 10;

	/** famille d'écriture de chaque classe, d'après son action. */
	private static final ClassValue<Integer> GENRES = new ClassValue<>() {
		@Override
		protected Integer computeValue(final Class<?> type) {
			final ActionAbstrait<?> action;
			try {
				action = JsonMarshaller.actionDe(TypeExtension.isEnum(type) && !type.isEnum() ? type.getSuperclass()
						: type);
			} catch (final RuntimeException | NotImplementedSerializeException e) {
				return AUTRE;
			}
			final Class<?> k = action.getClass();
			if (k == ActionJsonString.class)
				return CHAINE;
			if (k == ActionJsonInteger.class)
				return ENTIER;
			if (k == ActionJsonBoolean.class)
				return BOOLEEN;
			if (k == ActionJsonDate.class)
				return DATE;
			if (k == ActionJsonObject.class)
				return OBJET;
			if (k == ActionJsonCollectionType.class)
				return COLLECTION;
			if (k == ActionJsonDictionary.class)
				return MAP;
			if (k == ActionJsonArrayType.class)
				return TABLEAU;
			if (k == ActionJsonSimpleWithoutQuote.class)
				// AtomicBoolean : texte échappé, laissé à son action
				return Number.class.isAssignableFrom(type) ? NOMBRE : AUTRE;
			if (k == ActionJsonSimpleWithQuote.class)
				return TEXTE;
			return AUTRE;
		}
	};

	private final JsonMarshaller m;
	private final SortieTexte sortie;
	private final String[] remplacements = ActionJson.remplacements();
	private final boolean universel;
	private final boolean ecritType;
	/** mode données : ni type, ni identité (chaque objet écrit en entier à chaque occurrence), ni faux id. */
	private final boolean donnees;
	private final int profondeurMax;
	private final Map<Object, UUID> fakeIds;
	private final EntityManager entite;
	private int profondeur;
	/**
	 * champs des éléments par champ porteur : { élément de collection, clé, valeur } ; gardés d'une écriture à l'autre
	 * sur un même thread (les champs porteurs sont ceux des classes, ou ces champs d'éléments eux-mêmes).
	 */
	private static final ThreadLocal<IdentityHashMap<FieldInformations, FakeChamp[]>> CHAMPS_ELEMENTS = ThreadLocal
			.withInitial(IdentityHashMap::new);
	private static final int CHAMPS_ELEMENTS_MAX = 4096;
	private final IdentityHashMap<FieldInformations, FakeChamp[]> champsElements = CHAMPS_ELEMENTS.get();

	EcrivainJsonDirect(final JsonMarshaller m) {
		this.m = m;
		sortie = m.sortie();
		universel = m.idUniversel();
		ecritType = m.writeType;
		donnees = m.donnees;
		profondeurMax = donnees ? PROFONDEUR_DONNEES_MAX : PROFONDEUR_MAX;
		fakeIds = m.fakeIds();
		entite = m.getEntityManager();
	}

	private void clef(final FieldInformations fi) throws IOException {
		if (fi instanceof Champ)
			m.ecritClef((Champ) fi);
		else {
			final String nom = fi.getName();
			if (nom != null)
				m.ecritClef(nom);
		}
	}

	private boolean devinable(final Object v, final FieldInformations fi) {
		if (universel && m.dejaVu(v))
			return true;
		return fi.isTypeDevinable(v);
	}

	/**
	 * Écrit la valeur (précédée d'une virgule si demandé), comme ecritDirect puis l'action de la valeur.
	 * <p>
	 * Les valeurs simples sont écrites ici même : la méthode dépasse ainsi la taille au-delà de laquelle le JIT
	 * n'intègre pas une méthode chaude à ses appelants. Intégrée, avec la récursion objet → valeur → objet, elle
	 * produisait un code compilé démesuré (petites méthodes d'écriture plus intégrées, écriture 25 % plus lente).
	 */
	void ecrit(final Object v, final FieldInformations fi, final boolean separateur)
			throws IOException, MarshallExeption, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException {
		if (separateur)
			sortie.write(',');
		if (v == null) {
			clef(fi);
			sortie.write("null");
			return;
		}
		switch (GENRES.get(v.getClass())) {
		case CHAINE:
			clef(fi);
			m.ecritEntreGuillemets((String) v, remplacements);
			return;
		case ENTIER:
			clef(fi);
			sortie.writeLong((Integer) v);
			return;
		case BOOLEEN:
			clef(fi);
			sortie.write(((Boolean) v).booleanValue() ? "true" : "false");
			return;
		case NOMBRE:
		case TEXTE:
		case DATE: {
			// valeur simple : enveloppée {"__type":..,"__valeur":v} si son type n'est pas devinable (ActionJsonSimple)
			final boolean enveloppe = ecritType && !devinable(v, fi);
			clef(fi);
			if (enveloppe) {
				m.ouvreAccolade();
				m.ecritType(TypeExtension.getClasseASerialiser(v));
				m.writeSeparator();
				m.ecritClef(Constants.VALEUR);
			}
			if (v instanceof Integer || v instanceof Long || v instanceof Short || v instanceof Byte)
				sortie.writeLong(((Number) v).longValue());
			else if (v instanceof Double)
				sortie.writeDouble((Double) v);
			else if (v instanceof Number)
				sortie.write(v.toString());
			else if (v instanceof Date) {
				if (!m.ecritDateRapide(((Date) v).getTime()))
					m.ecritEntreGuillemets(m.formatDate().format((Date) v), remplacements);
			} else
				m.ecritEntreGuillemets(v.toString(), remplacements);
			if (enveloppe)
				m.fermeAccolade();
			return;
		}
		case OBJET:
			if (profondeur < profondeurMax) {
				ecritObjetComplet(v, fi);
				return;
			}
			break;
		case COLLECTION:
			if (profondeur < profondeurMax) {
				ecritCollection((Collection<?>) v, fi);
				return;
			}
			break;
		case MAP:
			if (profondeur < profondeurMax) {
				ecritMap((Map<?, ?>) v, fi);
				return;
			}
			break;
		case TABLEAU:
			if (profondeur < profondeurMax) {
				ecritTableau(v, fi);
				return;
			}
			break;
		default:
			// autres types (Calendar, BitSet, URI...) : par leur action (valeurs sans objet imbriqué)
			m.ecritParAction(v, fi);
			return;
		}
		if (donnees)
			throw new MarshallExeption("mode données : profondeur supérieure à " + PROFONDEUR_DONNEES_MAX
					+ " (graphe cyclique ?)");
		m.ecritParAction(v, fi);
	}

	/** Objet (ActionJsonObject) : ses champs, ou son seul id s'il est déjà écrit ou selon la stratégie. */
	private void ecritObjetComplet(final Object v, final FieldInformations fi)
			throws IOException, MarshallExeption, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException {
		final boolean nePasEcrireType = !ecritType || devinable(v, fi);
		clef(fi);
		m.ouvreAccolade();
		boolean virgule = false;
		if (!nePasEcrireType) {
			m.ecritType(TypeExtension.getClasseASerialiser(v));
			virgule = true;
		}
		final TypeExtension.ChampsDuType champsDuType = TypeExtension.getChampsDuType(v.getClass());
		if (donnees) {
			// ni identité ni stratégie : tous les champs, faux id exclu
			profondeur++;
			ecritChamps(v, champsDuType, virgule);
			profondeur--;
			m.fermeAccolade();
			return;
		}
		// la stratégie ne dépend que de la profondeur et du champ : consultée d'abord, l'objet est ensuite marqué déjà vu
		// et, si tout doit être écrit, totalement sérialisé (sans effet s'il l'était déjà), en une recherche
		final boolean strategieTout = m.serialiseTout(fi);
		final boolean serialiseTout = !m.marqueDejaVu(v, strategieTout) && strategieTout;
		profondeur++;
		if (!serialiseTout) {
			final Champ champId = champsDuType.getChampId();
			final Object id = champId.get(v, fakeIds, entite);
			if (aTraiter(id, champId))
				ecrit(id, champId, virgule);
		} else {
			ecritChamps(v, champsDuType, virgule);
		}
		profondeur--;
		m.fermeAccolade();
	}

	/** Tous les champs de l'objet, par l'écrivain généré de sa classe s'il existe (en mode données, sans faux id). */
	private void ecritChamps(final Object v, final TypeExtension.ChampsDuType champsDuType, boolean virgule)
			throws IOException, MarshallExeption, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException {
		final Champ[] champs = champsDuType.getTableauChamps();
		final EcrivainChamps ecrivain = ecrivainGenere(v.getClass(), champsDuType);
		if (ecrivain != null) {
			// champ par champ : ecritXxx(t.champ, champ), la virgule suivie ici
			final boolean virguleEnglobante = virguleCourante;
			virguleCourante = virgule;
			try {
				ecrivain.ecrit(v, this, champs);
			} catch (IOException | MarshallExeption | InstantiationException | IllegalAccessException
					| InvocationTargetException | NoSuchMethodException | NotImplementedSerializeException
					| RuntimeException e) {
				throw e;
			} catch (final Exception e) {
				throw new MarshallExeption(e);
			}
			virguleCourante = virguleEnglobante;
			return;
		}
		if (donnees) {
			// mode données : faux id exclu, virgule seulement après un champ écrit (un id null est omis comme un autre
			// champ null)
			for (final Champ champ : champs) {
				if (champ.getAcces() == null)
					continue;
				if (champ.getNaturePrimitive() != AccesChamp.AUCUNE && ecritChampSimple(v, champ, virgule)) {
					virgule = true;
					continue;
				}
				final Object valeur = champ.get(v, fakeIds, entite);
				if (valeur != null) {
					ecrit(valeur, champ, virgule);
					virgule = true;
				}
			}
			return;
		}
		for (final Champ champ : champs) {
			if (!ecritChampSimple(v, champ, virgule)) {
				final Object valeur = champ.get(v, fakeIds, entite);
				if (aTraiter(valeur, champ))
					ecrit(valeur, champ, virgule);
			}
			virgule = true;
		}
	}

	/** Tableau (ActionJsonArrayType) : [..] ou, type non devinable, {"__type":..,"__valeur":[..]}. */
	private void ecritTableau(final Object v, final FieldInformations fi)
			throws IOException, MarshallExeption, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException {
		final boolean nePasEcrireType = !ecritType || devinable(v, fi);
		clef(fi);
		if (nePasEcrireType)
			m.ouvreCrochet();
		else {
			m.ouvreAccolade();
			m.ecritType(TypeExtension.getClasseASerialiser(v));
			m.writeSeparator();
			m.ecritClef(Constants.VALEUR);
			m.ouvreCrochet();
		}
		final FakeChamp element = new FakeChamp(null, v.getClass().getComponentType(), fi.getRelation(),
				fi.getAnnotations());
		final int longueur = Array.getLength(v);
		profondeur++;
		for (int i = 0; i < longueur; i++)
			ecrit(Array.get(v, i), element, i > 0);
		profondeur--;
		m.fermeCrochet(longueur != 0);
		if (!nePasEcrireType)
			m.fermeAccolade();
	}

	/** virgule à écrire avant le prochain champ de l'objet en cours d'écriture par l'écrivain généré. */
	private boolean virguleCourante;

	/** écrivain généré des champs de la classe (getfield directs), null si la génération n'est pas possible. */
	private static EcrivainChamps ecrivainGenere(final Class<?> type, final TypeExtension.ChampsDuType champsDuType) {
		Object ecrivain = champsDuType.getEcrivainJson();
		if (ecrivain == null) {
			final EcrivainChamps e = GenerateurSerialiseurs.ecrivain(type, champsDuType.getTableauChamps(),
					EcrivainJsonDirect.class);
			ecrivain = e != null ? e : Boolean.FALSE;
			champsDuType.setEcrivainJson(ecrivain);
		}
		return ecrivain instanceof EcrivainChamps ? (EcrivainChamps) ecrivain : null;
	}

	//////// écriture des champs par l'écrivain généré : même texte que la boucle sur les champs

	private void clefGeneree(final FieldInformations champ) throws IOException {
		if (virguleCourante)
			sortie.write(',');
		virguleCourante = true;
		m.ecritClef((Champ) champ);
	}

	public void ecritInt(final int v, final FieldInformations champ) throws IOException {
		clefGeneree(champ);
		sortie.writeLong(v);
	}

	public void ecritLong(final long v, final FieldInformations champ) throws IOException {
		clefGeneree(champ);
		sortie.writeLong(v);
	}

	public void ecritShort(final short v, final FieldInformations champ) throws IOException {
		clefGeneree(champ);
		sortie.writeLong(v);
	}

	public void ecritByte(final byte v, final FieldInformations champ) throws IOException {
		clefGeneree(champ);
		sortie.writeLong(v);
	}

	public void ecritDouble(final double v, final FieldInformations champ) throws IOException {
		clefGeneree(champ);
		sortie.writeDouble(v);
	}

	public void ecritFloat(final float v, final FieldInformations champ) throws IOException {
		clefGeneree(champ);
		sortie.write(Float.toString(v));
	}

	public void ecritBoolean(final boolean v, final FieldInformations champ) throws IOException {
		clefGeneree(champ);
		sortie.write(v ? "true" : "false");
	}

	/** char : comme la boucle, par le chemin général (Character). */
	public void ecritChar(final char v, final FieldInformations champ)
			throws IOException, MarshallExeption, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException {
		final boolean virgule = virguleCourante;
		virguleCourante = true;
		ecrit(Character.valueOf(v), champ, virgule);
	}

	public void ecritObjet(final Object v, final FieldInformations champ)
			throws IOException, MarshallExeption, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException {
		final boolean virgule = virguleCourante;
		if (v == null && donnees)
			return; // rien d'écrit : pas de virgule pour le champ suivant (un id null est omis comme un autre)
		virguleCourante = true;
		if (!aTraiter(v, champ))
			return;
		if (champ.getValueType() == String.class) {
			if (virgule)
				sortie.write(',');
			m.ecritClef((Champ) champ);
			m.ecritEntreGuillemets(v.toString(), remplacements);
		} else
			ecrit(v, champ, virgule);
	}

	private static boolean aTraiter(final Object valeur, final FieldInformations champ) throws MarshallExeption {
		if (champ instanceof FakeChamp)
			return true;
		if (champ.isChampId() && valeur == null)
			throw new MarshallExeption("l'objet a un id null");
		return valeur != null;
	}

	/** Champ primitif (hors char) ou chaîne : même texte qu'ActionJsonObject.ecritSimple. */
	private boolean ecritChampSimple(final Object obj, final Champ champ, final boolean virgule)
			throws IOException, MarshallExeption, IllegalAccessException {
		final int nature = champ.getNaturePrimitive();
		if (nature == AccesChamp.AUCUNE) {
			if (champ.getValueType() != String.class)
				return false;
			final Object valeur = champ.get(obj, fakeIds, entite);
			if (aTraiter(valeur, champ)) {
				if (virgule)
					sortie.write(',');
				m.ecritClef(champ);
				m.ecritEntreGuillemets(valeur.toString(), remplacements);
			}
			return true;
		}
		final AccesChamp acces = champ.getAcces();
		switch (nature) {
		case AccesChamp.INT:
			clefSimple(champ, virgule);
			sortie.writeLong(acces.getInt(obj));
			return true;
		case AccesChamp.LONG:
			clefSimple(champ, virgule);
			sortie.writeLong(acces.getLong(obj));
			return true;
		case AccesChamp.SHORT:
			clefSimple(champ, virgule);
			sortie.writeLong(acces.getShort(obj));
			return true;
		case AccesChamp.BYTE:
			clefSimple(champ, virgule);
			sortie.writeLong(acces.getByte(obj));
			return true;
		case AccesChamp.DOUBLE:
			clefSimple(champ, virgule);
			sortie.writeDouble(acces.getDouble(obj));
			return true;
		case AccesChamp.FLOAT:
			clefSimple(champ, virgule);
			sortie.write(Float.toString(acces.getFloat(obj)));
			return true;
		case AccesChamp.BOOLEAN:
			clefSimple(champ, virgule);
			sortie.write(acces.getBoolean(obj) ? "true" : "false");
			return true;
		default:
			return false;
		}
	}

	private void clefSimple(final Champ champ, final boolean virgule) throws IOException {
		if (virgule)
			sortie.write(',');
		m.ecritClef(champ);
	}

	private FakeChamp[] champsElements(final FieldInformations fi) {
		FakeChamp[] champs = champsElements.get(fi);
		if (champs == null) {
			final Type[] types = fi.getParametreType();
			// collection : premier paramètre s'il y en a un ; map : clé et valeur s'il y en a deux
			final Type element = types != null && types.length > 0 ? types[0] : Object.class;
			final Type clef = types != null && types.length > 1 ? types[0] : Object.class;
			final Type valeur = types != null && types.length > 1 ? types[1] : Object.class;
			champs = new FakeChamp[] { new FakeChamp(null, element, fi.getRelation(), fi.getAnnotations()),
					new FakeChamp(null, clef, fi.getRelation(), fi.getAnnotations()),
					new FakeChamp(null, valeur, fi.getRelation(), fi.getAnnotations()) };
			if (champsElements.size() >= CHAMPS_ELEMENTS_MAX)
				champsElements.clear();
			champsElements.put(fi, champs);
		}
		return champs;
	}

	/** Collection (ActionJsonCollectionType) : [..] ou, type non devinable, {"__type":..,"__valeur":[..]}. */
	private void ecritCollection(final Collection<?> v, final FieldInformations fi)
			throws IOException, MarshallExeption, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException {
		final boolean nePasEcrireType = !ecritType || devinable(v, fi);
		final boolean crochetSeul = nePasEcrireType || v instanceof ArrayList;
		clef(fi);
		if (crochetSeul)
			m.ouvreCrochet();
		else {
			m.ouvreAccolade();
			m.ecritType(ActionJsonCollectionType.typeAEcrire(v));
			m.writeSeparator();
			m.ecritClef(Constants.VALEUR);
			m.ouvreCrochet();
		}
		final FakeChamp element = champsElements(fi)[0];
		profondeur++;
		boolean separateur = false;
		for (final Object e : v) {
			ecrit(e, element, separateur);
			separateur = true;
		}
		profondeur--;
		m.fermeCrochet(!v.isEmpty());
		if (!crochetSeul)
			m.fermeAccolade();
	}

	/** Map (ActionJsonDictionary) : [clé, valeur...] ou, type non devinable, {"__type":..,"__valeur":[..]}. */
	private void ecritMap(final Map<?, ?> v, final FieldInformations fi)
			throws IOException, MarshallExeption, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException {
		final boolean nePasEcrireType = !ecritType || devinable(v, fi);
		clef(fi);
		if (nePasEcrireType)
			m.ouvreCrochet();
		else {
			m.ouvreAccolade();
			m.ecritType(TypeExtension.getClasseASerialiser(v));
			m.writeSeparator();
			m.ecritClef(Constants.VALEUR);
			m.ouvreCrochet();
		}
		final FakeChamp[] champs = champsElements(fi);
		final FakeChamp champClef = champs[1];
		final FakeChamp champValeur = champs[2];
		profondeur++;
		boolean separateur = false;
		for (final Map.Entry<?, ?> entree : v.entrySet()) {
			ecrit(entree.getKey(), champClef, separateur);
			separateur = true;
			ecrit(entree.getValue(), champValeur, true);
		}
		profondeur--;
		m.fermeCrochet(!v.isEmpty());
		if (!nePasEcrireType)
			m.fermeAccolade();
	}
}
