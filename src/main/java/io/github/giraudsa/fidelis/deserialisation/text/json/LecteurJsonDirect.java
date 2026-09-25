package io.github.giraudsa.fidelis.deserialisation.text.json;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

import io.github.giraudsa.fidelis.annotations.TypeRelation;
import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonArrayType;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonCollectionType;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonDate;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonDictionaryType;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonEnum;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonObject;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonSimpleComportement;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonUUID;
import io.github.giraudsa.fidelis.deserialisation.text.json.actions.ActionJsonVoid;
import io.github.giraudsa.fidelis.utils.Constants;
import io.github.giraudsa.fidelis.utils.TypeExtension;
import io.github.giraudsa.fidelis.utils.champ.AccesChamp;
import io.github.giraudsa.fidelis.utils.champ.Champ;
import io.github.giraudsa.fidelis.utils.champ.ChampUid;
import io.github.giraudsa.fidelis.utils.champ.FakeChamp;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.champ.GenerateurSerialiseurs;
import io.github.giraudsa.fidelis.utils.champ.LecteurChamps;
import io.github.giraudsa.fidelis.utils.champ.NullChamp;
import io.github.giraudsa.fidelis.utils.io.DatesIso;
import io.github.giraudsa.fidelis.utils.io.Decimaux;

/**
 * Lecture directe d'un JSON sans gestionnaire d'entités : analyse stricte du texte entier (en octets) et construction
 * des objets au fil de l'eau, sans événements ni actions empilées. Reproduit les règles de typage du lecteur
 * historique ({@link JsonUnmarshallerHandler} et les actions) pour les cas courants : objets, collections, maps,
 * tableaux, valeurs simples, dates, enums, UUID, valeurs enveloppées {"__type":..,"__valeur":..} et références par
 * id. Tout ce qui sort de ces cas (JSON non strict, types particuliers, erreurs...) abandonne la lecture : le texte
 * est alors relu par le lecteur historique, sans effet de bord puisque rien n'est persisté.
 */
public final class LecteurJsonDirect {

	/** abandon de la lecture directe : le lecteur historique prend le relais. */
	private static final class Abandon extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private Abandon() {
			super(null, null, false, false);
		}
	}

	private static final Abandon ABANDON = new Abandon();

	/** au-delà, on laisse la main au lecteur historique (pile explicite) plutôt que de risquer la pile d'appels. */
	private static final int PROFONDEUR_MAX = 400;

	private static final int AUTRE = 0;
	private static final int SIMPLE = 1;
	private static final int DATE = 2;
	private static final int ENUM = 3;
	private static final int UUID_ = 4;
	private static final int VOID = 5;
	private static final int OBJET = 6;
	private static final int COLLECTION = 7;
	private static final int MAP = 8;
	private static final int TABLEAU = 9;

	/** famille de lecture de chaque classe, d'après l'action que le lecteur historique lui associe. */
	private static final ClassValue<Integer> GENRES = new ClassValue<>() {
		@Override
		protected Integer computeValue(final Class<?> type) {
			final ActionAbstrait<?> prototype;
			try {
				prototype = JsonUnmarshaller.prototype(type);
			} catch (final Exception e) {
				return AUTRE;
			}
			final Class<?> k = prototype == null ? null : prototype.getClass();
			if (k == ActionJsonSimpleComportement.class)
				return SIMPLE;
			if (k == ActionJsonDate.class)
				return DATE;
			if (k == ActionJsonEnum.class)
				return ENUM;
			if (k == ActionJsonUUID.class)
				return UUID_;
			if (k == ActionJsonVoid.class)
				return VOID;
			if (k == ActionJsonObject.class)
				return OBJET;
			if (k == ActionJsonCollectionType.class)
				return COLLECTION;
			if (k == ActionJsonDictionaryType.class)
				return MAP;
			if (k == ActionJsonArrayType.class)
				return TABLEAU;
			return AUTRE;
		}
	};

	/** constructeur (long) des sous-classes de Date. */
	private static final ClassValue<Constructor<?>> CONSTRUCTEURS_DATE = new ClassValue<>() {
		@Override
		protected Constructor<?> computeValue(final Class<?> t) {
			try {
				return t.getConstructor(long.class);
			} catch (final NoSuchMethodException | SecurityException e) {
				return null;
			}
		}
	};

	/** puissances de dix exactes en float (jusqu'à 10^10). */
	private static final float[] PUISSANCES_FLOAT = new float[11];

	static {
		float f = 1;
		for (int i = 0; i < PUISSANCES_FLOAT.length; i++, f *= 10)
			PUISSANCES_FLOAT[i] = f;
	}

	private static final int CLEF_NORMALE = 0;
	private static final int CLEF_TYPE = 1;
	private static final int CLEF_TYPE_UNIVERSEL = 2;
	private static final int CLEF_VALEUR = 3;
	private static final int CLEF_ID = 4;

	/**
	 * Clé d'objet lue : son nom et, pour les deux dernières classes où elle a été résolue, le champ et le type
	 * attendu de sa valeur.
	 */
	private static final class Clef {
		private final String nom;
		private final byte[] octets;
		private final int hash;
		private final int nature;
		private Class<?> classe1;
		private FieldInformations champ1;
		private Class<?> attendu1;
		private Class<?> classe2;
		private FieldInformations champ2;
		private Class<?> attendu2;
		/** lecture rapide du champ (RAPIDE_*) et son accès direct, pour les mêmes deux classes. */
		private int rapide1;
		private AccesChamp acces1;
		private int rapide2;
		private AccesChamp acces2;
		/** clé qui a suivi celle-ci dans un objet de la classe (deux dernières classes). */
		private Class<?> classeSuite1;
		private Clef suite1;
		private Class<?> classeSuite2;
		private Clef suite2;
		/** classe nommée par ce texte, quand il est la valeur d'une clé de type, et son plan de lecture. */
		private Class<?> typeNomme;
		private int genreNomme;
		private TypeExtension.ChampsDuType champsNommes;
		private LecteurChamps lecteurNomme;

		/** le nom contient '?' (à vérifier sur le texte d'origine en Latin-1). */
		private final boolean interrogation;

		private Clef(final String nom, final byte[] octets, final int hash) {
			this.nom = nom;
			interrogation = nom.indexOf('?') >= 0;
			this.octets = octets;
			this.hash = hash;
			if (nom.equals(Constants.CLEF_TYPE))
				nature = CLEF_TYPE;
			else if (nom.equals(Constants.CLEF_TYPE_ID_UNIVERSEL))
				nature = CLEF_TYPE_UNIVERSEL;
			else if (nom.equals(Constants.VALEUR))
				nature = CLEF_VALEUR;
			else if (nom.equals(ChampUid.UID_FIELD_NAME))
				nature = CLEF_ID;
			else
				nature = CLEF_NORMALE;
		}

		private Clef suitePrevue(final Class<?> classe) {
			return classe == classeSuite1 ? suite1 : classe == classeSuite2 ? suite2 : null;
		}

		private void apprendSuite(final Class<?> classe, final Clef suite) {
			if (suite.octets == null)
				return;
			if (classe == classeSuite1)
				suite1 = suite;
			else if (classe == classeSuite2)
				suite2 = suite;
			else {
				classeSuite2 = classeSuite1;
				suite2 = suite1;
				classeSuite1 = classe;
				suite1 = suite;
			}
		}

		private void memorise(final Class<?> classe, final FieldInformations champ, final Class<?> attendu) {
			classe2 = classe1;
			champ2 = champ1;
			attendu2 = attendu1;
			rapide2 = rapide1;
			acces2 = acces1;
			classe1 = classe;
			champ1 = champ;
			attendu1 = attendu;
			rapide1 = RAPIDE_NON;
			acces1 = null;
			if (nature == CLEF_NORMALE && champ instanceof Champ && !champ.isChampId()) {
				final Champ c = (Champ) champ;
				final AccesChamp acces = c.getAcces();
				final int r = natureRapide(c);
				if (acces != null && r != RAPIDE_NON) {
					rapide1 = r;
					acces1 = acces;
				}
			}
		}
	}

	private static final int RAPIDE_NON = 0;
	private static final int RAPIDE_INT = 1;
	private static final int RAPIDE_LONG = 2;
	private static final int RAPIDE_DOUBLE = 3;
	private static final int RAPIDE_BOOLEAN = 4;
	private static final int RAPIDE_CHAINE = 5;

	/** champ lu sans boxing ni résolution de type quand la valeur est dans le cas simple de sa nature. */
	private static int natureRapide(final Champ champ) {
		switch (champ.getNaturePrimitive()) {
		case AccesChamp.INT:
			return RAPIDE_INT;
		case AccesChamp.LONG:
			return RAPIDE_LONG;
		case AccesChamp.DOUBLE:
			return RAPIDE_DOUBLE;
		case AccesChamp.BOOLEAN:
			return RAPIDE_BOOLEAN;
		case AccesChamp.AUCUNE:
			return champ.getValueType() == String.class ? RAPIDE_CHAINE : RAPIDE_NON;
		default:
			return RAPIDE_NON;
		}
	}

	/** clés déjà rencontrées (par thread), retrouvées d'après leurs octets sans créer de chaîne. */
	private static final class TableClefs {
		private static final int TAILLE_MAX = 1 << 14;
		private Clef[] cases = new Clef[256];
		private int nb;
		private int generation = TypeExtension.getGeneration();
		/**
		 * champs des éléments (collection, map, tableau) par champ porteur : { élément, clé, valeur, élément de
		 * tableau }.
		 */
		private final IdentityHashMap<FieldInformations, FakeChamp[]> champsElements = new IdentityHashMap<>();

		/** oublie les champs résolus si la configuration des champs a changé. */
		private void verifieGeneration() {
			final int g = TypeExtension.getGeneration();
			if (g != generation) {
				cases = new Clef[256];
				nb = 0;
				champsElements.clear();
				generation = g;
			}
		}

		private Clef cherche(final byte[] b, final int debut, final int fin, final int hash) {
			final Clef[] t = cases;
			final int masque = t.length - 1;
			final int taille = fin - debut;
			int i = (hash ^ hash >>> 16) & masque;
			Clef e;
			while ((e = t[i]) != null) {
				if (e.hash == hash && e.octets.length == taille && egaux(e.octets, b, debut))
					return e;
				i = i + 1 & masque;
			}
			final byte[] octets = Arrays.copyOfRange(b, debut, fin);
			final Clef clef = new Clef(new String(octets, StandardCharsets.ISO_8859_1), octets, hash);
			if (2 * (nb + 1) > t.length) {
				if (t.length >= TAILLE_MAX)
					return clef; // table pleine : clé non gardée
				agrandit();
				ajoute(cases, clef);
			} else
				t[i] = clef;
			nb++;
			return clef;
		}

		private static boolean egaux(final byte[] o, final byte[] b, final int debut) {
			for (int k = 0; k < o.length; k++)
				if (o[k] != b[debut + k])
					return false;
			return true;
		}

		private void agrandit() {
			final Clef[] nouvelles = new Clef[cases.length * 2];
			for (final Clef e : cases)
				if (e != null)
					ajoute(nouvelles, e);
			cases = nouvelles;
		}

		private static void ajoute(final Clef[] t, final Clef clef) {
			final int masque = t.length - 1;
			int i = (clef.hash ^ clef.hash >>> 16) & masque;
			while (t[i] != null)
				i = i + 1 & masque;
			t[i] = clef;
		}
	}

	/** champ de l'objet racine, comme pour le lecteur historique. */
	private static final FieldInformations RACINE = new FakeChamp(null, Object.class, TypeRelation.COMPOSITION, null);

	private static final ThreadLocal<TableClefs> TABLES = ThreadLocal.withInitial(TableClefs::new);

	/**
	 * @return l'objet lu, ou null si la lecture directe n'est pas possible (le lecteur historique doit alors relire le
	 *         texte).
	 */
	static Object lit(final String texte) {
		// d'abord en Latin-1 (un octet par caractère, copie directe d'une chaîne compacte) ; un caractère au-delà de
		// U+00FF y devient '?' : les chaînes lues qui en contiennent sont comparées au texte, et au premier écart la
		// lecture reprend en UTF-8
		try {
			return new LecteurJsonDirect(texte.getBytes(StandardCharsets.ISO_8859_1), texte).lit();
		} catch (final Abandon e) {
			if (e != LATIN1_INSUFFISANT)
				return null;
		} catch (final StackOverflowError | Exception e) {
			// le lecteur historique relira et signalera l'erreur à sa manière
			return null;
		}
		// un demi-caractère UTF-16 isolé ne survit pas à l'UTF-8 : lecteur historique
		if (aSubstitutIsole(texte))
			return null;
		try {
			return new LecteurJsonDirect(texte.getBytes(StandardCharsets.UTF_8), null).lit();
		} catch (final Abandon | StackOverflowError e) {
			return null;
		} catch (final Exception e) {
			return null;
		}
	}

	/** le texte n'est pas en Latin-1 : relecture en UTF-8. */
	private static final Abandon LATIN1_INSUFFISANT = new Abandon();

	/**
	 * En Latin-1, une chaîne lue qui contient '?' est comparée au texte d'origine (même indice : un octet par
	 * caractère) : ce '?' peut remplacer un caractère au-delà de U+00FF.
	 */
	private String verifie(final String s, final int debut) {
		if (source != null && s.indexOf('?') >= 0 && !source.regionMatches(debut, s, 0, s.length()))
			throw LATIN1_INSUFFISANT;
		return s;
	}

	private static boolean aSubstitutIsole(final String s) {
		final int n = s.length();
		for (int i = 0; i < n; i++) {
			final char x = s.charAt(i);
			if (Character.isHighSurrogate(x)) {
				if (i + 1 >= n || !Character.isLowSurrogate(s.charAt(i + 1)))
					return true;
				i++;
			} else if (Character.isLowSurrogate(x))
				return true;
		}
		return false;
	}

	private static int genre(final Class<?> type) {
		return GENRES.get(type);
	}

	private final byte[] c;
	/** codage des caractères non ASCII du texte : Latin-1 ou UTF-8. */
	private final Charset codage;
	private final int n;
	private int p;
	private int profondeur;
	private final JsonUnmarshaller<?> u;
	private final TableClefs clefs;
	/** première clé du dernier objet lu (le plus souvent la clé de type) : prévue pour le suivant. */
	private Clef premiereClef;
	/** dernier nom de type lu pour un type attendu (table à accès direct par type attendu). */
	private static final int PREVISIONS = 16;
	private final Class<?>[] declaresPrevus = new Class<?>[PREVISIONS];
	private final Clef[] nomsPrevus = new Clef[PREVISIONS];
	/** une clé de type a été rencontrée (la première fixe le mode de cache des ids, comme le lecteur historique). */
	private boolean clefTypeVue;

	/** texte d'origine quand les octets sont en Latin-1 (voir verifie), null en UTF-8. */
	private final String source;

	private LecteurJsonDirect(final byte[] texte, final String sourceLatin1) throws Exception {
		c = texte;
		source = sourceLatin1;
		codage = sourceLatin1 != null ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;
		n = texte.length;
		u = JsonUnmarshaller.pourLectureDirecte();
		clefs = TABLES.get();
		clefs.verifieGeneration();
	}

	private Object lit() throws Exception {
		saute();
		if (p >= n)
			throw ABANDON;
		final Object o;
		if (c[p] == '{')
			o = litAccolade(null, RACINE);
		else if (c[p] == '[')
			o = litCrochet(ArrayList.class, RACINE);
		else
			throw ABANDON;
		saute();
		if (p != n || o == null)
			throw ABANDON;
		return o;
	}

	//////// analyse lexicale

	/** saute les blancs ; une tabulation ou un \r isolé abandonnent (le lecteur historique les traite à part). */
	private void saute() {
		while (p < n) {
			final byte x = c[p];
			if (x > ' ') // cas courant : caractère significatif
				return;
			if (x == ' ' || x == '\n')
				p++;
			else if (x == '\r' && p + 1 < n && c[p + 1] == '\n')
				p += 2;
			else if (x == '\t' || x == '\r')
				throw ABANDON;
			else
				return;
		}
	}

	/** @return l'octet significatif suivant (sans l'avancer). */
	private byte suivant() {
		saute();
		if (p >= n)
			throw ABANDON;
		return c[p];
	}

	private void attend(final char attendu) {
		if (suivant() != attendu)
			throw ABANDON;
		p++;
	}

	/** lit une chaîne entre guillemets (p sur le guillemet ouvrant). */
	private String litChaine() {
		final byte[] b = c;
		int i = ++p;
		int ou = 0;
		while (i < n) {
			final byte x = b[i];
			if (x == '"') {
				final String s = verifie(new String(b, p, i - p, ou < 0 ? codage : StandardCharsets.ISO_8859_1), p);
				p = i + 1;
				return s;
			}
			if (x == '\\')
				return litChaineEchappee(i);
			ou |= x;
			i++;
		}
		throw ABANDON;
	}

	private String litChaineEchappee(int i) {
		final StringBuilder sb = new StringBuilder(i - p + 16);
		int debut = p;
		while (i < n) {
			final byte x = c[i];
			if (x == '"') {
				segment(sb, debut, i);
				p = i + 1;
				return sb.toString();
			}
			if (x != '\\') {
				i++;
				continue;
			}
			segment(sb, debut, i);
			if (++i >= n)
				throw ABANDON;
			final byte e = c[i++];
			switch (e) {
			case 'u':
				if (i + 4 > n)
					throw ABANDON;
				char r = 0;
				for (int k = 0; k < 4; k++) {
					final byte h = c[i++];
					r <<= 4;
					if (h >= '0' && h <= '9')
						r += h - '0';
					else if (h >= 'a' && h <= 'f')
						r += h - 'a' + 10;
					else if (h >= 'A' && h <= 'F')
						r += h - 'A' + 10;
					else
						throw ABANDON;
				}
				sb.append(r);
				break;
			case 't':
				sb.append('\t');
				break;
			case 'b':
				sb.append('\b');
				break;
			case 'n':
				sb.append('\n');
				break;
			case 'r':
				sb.append('\r');
				break;
			case 'f':
				sb.append('\f');
				break;
			default:
				if (e < 0) // caractère non ASCII échappé
					throw ABANDON;
				sb.append((char) e);
			}
			debut = i;
		}
		throw ABANDON;
	}

	private void segment(final StringBuilder sb, final int debut, final int fin) {
		if (fin > debut)
			sb.append(verifie(new String(c, debut, fin - debut, codage), debut));
	}

	/** lit une clé et le deux-points qui la suit. */
	private Clef litClef() {
		final Clef clef = litNom(null);
		attend(':');
		return clef;
	}

	/**
	 * lit une clé et le deux-points qui la suit, la clé prévue (celle qui a suivi la précédente dans cette classe)
	 * étant vérifiée d'abord ; la clé lue devient la suite prévue.
	 */
	private Clef litClef(final Clef precedente, final Class<?> classe) {
		final Clef clef = litNom(precedente.suitePrevue(classe));
		attend(':');
		precedente.apprendSuite(classe, clef);
		return clef;
	}

	/** @return true si le texte entre guillemets en p est celui de la clé. */
	private boolean correspond(final Clef clef) {
		final byte[] o = clef.octets;
		final int debut = p + 1;
		final int fin = debut + o.length;
		if (fin >= n || c[fin] != '"')
			return false;
		for (int k = 0; k < o.length; k++)
			if (o[k] != c[debut + k])
				return false;
		return true;
	}

	/**
	 * lit un texte entre guillemets comme clé : retrouvé d'après ses octets (ou égal à la clé prévue), sinon (texte
	 * échappé ou non ASCII) une clé non gardée.
	 */
	private Clef litNom(final Clef prevue) {
		if (suivant() != '"')
			throw ABANDON;
		if (prevue != null && correspond(prevue)) {
			if (prevue.interrogation)
				verifie(prevue.nom, p + 1);
			p += prevue.octets.length + 2;
			return prevue;
		}
		final byte[] b = c;
		final int debut = ++p;
		int i = debut;
		int h = 0;
		while (i < n) {
			final byte x = b[i];
			if (x == '"')
				break;
			if (x == '\\' || x < 0) { // clé échappée ou non ASCII : non gardée
				p = debut - 1;
				return new Clef(litChaine(), null, 0);
			}
			h = 31 * h + x;
			i++;
		}
		if (i >= n)
			throw ABANDON;
		final Clef clef = clefs.cherche(b, debut, i, h);
		if (clef.interrogation)
			verifie(clef.nom, debut);
		p = i + 1;
		return clef;
	}

	/** @return true si la clé est une clé de type ; la première rencontrée fixe le mode de cache. */
	private boolean isClefType(final Clef clef) {
		if (clef.nature != CLEF_TYPE && clef.nature != CLEF_TYPE_UNIVERSEL)
			return false;
		if (!clefTypeVue) {
			clefTypeVue = true;
			u.choisitCache(clef.nature == CLEF_TYPE_UNIVERSEL);
		}
		return true;
	}

	//////// valeurs

	/**
	 * Lit la valeur suivante.
	 *
	 * @param declare type attendu (enveloppe des primitifs)
	 * @param fi      champ qui recevra la valeur (typage des éléments d'une collection...)
	 */
	private Object litValeur(final Class<?> declare, final FieldInformations fi) throws Exception {
		final byte x = suivant();
		if (x == '"')
			return litterale(declare, String.class, litChaine(), -1, -1);
		if (x == '{')
			return litAccolade(declare, fi);
		if (x == '[')
			return litCrochet(declare == null ? ArrayList.class : declare, fi);
		// valeur sans guillemets : jusqu'au prochain séparateur
		final int debut = p;
		int i = p;
		while (i < n) {
			final byte y = c[i];
			if (y == ',' || y == '}' || y == ']' || y == ' ' || y == '\n' || y == '\r')
				break;
			if (y == '"' || y == '{' || y == '[' || y == ':' || y == '\\' || y == '\t' || y < 0)
				throw ABANDON;
			i++;
		}
		if (i == debut) // valeur vide : ignorée par le lecteur historique
			throw ABANDON;
		p = i;
		final byte s = suivant();
		if (s != ',' && s != '}' && s != ']')
			throw ABANDON;
		final Class<?> typeGuess;
		switch (x) {
		case 't':
		case 'f':
			typeGuess = Boolean.class;
			break;
		case 'n':
			typeGuess = Void.class;
			break;
		default:
			typeGuess = Integer.class;
		}
		return litterale(declare, typeGuess, null, debut, i);
	}

	/**
	 * Valeur littérale, typée comme par le lecteur historique : le type deviné, sauf s'il n'est pas compatible avec le
	 * type attendu. Le texte est soit la chaîne donnée, soit c[debut, fin[ (ASCII).
	 */
	private Object litterale(final Class<?> declare, final Class<?> typeGuess, final String chaine, final int debut,
			final int fin) throws Exception {
		Class<?> type = typeGuess;
		if (typeGuess != Void.class && declare != null && !declare.isAssignableFrom(typeGuess))
			type = declare;
		if (type == String.class && chaine != null) // construit(String, s) rend s
			return chaine;
		switch (genre(type)) {
		case SIMPLE:
			if (chaine == null) {
				final Object rapide = nombreRapide(type, debut, fin);
				if (rapide != null)
					return rapide;
			}
			if (type == BigDecimal.class) // même résultat que le constructeur (String), sans réflexion
				return new BigDecimal(texte(chaine, debut, fin));
			return ActionJsonSimpleComportement.construit(type, texte(chaine, debut, fin));
		case DATE:
			return date(type, texte(chaine, debut, fin));
		case ENUM:
			return type == Enum.class ? null : TypeExtension.getEnumParNom(type).get(texte(chaine, debut, fin));
		case UUID_:
			return UUID.fromString(texte(chaine, debut, fin));
		case VOID:
			return null;
		default:
			throw ABANDON;
		}
	}

	/**
	 * Lecture sans chaîne intermédiaire des cas simples, de résultat identique à valueOf : entiers décimaux, booléen
	 * vrai, décimaux courts (exacts : mantisse et puissance de dix représentables, une seule division arrondie).
	 *
	 * @return null si la valeur n'est pas dans ces cas.
	 */
	private Object nombreRapide(final Class<?> type, final int debut, final int fin) {
		if (type == Integer.class) {
			final long v = entier(debut, fin, 10);
			if (v != Long.MIN_VALUE && v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE)
				return Integer.valueOf((int) v);
		} else if (type == Long.class) {
			final long v = entier(debut, fin, 18);
			if (v != Long.MIN_VALUE)
				return Long.valueOf(v);
		} else if (type == Double.class) {
			final double v = Decimaux.lit(c, debut, fin);
			if (v == v) // INVALIDE (NaN) : lecture par valueOf
				return Double.valueOf(v);
		} else if (type == Float.class)
			return floatRapide(debut, fin);
		else if (type == BigDecimal.class)
			return decimalExact(debut, fin);
		else if (type == Boolean.class) {
			if (fin - debut == 4 && c[debut] == 't' && c[debut + 1] == 'r' && c[debut + 2] == 'u'
					&& c[debut + 3] == 'e')
				return Boolean.TRUE;
		}
		return null;
	}

	/**
	 * [-]chiffres[.chiffres], au plus 18 chiffres : valeur non mise à l'échelle et échelle, comme le constructeur
	 * BigDecimal(String).
	 */
	private Object decimalExact(final int debut, final int fin) {
		int i = debut;
		final boolean negatif = c[i] == '-';
		if (negatif)
			i++;
		long v = 0;
		int chiffres = 0;
		int echelle = -1;
		for (; i < fin; i++) {
			final int d = c[i] - '0';
			if (d >= 0 && d <= 9) {
				if (++chiffres > 18)
					return null;
				v = v * 10 + d;
				if (echelle >= 0)
					echelle++;
			} else if (c[i] == '.' && echelle < 0 && chiffres > 0)
				echelle = 0;
			else
				return null;
		}
		if (chiffres == 0 || echelle == 0)
			return null;
		return BigDecimal.valueOf(negatif ? -v : v, echelle < 0 ? 0 : echelle);
	}

	/** [-]chiffres[.chiffres], mantisse d'au plus 2^24 et au plus 10 décimales (exacts en float). */
	private Object floatRapide(final int debut, final int fin) {
		int i = debut;
		final boolean negatif = c[i] == '-';
		if (negatif)
			i++;
		long m = 0;
		int chiffres = 0;
		int decimales = -1;
		for (; i < fin; i++) {
			final int d = c[i] - '0';
			if (d >= 0 && d <= 9) {
				if (++chiffres > 15)
					return null;
				m = m * 10 + d;
				if (decimales >= 0)
					decimales++;
			} else if (c[i] == '.' && decimales < 0 && chiffres > 0)
				decimales = 0;
			else
				return null;
		}
		if (chiffres == 0 || decimales == 0)
			return null;
		final int k = decimales < 0 ? 0 : decimales;
		if (m > 1 << 24 || k >= PUISSANCES_FLOAT.length)
			return null;
		final float f = (float) m / PUISSANCES_FLOAT[k];
		return Float.valueOf(negatif ? -f : f);
	}

	private String texte(final String chaine, final int debut, final int fin) {
		return chaine != null ? chaine : verifie(new String(c, debut, fin - debut, StandardCharsets.ISO_8859_1), debut);
	}

	/** @return l'entier écrit dans c[debut, fin[ (signe moins et chiffres seulement), ou Long.MIN_VALUE. */
	private long entier(final int debut, final int fin, final int chiffresMax) {
		int i = debut;
		final boolean negatif = c[i] == '-';
		if (negatif)
			i++;
		final int nb = fin - i;
		if (nb <= 0 || nb > chiffresMax)
			return Long.MIN_VALUE;
		long v = 0;
		for (; i < fin; i++) {
			final int d = c[i] - '0';
			if (d < 0 || d > 9)
				return Long.MIN_VALUE;
			v = v * 10 + d;
		}
		return negatif ? -v : v;
	}

	private Object date(final Class<?> type, final String donnees) throws Exception {
		long time = u.datesIsoUtc() ? DatesIso.lit(donnees) : DatesIso.INVALIDE;
		if (time == DatesIso.INVALIDE)
			time = u.formatDate().parse(donnees).getTime(); // en cas d'échec, le lecteur historique journalise
		if (type == Date.class)
			return new Date(time);
		final Constructor<?> constructeur = CONSTRUCTEURS_DATE.get(type);
		if (constructeur == null)
			throw ABANDON;
		return constructeur.newInstance(time);
	}

	//////// structures

	private void entre() {
		if (++profondeur > PROFONDEUR_MAX)
			throw ABANDON;
	}

	/** objet entre accolades (p sur l'accolade) : son type est donné par sa clé de type, sinon par le type attendu. */
	private Object litAccolade(final Class<?> declare, final FieldInformations fi) throws Exception {
		entre();
		p++;
		if (suivant() == '}')
			throw ABANDON;
		Clef clef = litNom(premiereClef);
		attend(':');
		if (clef.octets != null)
			premiereClef = clef;
		final Class<?> type;
		Clef nomType = null;
		if (isClefType(clef)) {
			// nom de type prévu d'après le type attendu (dernier nom lu pour lui)
			final int prevision = declare == null ? 0 : System.identityHashCode(declare) & PREVISIONS - 1;
			nomType = litNom(declaresPrevus[prevision] == declare ? nomsPrevus[prevision] : null);
			if (nomType.octets != null) {
				declaresPrevus[prevision] = declare;
				nomsPrevus[prevision] = nomType;
			}
			if (nomType.typeNomme == null) {
				final Class<?> t = JsonUnmarshaller.classeDepuisNom(nomType.nom);
				if (t.isAssignableFrom(String.class))
					throw ABANDON;
				nomType.genreNomme = genre(t);
				if (nomType.genreNomme == OBJET) {
					nomType.champsNommes = TypeExtension.getChampsDuType(t);
					nomType.lecteurNomme = lecteurGenere(t, nomType.champsNommes);
				}
				nomType.typeNomme = t;
			}
			type = nomType.typeNomme;
			final byte s = suivant();
			p++;
			if (s == '}')
				clef = null;
			else if (s == ',') {
				clef = litClef(clef, type);
				if (isClefType(clef))
					throw ABANDON;
			} else
				throw ABANDON;
		} else {
			if (declare == null) // objet racine sans type
				throw ABANDON;
			type = declare;
		}
		final Object o;
		switch (nomType != null ? nomType.genreNomme : genre(type)) {
		case OBJET:
			o = litObjet(type, clef, nomType);
			break;
		case COLLECTION:
			o = litCollectionEnveloppee(type, fi, clef);
			break;
		case MAP:
			o = litMapEnveloppee(type, fi, clef);
			break;
		case SIMPLE:
		case DATE:
		case ENUM:
		case UUID_:
		case VOID:
			o = litValeurEnveloppee(type, clef);
			break;
		default:
			throw ABANDON;
		}
		profondeur--;
		return o;
	}

	/** passe à la clé suivante de l'objet de la classe donnée, prévue d'après la précédente : null en fin d'objet. */
	private Clef clefSuivante(final Clef precedente, final Class<?> classe) {
		final byte s = suivant();
		p++;
		if (s == '}')
			return null;
		if (s != ',')
			throw ABANDON;
		final Clef clef = litClef(precedente, classe);
		if (isClefType(clef))
			throw ABANDON;
		return clef;
	}

	/** passe à la clé suivante de l'objet en cours : null en fin d'objet. */
	private Clef clefSuivante() {
		final byte s = suivant();
		p++;
		if (s == '}')
			return null;
		if (s != ',')
			throw ABANDON;
		final Clef clef = litClef();
		if (isClefType(clef))
			throw ABANDON;
		return clef;
	}

	/**
	 * Objet métier. Comme ActionJsonObject : l'objet est celui de son id (déjà vu ou créé), son type celui de l'objet
	 * retrouvé ; les champs sont affectés dans l'ordre de lecture. Les valeurs lues avant l'id attendent l'objet.
	 */
	/** @param plan nom de type lu (plan de lecture de sa classe), ou null */
	private Object litObjet(final Class<?> typeInitial, Clef clef, final Clef plan) throws Exception {
		Class<?> type = typeInitial;
		Object obj = null;
		Object[] enAttente = null;
		int nbEnAttente = 0;
		final Map<Object, UUID> fakeIds = u.fakeIds();
		for (; clef != null; clef = clefSuivante(clef, type)) {
			FieldInformations champ;
			final Class<?> attendu;
			if (clef.classe1 == type) {
				if (obj != null && clef.rapide1 != RAPIDE_NON && litRapide(clef.rapide1, clef.acces1, obj))
					continue;
				champ = clef.champ1;
				attendu = clef.attendu1;
			} else if (clef.classe2 == type) {
				if (obj != null && clef.rapide2 != RAPIDE_NON && litRapide(clef.rapide2, clef.acces2, obj))
					continue;
				champ = clef.champ2;
				attendu = clef.attendu2;
			} else {
				champ = TypeExtension.getChampByName(type, clef.nom);
				Class<?> t = champ.getValueType();
				if (champ.isSimple())
					t = TypeExtension.getTypeEnveloppe(t);
				attendu = TypeExtension.getTypeEnveloppe(t);
				if (champ != NullChamp.getInstance()) // selon la configuration, un champ inconnu est une erreur
					clef.memorise(type, champ, attendu);
			}
			final Object valeur = litValeur(attendu, champ);
			if (valeur != null && clef.nature == CLEF_ID) {
				if (obj != null)
					throw ABANDON;
				obj = u.objetParId(valeur.toString(), type);
				if (obj == null)
					throw ABANDON;
				if (obj.getClass() != type) {
					type = obj.getClass();
					champ = TypeExtension.getChampByName(type, clef.nom);
				}
				for (int i = 0; i < nbEnAttente; i += 2)
					TypeExtension.getChampByName(type, (String) enAttente[i]).set(obj, enAttente[i + 1], fakeIds);
				nbEnAttente = 0;
			}
			if (obj != null) {
				champ.set(obj, valeur, fakeIds);
				if (clef.nature == CLEF_ID) {
					// les champs suivants, dans l'ordre d'écriture, par le lecteur généré de la classe
					final boolean planValide = plan != null && plan.typeNomme == type;
					final TypeExtension.ChampsDuType champsDuType = planValide ? plan.champsNommes
							: TypeExtension.getChampsDuType(type);
					final LecteurChamps lecteur = planValide ? plan.lecteurNomme : lecteurGenere(type, champsDuType);
					if (lecteur != null)
						lecteur.lit(obj, this, champsDuType.getTableauChamps());
				}
			} else {
				if (enAttente == null)
					enAttente = new Object[8];
				else if (nbEnAttente == enAttente.length)
					enAttente = Arrays.copyOf(enAttente, nbEnAttente * 2);
				enAttente[nbEnAttente++] = clef.nom;
				enAttente[nbEnAttente++] = valeur;
			}
		}
		if (obj == null && nbEnAttente > 0)
			throw ABANDON; // objet sans id : le lecteur historique décide
		return obj;
	}

	/**
	 * Valeur d'un champ primitif ou chaîne, écrite dans son cas simple (nombre décimal, true/false, chaîne sans
	 * échappement) : lue et affectée directement, avec le même résultat que le chemin général (même type retenu, même
	 * valeur). @return false (p inchangé) si la valeur n'est pas dans ce cas.
	 */
	private boolean litRapide(final int rapide, final AccesChamp acces, final Object obj) {
		final byte x = suivant();
		final int debut = p;
		if (rapide == RAPIDE_CHAINE) {
			if (x != '"')
				return false;
			int i = debut + 1;
			int ou = 0;
			while (i < n) {
				final byte y = c[i];
				if (y == '"')
					break;
				if (y == '\\')
					return false;
				ou |= y;
				i++;
			}
			if (i >= n)
				return false;
			final String s = verifie(new String(c, debut + 1, i - debut - 1,
					ou < 0 ? codage : StandardCharsets.ISO_8859_1), debut + 1);
			p = i + 1;
			if (!finDeValeur()) {
				p = debut;
				return false;
			}
			acces.set(obj, s);
			return true;
		}
		if (x == '"' || x == '{' || x == '[')
			return false;
		int i = debut;
		while (i < n) {
			final byte y = c[i];
			if (y == ',' || y == '}' || y == ']' || y == ' ' || y == '\n' || y == '\r')
				break;
			i++;
		}
		p = i;
		if (i == debut || !finDeValeur()) {
			p = debut;
			return false;
		}
		switch (rapide) {
		case RAPIDE_INT: {
			final long v = x == 't' || x == 'f' || x == 'n' ? Long.MIN_VALUE : entier(debut, i, 10);
			if (v != Long.MIN_VALUE && v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) {
				acces.setInt(obj, (int) v);
				return true;
			}
			break;
		}
		case RAPIDE_LONG: {
			final long v = x == 't' || x == 'f' || x == 'n' ? Long.MIN_VALUE : entier(debut, i, 18);
			if (v != Long.MIN_VALUE) {
				acces.setLong(obj, v);
				return true;
			}
			break;
		}
		case RAPIDE_DOUBLE: {
			final double v = Decimaux.lit(c, debut, i);
			if (v == v) { // INVALIDE (NaN) : chemin général
				acces.setDouble(obj, v);
				return true;
			}
			break;
		}
		case RAPIDE_BOOLEAN:
			// deviné booléen (t ou f), le type attendu l'accepte : Boolean.valueOf
			if (x == 't' || x == 'f') {
				acces.setBoolean(obj, "true".equalsIgnoreCase(new String(c, debut, i - debut, StandardCharsets.ISO_8859_1)));
				return true;
			}
			break;
		default:
			break;
		}
		p = debut;
		return false;
	}

	/** @return true si la valeur est suivie (blancs éventuels) d'une virgule ou d'une fin d'objet ou de tableau. */
	private boolean finDeValeur() {
		saute();
		if (p >= n)
			return false;
		final byte s = c[p];
		return s == ',' || s == '}' || s == ']';
	}

	/**
	 * Lecteur généré des champs de la classe (tous sauf l'id, qui vient en tête) : pour chaque champ, dans l'ordre
	 * d'écriture, t.champ = litXxx(t.champ, champ). null si la génération n'est pas possible.
	 */
	private static LecteurChamps lecteurGenere(final Class<?> type, final TypeExtension.ChampsDuType champsDuType) {
		Object lecteur = champsDuType.getLecteurJson();
		if (lecteur == null) {
			lecteur = creeLecteur(type, champsDuType);
			champsDuType.setLecteurJson(lecteur);
		}
		return lecteur instanceof LecteurChamps ? (LecteurChamps) lecteur : null;
	}

	private static Object creeLecteur(final Class<?> type, final TypeExtension.ChampsDuType champsDuType) {
		final Champ[] champs = champsDuType.getTableauChamps();
		if (champs.length < 2 || champs[0] != champsDuType.getChampId())
			return Boolean.FALSE;
		for (int i = 1; i < champs.length; i++) {
			final String nom = champs[i].getName();
			// un champ au nom réservé est lu par le chemin général (clé de type ou de valeur enveloppée)
			if (nom.equals(Constants.CLEF_TYPE) || nom.equals(Constants.CLEF_TYPE_ID_UNIVERSEL)
					|| nom.equals(Constants.VALEUR) || champs[i].getClefJson() == null)
				return Boolean.FALSE;
		}
		final LecteurChamps lecteur = GenerateurSerialiseurs.lecteurAvecValeurCourante(type, champs, 1,
				LecteurJsonDirect.class);
		return lecteur != null ? lecteur : Boolean.FALSE;
	}

	/**
	 * Si la clé du champ suit (après une virgule), la lit, deux-points compris. Sinon (fin de l'objet, autre clé,
	 * blancs inhabituels...) rien n'est lu : le chemin général traitera la suite.
	 */
	private boolean clefPresente(final FieldInformations fi) {
		int q = p;
		while (q < n && (c[q] == ' ' || c[q] == '\n'))
			q++;
		if (q >= n || c[q] != ',')
			return false;
		q++;
		while (q < n && (c[q] == ' ' || c[q] == '\n'))
			q++;
		final byte[] clef = ((Champ) fi).getClefJson();
		if (q + clef.length > n)
			return false;
		for (int k = 0; k < clef.length; k++)
			if (c[q + k] != clef[k])
				return false;
		p = q + clef.length;
		return true;
	}

	/** valeur générale du champ, du type attendu (enveloppe) : comme le chemin général. */
	private Object valeurGenerale(final FieldInformations fi) throws Exception {
		return litValeur(TypeExtension.getTypeEnveloppe(fi.getValueType()), fi);
	}

	/** valeur d'un champ primitif par le chemin général : du type de l'enveloppe, sinon abandon (null...). */
	private Object valeurPrimitive(final FieldInformations fi, final Class<?> enveloppe) throws Exception {
		final Object v = valeurGenerale(fi);
		if (v == null || v.getClass() != enveloppe)
			throw ABANDON;
		return v;
	}

	/**
	 * Entier sans guillemets suivi d'une fin de valeur : sa valeur, p après lui ; sinon Long.MIN_VALUE, p inchangé.
	 */
	private long entierSimple(final int chiffresMax) {
		final int debut = p;
		if (debut >= n)
			return Long.MIN_VALUE;
		final byte x = c[debut];
		if (x != '-' && (x < '0' || x > '9'))
			return Long.MIN_VALUE;
		int i = debut + 1;
		while (i < n && c[i] >= '0' && c[i] <= '9')
			i++;
		final long v = entier(debut, i, chiffresMax);
		if (v == Long.MIN_VALUE)
			return v;
		p = i;
		if (!finDeValeur()) {
			p = debut;
			return Long.MIN_VALUE;
		}
		return v;
	}

	//////// lecture des champs par le lecteur généré : valeur lue si la clé suit, sinon valeur courante

	public int litInt(final int courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		saute();
		final long v = entierSimple(10);
		if (v != Long.MIN_VALUE && v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE)
			return (int) v;
		return (Integer) valeurPrimitive(fi, Integer.class);
	}

	public long litLong(final long courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		saute();
		final long v = entierSimple(18);
		if (v != Long.MIN_VALUE)
			return v;
		return (Long) valeurPrimitive(fi, Long.class);
	}

	public double litDouble(final double courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		saute();
		final int debut = p;
		int i = debut;
		while (i < n) {
			final byte y = c[i];
			if (y == ',' || y == '}' || y == ']' || y == ' ' || y == '\n' || y == '\r')
				break;
			i++;
		}
		if (i > debut) {
			final double v = Decimaux.lit(c, debut, i);
			if (v == v) { // INVALIDE (NaN) : chemin général
				p = i;
				if (finDeValeur())
					return v;
				p = debut;
			}
		}
		return (Double) valeurPrimitive(fi, Double.class);
	}

	public float litFloat(final float courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		return (Float) valeurPrimitive(fi, Float.class);
	}

	public boolean litBoolean(final boolean courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		saute();
		final int debut = p;
		if (debut + 4 <= n && c[debut] == 't' && c[debut + 1] == 'r' && c[debut + 2] == 'u' && c[debut + 3] == 'e') {
			p = debut + 4;
			if (finDeValeur())
				return true;
			p = debut;
		} else if (debut + 5 <= n && c[debut] == 'f' && c[debut + 1] == 'a' && c[debut + 2] == 'l'
				&& c[debut + 3] == 's' && c[debut + 4] == 'e') {
			p = debut + 5;
			if (finDeValeur())
				return false;
			p = debut;
		}
		return (Boolean) valeurPrimitive(fi, Boolean.class);
	}

	public byte litByte(final byte courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		return (Byte) valeurPrimitive(fi, Byte.class);
	}

	public short litShort(final short courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		return (Short) valeurPrimitive(fi, Short.class);
	}

	public char litChar(final char courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		return (Character) valeurPrimitive(fi, Character.class);
	}

	public Object litObjet(final Object courant, final FieldInformations fi) throws Exception {
		if (!clefPresente(fi))
			return courant;
		if (fi.getValueType() == String.class && suivant() == '"') {
			// chaîne sans échappement : lue directement
			final int debut = p;
			int i = debut + 1;
			int ou = 0;
			while (i < n) {
				final byte y = c[i];
				if (y == '"' || y == '\\')
					break;
				ou |= y;
				i++;
			}
			if (i < n && c[i] == '"') {
				final String s = verifie(new String(c, debut + 1, i - debut - 1,
						ou < 0 ? codage : StandardCharsets.ISO_8859_1), debut + 1);
				p = i + 1;
				if (finDeValeur())
					return s;
				p = debut;
			}
		}
		return valeurGenerale(fi);
	}

	/** valeur simple enveloppée {"__type":T,"__valeur":v}. */
	private Object litValeurEnveloppee(final Class<?> type, Clef clef) throws Exception {
		final int genre = genre(type);
		Object resultat = null;
		for (; clef != null; clef = clefSuivante()) {
			if (clef.nature != CLEF_VALEUR)
				throw ABANDON;
			final byte x = suivant();
			if (x == '{' || x == '[')
				throw ABANDON;
			final Class<?> declare = genre == DATE ? Date.class : genre == UUID_ ? UUID.class : type;
			final Object v = litValeur(declare, null);
			if (genre == UUID_)
				resultat = v instanceof String ? UUID.fromString((String) v) : v instanceof UUID ? v : resultat;
			else if (genre != VOID)
				resultat = v;
		}
		return resultat;
	}

	private FakeChamp[] champsElements(final FieldInformations fi) {
		final IdentityHashMap<FieldInformations, FakeChamp[]> champsElements = clefs.champsElements;
		FakeChamp[] champs = champsElements.get(fi);
		if (champs == null) {
			final Type[] types = fi.getParametreType();
			final Type t0 = types != null && types.length > 0 ? types[0] : Object.class;
			final Type t1 = types != null && types.length > 1 ? types[1] : Object.class;
			champs = new FakeChamp[] { new FakeChamp("V", t0, fi.getRelation(), fi.getAnnotations()),
					new FakeChamp("K", t0, fi.getRelation(), fi.getAnnotations()),
					new FakeChamp("V", t1, fi.getRelation(), fi.getAnnotations()), null };
			if (champsElements.size() >= TableClefs.TAILLE_MAX)
				champsElements.clear();
			champsElements.put(fi, champs);
		}
		return champs;
	}

	/** tableau entre crochets (p sur le crochet), lu selon le type attendu. */
	private Object litCrochet(final Class<?> type, final FieldInformations fi) throws Exception {
		entre();
		final Object o;
		switch (genre(type)) {
		case COLLECTION: {
			final Collection<Object> coll = nouvelleCollection(type);
			litElements(coll, champsElements(fi)[0]);
			o = coll;
			break;
		}
		case MAP: {
			final Map<Object, Object> map = nouvelleMap(type);
			litPaires(map, fi);
			o = map;
			break;
		}
		case TABLEAU:
			o = litTableau(type, fi);
			break;
		default:
			throw ABANDON;
		}
		profondeur--;
		return o;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private static Collection<Object> nouvelleCollection(final Class<?> type) throws Exception {
		Class<?> t = type;
		if (TypeExtension.isHibernate(type) || type.isInterface())
			t = ArrayList.class;
		if (t == ArrayList.class)
			return new ArrayList<>();
		try {
			return (Collection<Object>) t.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return new ArrayList<>();
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private static Map<Object, Object> nouvelleMap(final Class<?> type) throws Exception {
		if (type.isInterface())
			throw ABANDON;
		return (Map<Object, Object>) type.newInstance();
	}

	/** éléments d'une collection : [e1, e2...] (p sur le crochet). */
	private void litElements(final Collection<Object> coll, final FakeChamp champ) throws Exception {
		final Class<?> declare = TypeExtension.getTypeEnveloppe(champ.getValueType());
		p++;
		if (suivant() == ']') {
			p++;
			return;
		}
		while (true) {
			coll.add(litValeur(declare, champ));
			final byte s = suivant();
			p++;
			if (s == ']')
				return;
			if (s != ',')
				throw ABANDON;
		}
	}

	/** map écrite à plat : [k1, v1, k2, v2...] (p sur le crochet). Une clé null est suivie d'une nouvelle clé. */
	private void litPaires(final Map<Object, Object> map, final FieldInformations fi) throws Exception {
		final FakeChamp[] champs = champsElements(fi);
		final Class<?> declareClef = TypeExtension.getTypeEnveloppe(champs[1].getValueType());
		final Class<?> declareValeur = TypeExtension.getTypeEnveloppe(champs[2].getValueType());
		Object clefTampon = null;
		p++;
		if (suivant() == ']') {
			p++;
			return;
		}
		while (true) {
			if (clefTampon == null)
				clefTampon = litValeur(declareClef, champs[1]);
			else {
				map.put(clefTampon, litValeur(declareValeur, champs[2]));
				clefTampon = null;
			}
			final byte s = suivant();
			p++;
			if (s == ']')
				return;
			if (s != ',')
				throw ABANDON;
		}
	}

	private Object litTableau(final Class<?> type, final FieldInformations fi) throws Exception {
		final Class<?> composantChamp = fi.getValueType().getComponentType();
		if (composantChamp == null)
			throw ABANDON;
		final FakeChamp[] champs = champsElements(fi);
		FakeChamp champ = champs[3];
		if (champ == null)
			champs[3] = champ = new FakeChamp("V", composantChamp, fi.getRelation(), fi.getAnnotations());
		final ArrayList<Object> tampon = new ArrayList<>();
		litElements(tampon, champ);
		final Object tableau = Array.newInstance(type.getComponentType(), tampon.size());
		for (int i = 0; i < tampon.size(); i++)
			Array.set(tableau, i, tampon.get(i));
		return tableau;
	}

	/** collection écrite {"__type":T,"__valeur":[...]} : les éléments du tableau sont ajoutés. */
	private Object litCollectionEnveloppee(final Class<?> type, final FieldInformations fi, Clef clef)
			throws Exception {
		final Collection<Object> coll = nouvelleCollection(type);
		for (; clef != null; clef = clefSuivante()) {
			if (clef.nature != CLEF_VALEUR || suivant() != '[')
				throw ABANDON;
			entre();
			litElements(coll, champsElements(fi)[0]);
			profondeur--;
		}
		return coll;
	}

	/** map écrite {"__type":T,"__valeur":[k1,v1...]}. */
	private Object litMapEnveloppee(final Class<?> type, final FieldInformations fi, Clef clef) throws Exception {
		final Map<Object, Object> map = nouvelleMap(type);
		for (; clef != null; clef = clefSuivante()) {
			if (clef.nature != CLEF_VALEUR || suivant() != '[')
				throw ABANDON;
			entre();
			litPaires(map, fi);
			profondeur--;
		}
		return map;
	}
}
