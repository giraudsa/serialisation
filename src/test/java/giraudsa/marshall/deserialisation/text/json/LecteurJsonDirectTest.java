package giraudsa.marshall.deserialisation.text.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import giraudsa.marshall.annotations.Relation;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.UnmarshallExeption;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

/**
 * La lecture directe doit donner exactement le même graphe que le lecteur historique (comparés par leur
 * réécriture JSON, faux ids renumérotés), et être effectivement prise pour les JSON produits par l'écriture.
 */
class LecteurJsonDirectTest {

	enum Couleur {
		BLEU, ROUGE
	}

	static class Personne {
		String id;
		String nom;
		int age;
		double taille;
		float petit;
		long grand;
		short court;
		byte octet;
		char lettre;
		boolean actif;
		Integer boite;
		Long grandeBoite;
		Date naissance;
		java.sql.Timestamp horodatage;
		UUID uuid;
		Couleur couleur;
		BigDecimal montant;
		BigInteger entierLong;
		Personne ami;
		@Relation(type = TypeRelation.COMPOSITION)
		List<Personne> enfants = new ArrayList<>();
		@Relation(type = TypeRelation.COMPOSITION)
		Map<String, Integer> notes = new LinkedHashMap<>();
		Set<String> etiquettes = new HashSet<>();
		Object libre;
		Object autreLibre;
		int[] tableau;
		String[] noms;
		@Relation(type = TypeRelation.COMPOSITION)
		List<List<Integer>> matrice = new ArrayList<>();
		@Relation(type = TypeRelation.COMPOSITION)
		Map<Couleur, List<String>> parCouleur = new TreeMap<>();
		@Relation(type = TypeRelation.COMPOSITION)
		LinkedList<Object> melange = new LinkedList<>();
	}

	static class Derivee extends Personne {
		String extra;
	}

	static class SansId {
		String v;
		@Relation(type = TypeRelation.COMPOSITION)
		List<SansId> liste = new ArrayList<>();
		SansId autre;
	}

	private static Personne personne(final String id) {
		final Derivee p = new Derivee();
		p.extra = "dérivée";
		p.id = id;
		p.nom = "texte \"échappé\" \\ / \n\t \u0001 <>&='   , : { } [ ] fin";
		p.age = -42;
		p.taille = 1.75;
		p.petit = -0.5f;
		p.grand = Long.MIN_VALUE;
		p.court = Short.MAX_VALUE;
		p.octet = -128;
		p.lettre = '"';
		p.actif = true;
		p.boite = 7;
		p.grandeBoite = 1L << 40;
		p.naissance = new Date(-86_400_000L * 365 * 30);
		p.horodatage = new java.sql.Timestamp(1_700_000_000_123L);
		p.uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		p.couleur = Couleur.ROUGE;
		p.montant = new BigDecimal("-12345.678901234567890");
		p.entierLong = new BigInteger("123456789012345678901234567890");
		p.notes.put("un", 1);
		p.notes.put("deux, \"trois\"", 2);
		p.etiquettes.add("a");
		p.etiquettes.add("");
		p.libre = 12;
		p.autreLibre = "chaîne";
		p.tableau = new int[] { 1, -2, Integer.MAX_VALUE };
		p.noms = new String[] { "x", null, "z" };
		p.matrice.add(Arrays.asList(1, 2));
		p.matrice.add(new ArrayList<>());
		p.parCouleur.put(Couleur.BLEU, new ArrayList<>(Arrays.asList("ciel", "mer")));
		p.melange.add(1);
		p.melange.add("deux");
		p.melange.add(3L);
		p.melange.add(4.5);
		p.melange.add(new Date(0));
		p.melange.add(Couleur.BLEU);
		p.melange.add(null);
		p.melange.add(true);
		p.melange.add(UUID.fromString("00000000-0000-0000-0000-000000000001"));
		p.melange.add(new HashMap<>(Map.of("k", 1)));
		for (int i = 0; i < 3; i++) {
			final Personne e = new Personne();
			e.id = id + "-enfant" + i;
			e.nom = "enfant " + i;
			e.ami = p; // cycle
			e.uuid = p.uuid;
			e.naissance = p.naissance;
			p.enfants.add(e);
		}
		p.ami = p.enfants.get(1);
		p.melange.add(p.enfants.get(2));
		return p;
	}

	private static SansId sansId() {
		final SansId s = new SansId();
		s.v = "racine";
		for (int i = 0; i < 3; i++) {
			final SansId e = new SansId();
			e.v = "e" + i;
			e.autre = s;
			s.liste.add(e);
		}
		s.autre = s.liste.get(0);
		return s;
	}

	private static final Pattern UUID_TEXTE = Pattern
			.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

	/** réécriture JSON, les UUID (faux ids aléatoires) renumérotés dans l'ordre d'apparition. */
	private static String empreinte(final Object o) throws Exception {
		final String json = JsonMarshaller.toCompleteJson(o);
		final Map<String, String> numeros = new HashMap<>();
		final Matcher m = UUID_TEXTE.matcher(json);
		final StringBuilder sb = new StringBuilder();
		while (m.find())
			m.appendReplacement(sb, numeros.computeIfAbsent(m.group(), k -> "uuid" + numeros.size()));
		m.appendTail(sb);
		return sb.toString();
	}

	/** met en forme le JSON (hors chaînes) avec le saut de ligne et l'indentation donnés. */
	private static String indente(final String json, final String saut, final String indentation) {
		final StringBuilder sb = new StringBuilder();
		boolean chaine = false;
		int niveau = 0;
		for (int i = 0; i < json.length(); i++) {
			final char x = json.charAt(i);
			if (chaine) {
				sb.append(x);
				if (x == '\\')
					sb.append(json.charAt(++i));
				else if (x == '"')
					chaine = false;
				continue;
			}
			switch (x) {
			case '"':
				chaine = true;
				sb.append(x);
				break;
			case '{':
			case '[':
				niveau++;
				sb.append(x).append(saut).append(indentation.repeat(niveau));
				break;
			case '}':
			case ']':
				niveau--;
				sb.append(saut).append(indentation.repeat(niveau)).append(x);
				break;
			case ',':
				sb.append(x).append(saut).append(indentation.repeat(niveau));
				break;
			case ':':
				sb.append(" : ");
				break;
			default:
				sb.append(x);
			}
		}
		return sb.toString();
	}

	private static void compare(final String json, final boolean directAttendu) throws Exception {
		final Object direct = LecteurJsonDirect.lit(json);
		final Object historique;
		try {
			historique = JsonUnmarshaller.litHistorique(json);
		} catch (final RuntimeException | UnmarshallExeption e) {
			// refusé par le lecteur historique : doit l'être aussi par la lecture courante
			assertNull(direct, json);
			assertThrows(Exception.class, () -> JsonUnmarshaller.fromJson(json));
			return;
		}
		if (directAttendu)
			assertNotNull(direct, "la lecture directe devait être prise : " + json);
		final Object lu = direct != null ? direct : JsonUnmarshaller.fromJson(json);
		assertEquals(empreinte(historique), empreinte(lu), json);
		assertEquals(empreinte(historique), empreinte(JsonUnmarshaller.fromJson(new java.io.StringReader(json))));
	}

	private static void compareVariantes(final Object o) throws Exception {
		compareVariantes(o, true);
	}

	private static void compareVariantes(final Object o, final boolean directAttendu) throws Exception {
		final String json = JsonMarshaller.toCompleteJson(o);
		compare(json, directAttendu);
		compare(indente(json, "\n", "  "), directAttendu);
		compare(indente(json, "\r\n", "    "), directAttendu);
		compare(indente(json, "\n", "\t"), false); // tabulations : lecteur historique
		compare(json.replace("\"__type\":", "\"@type\":"), directAttendu); // ids universels
		compare(JsonMarshaller.toJson(o), directAttendu);
	}

	@Test
	void grapheComplet() throws Exception {
		compareVariantes(personne("p"));
	}

	@Test
	void fauxIds() throws Exception {
		compareVariantes(sansId());
	}

	@Test
	void racines() throws Exception {
		final List<Object> liste = new ArrayList<>(Arrays.asList(1, "a", personne("q"), null));
		compareVariantes(liste);
		final Map<Object, Object> map = new LinkedHashMap<>();
		map.put("k", personne("r"));
		map.put(2, Couleur.BLEU);
		compareVariantes(map);
		compareVariantes(new Date(123));
		compareVariantes(Couleur.ROUGE);
		compareVariantes(3L);
		compareVariantes(new int[] { 1, 2 }, false); // tableau enveloppé : lecteur historique
	}

	@Test
	void textesEcritsALaMain() throws Exception {
		final String p = Personne.class.getName();
		// id après les autres champs, blancs, échappements
		compare("{\"__type\":\"" + p + "\", \"nom\" : \"a\\u00e9\\/\\\"b\" ,\"age\":3, \"ami\":{\"__type\":\"" + p
				+ "\",\"id\":\"z\"}, \"id\":\"x\" }", true);
		// clé inconnue
		compare("{\"__type\":\"" + p + "\", \"nom\" : \"a\\u00e9\\/\\\"b\" ,\"age\":3, \"inconnu\":{\"__type\":\"" + p
				+ "\",\"id\":\"z\"}, \"id\":\"x\" }", true);
		// référence avant définition, valeurs nulles
		compare("{\"__type\":\"" + p + "\",\"id\":\"x\",\"ami\":{\"id\":\"y\"},\"enfants\":[{\"id\":\"y\",\"nom\":\"n\","
				+ "\"ami\":{\"id\":\"x\"}},null],\"boite\":null,\"nom\":null}", true);
		// JSON non strict ou hors des cas pris en charge : lecteur historique
		compare("{\"__type\":\"" + p + "\",\"id\":\"x\",\"enfants\":[1,],\"age\":3,}", false);
		compare("{\"__type\":\"" + p + "\",\"id\":\"x\",\"age\": 3 4}", false);
		compare("{\"__type\":\"" + p + "\",\"id\":\"x\",\"age\":,\"nom\":\"n\"}", false);
		compare("{\"__type\":\"" + p + "\",\"id\":\"x\",\"notes\":{}}", false);
		assertNull(LecteurJsonDirect.lit("\"chaine\""));
		assertNull(LecteurJsonDirect.lit("{\"a\":1}"));
		assertNull(LecteurJsonDirect.lit("{\"__type\":\"java.lang.Integer\""));
	}

	static class Nombres {
		String id = "n";
		double d;
		float f;
		Double boiteD;
		Float boiteF;
		long l;
		int i;
		@Relation(type = TypeRelation.COMPOSITION)
		List<Double> liste = new ArrayList<>();
		String texte;
		BigDecimal montant;
		boolean vrai;
	}

	@Test
	void nombresEtTextes() throws Exception {
		final java.util.Random r = new java.util.Random(7);
		final String[] decimaux = { "0", "-0", "0.0", "-0.0", "1", "0.1", "0.3", "123.456", "-9.99", "5.", ".5", "1e3",
				"1.5E-7", "00.25", "999999999999999", "9999999999999999", "0.000000000000000000000001",
				"123456.7890123", "16777216", "16777217", "3.4028235E38", "NaN", "-Infinity", "1_0", "+1", "true",
				"false", "TRUE", "tru", "fx", "null", "\"true\"", "\"12\"", "\"a\\\"b\"", "\"\"", "12 ", " 7" };
		final String type = Nombres.class.getName();
		for (final String d : decimaux)
			for (final String champ : new String[] { "d", "f", "boiteD", "boiteF", "l", "i", "montant", "vrai", "texte" })
				compare("{\"__type\":\"" + type + "\",\"id\":\"n\",\"" + champ + "\":" + d + "}", false);
		for (int k = 0; k < 300; k++) {
			final Nombres nb = new Nombres();
			nb.d = k % 3 == 0 ? r.nextDouble() : Math.round(r.nextDouble() * 1e6) / Math.pow(10, k % 7);
			nb.f = k % 3 == 0 ? r.nextFloat() : (float) (Math.round(r.nextDouble() * 1e4) / Math.pow(10, k % 5));
			nb.boiteD = -nb.d * Math.pow(10, k % 40 - 20);
			nb.boiteF = -nb.f;
			nb.l = r.nextLong() >> k % 64;
			nb.i = r.nextInt() >> k % 32;
			nb.liste.add(nb.d);
			nb.liste.add((double) nb.f);
			nb.montant = BigDecimal.valueOf(r.nextLong() >> k % 64, k % 25 - 5);
			final StringBuilder sb = new StringBuilder();
			for (int j = 0; j < 10; j++)
				sb.append((char) (k % 2 == 0 ? r.nextInt(0x80) : r.nextInt(0x3000)));
			sb.append("\uD83D\uDE00"); // paire de substitution
			nb.texte = sb.toString();
			final String json = JsonMarshaller.toCompleteJson(nb);
			compare(json, true);
			final Nombres lu = JsonUnmarshaller.fromJson(json);
			assertEquals(Double.doubleToRawLongBits(nb.d), Double.doubleToRawLongBits(lu.d));
			assertEquals(Float.floatToRawIntBits(nb.f), Float.floatToRawIntBits(lu.f));
			assertEquals(nb.boiteD, lu.boiteD);
			assertEquals(nb.boiteF, lu.boiteF);
			assertEquals(nb.l, lu.l);
			assertEquals(nb.i, lu.i);
			assertEquals(nb.texte, lu.texte);
			assertEquals(nb.montant, lu.montant); // valeur et échelle
		}
		// demi-caractère isolé : lecteur historique
		final Nombres isole = new Nombres();
		isole.texte = "a\uD83Db";
		compare(JsonMarshaller.toCompleteJson(isole), false);
	}

	static class Unicode {
		String id = "u";
		String prix\u20ac;
		String texte;
		String question;
	}

	@Test
	void latin1EtAuDela() throws Exception {
		final Unicode u = new Unicode();
		u.prix\u20ac = "12 \u20ac ?";
		u.texte = "caf\u00e9 ? \u4e2d ?";
		u.question = "pourquoi ?";
		compare(JsonMarshaller.toCompleteJson(u), true);
		final Unicode seulementLatin1 = new Unicode();
		seulementLatin1.question = "quoi ? où ?";
		compare(JsonMarshaller.toCompleteJson(seulementLatin1), true);
		final String type = Unicode.class.getName();
		compare("{\"__type\":\"" + type + "\",\"id\":\"a?\",\"question\":\"\u20ac\"}", true);
		compare("{\"__type\":\"" + type + "\",\"id\":\"a?\",\"question\":\"?\u20ac?\\n\"}", true);
	}

	@Test
	void lecteurGenere() throws Exception {
		final String n = Nombres.class.getName();
		final String debut = "{\"__type\":\"" + n + "\",\"id\":\"n\"";
		// dans l'ordre d'écriture, clés manquantes, désordre, doublon, blancs, référence seule
		compare(JsonMarshaller.toCompleteJson(new Nombres()), true);
		compare(debut + ",\"d\":1.5,\"i\":3,\"texte\":\"t\"}", true);
		compare(debut + ",\"texte\":\"t\",\"i\":3,\"d\":1.5,\"l\":7}", true);
		compare(debut + ",\"i\":3,\"i\":4,\"vrai\":true,\"vrai\":false}", true);
		compare(debut + " , \"i\" : 3 ,\n\"d\":2}", true);
		compare(debut + "}", false);
		compare(debut + ",\"i\":\"12\",\"l\":\"13\",\"d\":\"1e2\",\"f\":2,\"vrai\":\"true\"}", true);
		compare(debut + ",\"i\":null}", false);
		compare(debut + ",\"i\":3000000000}", false);
		final Object lecteur = utils.TypeExtension.getChampsDuType(Nombres.class).getLecteurJson();
		org.junit.jupiter.api.Assertions.assertTrue(lecteur instanceof utils.champ.LecteurChamps,
				"le lecteur généré doit être utilisé : " + lecteur);
	}

	@Test
	void grapheProfond() throws Exception {
		final SansId racine = new SansId();
		SansId courant = racine;
		for (int i = 0; i < 5000; i++) {
			final SansId e = new SansId();
			e.v = "n" + i;
			courant.liste.add(e);
			courant = e;
		}
		final String json = JsonMarshaller.toCompleteJson(racine);
		assertNull(LecteurJsonDirect.lit(json), "trop profond : lecteur historique");
		final SansId lu = JsonUnmarshaller.fromJson(json);
		SansId l = lu;
		for (int i = 0; i < 5000; i++)
			l = l.liste.get(0);
		assertEquals("n4999", l.v);
	}
}
