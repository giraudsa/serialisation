package giraudsa.marshall.serialisation.text.json;

import java.io.IOException;
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
import utils.champ.FieldInformations;
import utils.io.SortieTexte;

/**
 * Écriture JSON directe des valeurs courantes (objets, collections, maps, chaînes, nombres, booléens, dates, enums,
 * UUID) : mêmes étapes, dans le même ordre, que les actions (ActionJsonObject, ActionJsonCollectionType,
 * ActionJsonDictionary, ActionJsonSimple...), donc le même texte, mais sans comportement alloué, sans recherche
 * d'action par valeur ni champ d'éléments reconstruit pour chaque collection. Les autres valeurs, et au-delà d'une
 * profondeur, sont écrites par leur action.
 */
final class EcrivainJsonDirect {

	/** au-delà, les valeurs imbriquées sont confiées aux actions (qui passent par une pile explicite). */
	private static final int PROFONDEUR_MAX = 100;

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
			if (profondeur < PROFONDEUR_MAX) {
				ecritObjet(v, fi);
				return;
			}
			break;
		case COLLECTION:
			if (profondeur < PROFONDEUR_MAX) {
				ecritCollection((Collection<?>) v, fi);
				return;
			}
			break;
		case MAP:
			if (profondeur < PROFONDEUR_MAX) {
				ecritMap((Map<?, ?>) v, fi);
				return;
			}
			break;
		default:
			break;
		}
		m.ecritParAction(v, fi);
	}

	/** Objet (ActionJsonObject) : ses champs, ou son seul id s'il est déjà écrit ou selon la stratégie. */
	private void ecritObjet(final Object v, final FieldInformations fi)
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
		final boolean serialiseTout = m.serialiseTout(fi) && !m.totalementSerialise(v);
		m.marqueDejaVu(v);
		profondeur++;
		if (!serialiseTout) {
			final Champ champId = champsDuType.getChampId();
			final Object id = champId.get(v, fakeIds, entite);
			if (aTraiter(id, champId))
				ecrit(id, champId, virgule);
		} else {
			m.marqueTotalementSerialise(v);
			for (final Champ champ : champsDuType.getTableauChamps()) {
				if (!ecritChampSimple(v, champ, virgule)) {
					final Object valeur = champ.get(v, fakeIds, entite);
					if (aTraiter(valeur, champ))
						ecrit(valeur, champ, virgule);
				}
				virgule = true;
			}
		}
		profondeur--;
		m.fermeAccolade();
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
			sortie.write(Double.toString(acces.getDouble(obj)));
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
