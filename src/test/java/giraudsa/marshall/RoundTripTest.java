package giraudsa.marshall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;

import giraudsa.marshall.annotations.Relation;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

class RoundTripTest {

	enum Couleur {
		BLEU, ROUGE
	}

	static class Noeud {
		String id;
		String nom;
		int entier;
		long grand;
		double reel;
		boolean drapeau;
		Integer boite;
		Date date;
		UUID uuid;
		Couleur couleur;
		BigDecimal montant;
		Noeud parent;
		@Relation(type = TypeRelation.COMPOSITION)
		List<Noeud> enfants = new ArrayList<>();
		@Relation(type = TypeRelation.COMPOSITION)
		Map<String, Integer> compteurs = new LinkedHashMap<>();
		List<String> tags = new ArrayList<>();
	}

	static class NoeudDerive extends Noeud {
		String extra;
	}

	/** Classe sans attribut id : un faux id est généré. */
	static class SansId {
		String valeur;
		@Relation(type = TypeRelation.COMPOSITION)
		List<SansId> liste = new ArrayList<>();
	}

	/** Deux instances distinctes mais égales au sens de equals. */
	static class Egal {
		String valeur;

		@Override
		public boolean equals(final Object o) {
			return o instanceof Egal;
		}

		@Override
		public int hashCode() {
			return 1;
		}
	}

	static class Conteneur {
		String id = "c";
		@Relation(type = TypeRelation.COMPOSITION)
		Egal a;
		@Relation(type = TypeRelation.COMPOSITION)
		Egal b;
	}

	static class Primitifs {
		String id = "p";
		byte b;
		short s;
		int i;
		long l;
		float f;
		double d;
		int grandId;
	}

	interface Format {
		<T> T roundTrip(T obj) throws Exception;
	}

	private static final Format JSON = new Format() {
		@Override
		public <T> T roundTrip(final T obj) throws Exception {
			return JsonUnmarshaller.fromJson(new StringReader(JsonMarshaller.toCompleteJson(obj)));
		}
	};
	private static final Format BINARY = new Format() {
		@Override
		public <T> T roundTrip(final T obj) throws Exception {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			BinaryMarshaller.toCompleteBinary(obj, out);
			return BinaryUnmarshaller.fromBinary(new ByteArrayInputStream(out.toByteArray()));
		}
	};

	static Noeud graphe(final String prefixe) {
		final Noeud racine = new NoeudDerive();
		((NoeudDerive) racine).extra = "dérivé";
		racine.id = prefixe + "racine";
		racine.nom = "texte \"échappé\" \\ / \n\t <>&=' \u2028 fin";
		racine.entier = 42;
		racine.grand = Long.MAX_VALUE;
		racine.reel = 3.25;
		racine.drapeau = true;
		racine.boite = -7;
		racine.date = new Date(1_700_000_000_123L);
		racine.uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		racine.couleur = Couleur.ROUGE;
		racine.montant = new BigDecimal("12345.678");
		racine.compteurs.put("un", 1);
		racine.compteurs.put("deux, trois", 2);
		racine.tags.add("a");
		racine.tags.add("a");
		racine.tags.add("");
		for (int i = 0; i < 3; i++) {
			final Noeud enfant = new Noeud();
			enfant.id = prefixe + "enfant" + i;
			enfant.nom = "enfant " + i;
			enfant.entier = i;
			enfant.parent = racine; // cycle
			enfant.uuid = racine.uuid; // uuid partagé
			enfant.date = racine.date;
			racine.enfants.add(enfant);
		}
		return racine;
	}

	static void verifie(final Noeud attendu, final Noeud lu) {
		assertEquals(NoeudDerive.class, lu.getClass());
		assertEquals(((NoeudDerive) attendu).extra, ((NoeudDerive) lu).extra);
		assertEquals(attendu.id, lu.id);
		assertEquals(attendu.nom, lu.nom);
		assertEquals(attendu.entier, lu.entier);
		assertEquals(attendu.grand, lu.grand);
		assertEquals(attendu.reel, lu.reel);
		assertEquals(attendu.drapeau, lu.drapeau);
		assertEquals(attendu.boite, lu.boite);
		assertEquals(attendu.date, lu.date);
		assertEquals(attendu.uuid, lu.uuid);
		assertEquals(attendu.couleur, lu.couleur);
		assertEquals(0, attendu.montant.compareTo(lu.montant));
		assertEquals(attendu.compteurs, lu.compteurs);
		assertEquals(attendu.tags, lu.tags);
		assertEquals(attendu.enfants.size(), lu.enfants.size());
		for (int i = 0; i < attendu.enfants.size(); i++) {
			final Noeud e = attendu.enfants.get(i);
			final Noeud l = lu.enfants.get(i);
			assertEquals(e.id, l.id);
			assertEquals(e.nom, l.nom);
			assertEquals(e.entier, l.entier);
			assertEquals(e.uuid, l.uuid);
			assertEquals(e.date, l.date);
			assertSame(lu, l.parent, "le cycle doit être reconstruit");
		}
	}

	private static void grapheComplet(final Format format) throws Exception {
		final Noeud racine = graphe("");
		verifie(racine, format.roundTrip(racine));
	}

	private static void sansId(final Format format) throws Exception {
		final SansId racine = new SansId();
		racine.valeur = "r";
		final SansId enfant = new SansId();
		enfant.valeur = "e";
		racine.liste.add(enfant);
		racine.liste.add(enfant);
		final SansId lu = format.roundTrip(racine);
		assertEquals("r", lu.valeur);
		assertEquals(2, lu.liste.size());
		assertEquals("e", lu.liste.get(0).valeur);
		assertSame(lu.liste.get(0), lu.liste.get(1));
	}

	private static void identite(final Format format) throws Exception {
		final Conteneur c = new Conteneur();
		c.a = new Egal();
		c.a.valeur = "a";
		c.b = new Egal();
		c.b.valeur = "b";
		final Conteneur lu = format.roundTrip(c);
		assertNotSame(lu.a, lu.b);
		assertEquals("a", lu.a.valeur);
		assertEquals("b", lu.b.valeur);
	}

	private static void concurrence(final Format format) throws Exception {
		final ExecutorService pool = Executors.newFixedThreadPool(8);
		try {
			final List<Future<Void>> futures = new ArrayList<>();
			for (int t = 0; t < 64; t++) {
				final String prefixe = "t" + t + "-";
				futures.add(pool.submit((Callable<Void>) () -> {
					final Noeud racine = graphe(prefixe);
					verifie(racine, format.roundTrip(racine));
					return null;
				}));
			}
			for (final Future<Void> f : futures)
				f.get();
		} finally {
			pool.shutdown();
		}
	}

	@Test
	void jsonGrapheComplet() throws Exception {
		grapheComplet(JSON);
	}

	@Test
	void binaireGrapheComplet() throws Exception {
		grapheComplet(BINARY);
		// JDK 15+ : écrivain et lecteur générés pour les classes du graphe (sinon, chemin générique)
		if (Runtime.version().feature() >= 15)
			for (final Class<?> c : new Class<?>[] { Noeud.class, NoeudDerive.class }) {
				final utils.TypeExtension.ChampsDuType champs = utils.TypeExtension.getChampsDuType(c);
				org.junit.jupiter.api.Assertions.assertTrue(
						champs.getEcrivainBinaire() instanceof utils.champ.EcrivainChamps, "écrivain de " + c);
				org.junit.jupiter.api.Assertions.assertTrue(
						champs.getLecteurBinaire() instanceof utils.champ.LecteurChamps, "lecteur de " + c);
			}
	}

	@Test
	void jsonSansId() throws Exception {
		sansId(JSON);
	}

	@Test
	void binaireSansId() throws Exception {
		sansId(BINARY);
	}

	@Test
	void jsonIdentite() throws Exception {
		identite(JSON);
	}

	@Test
	void binaireIdentite() throws Exception {
		identite(BINARY);
	}

	@Test
	void jsonConcurrence() throws Exception {
		concurrence(JSON);
	}

	@Test
	void binaireConcurrence() throws Exception {
		concurrence(BINARY);
	}

	@Test
	void caracteresDeControle() throws Exception {
		final String texte = "a\r\nb\u0001c\u001fd\u2029e\bf\fg\u0085h\u007fi\u2028j\r";
		for (final Format format : new Format[] { JSON, BINARY }) {
			final Noeud racine = graphe("");
			racine.nom = texte;
			verifie(racine, format.roundTrip(racine));
		}
	}

	@Test
	void caractereNul() throws Exception {
		for (final Format format : new Format[] { JSON, BINARY }) {
			final Noeud racine = graphe("");
			racine.nom = "a\u0000b";
			verifie(racine, format.roundTrip(racine));
		}
	}

	@Test
	void texteQuiRessembleAUneEntite() throws Exception {
		for (final Format format : new Format[] { JSON, BINARY }) {
			final Noeud racine = graphe("");
			racine.nom = "a &lt; b &amp; c &#65; &#x42; &quot; d";
			verifie(racine, format.roundTrip(racine));
		}
	}

	@Test
	void idAvecCaracteresSpeciaux() throws Exception {
		for (final Format format : new Format[] { JSON, BINARY }) {
			final Noeud racine = graphe("id \"<&>'\t\n\r fin ");
			verifie(racine, format.roundTrip(racine));
		}
	}

	static class Melange {
		String id = "m";
		@Relation(type = TypeRelation.COMPOSITION)
		Map<String, Object> valeurs = new LinkedHashMap<>();
		@Relation(type = TypeRelation.COMPOSITION)
		Map<Integer, Long> nombres = new LinkedHashMap<>();
		@Relation(type = TypeRelation.COMPOSITION)
		List<String> liste = new java.util.concurrent.CopyOnWriteArrayList<>();
	}

	@Test
	void mapsTypees() throws Exception {
		final Melange m = new Melange();
		m.valeurs.put("entier", 3);
		m.valeurs.put("texte", "3");
		m.valeurs.put("reel", 2.5);
		m.valeurs.put("bool", true);
		m.valeurs.put("date", new Date(123456789L));
		m.nombres.put(1, 10L);
		m.nombres.put(-2, Long.MAX_VALUE);
		m.liste.add("b");
		m.liste.add("a");
		for (final Format format : new Format[] { JSON, BINARY }) {
			final Melange lu = format.roundTrip(m);
			assertEquals(m.valeurs, lu.valeurs);
			assertEquals(m.nombres, lu.nombres);
			// classe et ordre d'itération conservés
			assertEquals(LinkedHashMap.class, lu.valeurs.getClass());
			assertEquals(new ArrayList<>(m.valeurs.keySet()), new ArrayList<>(lu.valeurs.keySet()));
			assertEquals(new ArrayList<>(m.nombres.keySet()), new ArrayList<>(lu.nombres.keySet()));
			assertEquals(java.util.concurrent.CopyOnWriteArrayList.class, lu.liste.getClass());
			assertEquals(m.liste, lu.liste);
		}
	}

	@Test
	void primitifsAuxBornes() throws Exception {
		final Primitifs p = new Primitifs();
		p.b = -3;
		p.s = Short.MIN_VALUE;
		p.i = Integer.MIN_VALUE;
		p.l = Long.MIN_VALUE;
		p.f = -1.5f;
		p.d = Double.MAX_VALUE;
		p.grandId = 100_000;
		for (final Format format : new Format[] { JSON, BINARY }) {
			final Primitifs lu = format.roundTrip(p);
			assertEquals(p.b, lu.b);
			assertEquals(p.s, lu.s);
			assertEquals(p.i, lu.i);
			assertEquals(p.l, lu.l);
			assertEquals(p.f, lu.f);
			assertEquals(p.d, lu.d);
			assertEquals(p.grandId, lu.grandId);
		}
	}

	@Test
	void grosGraphe() throws Exception {
		// dépasse les "very small id" et les encodages sur 1 octet
		final Noeud racine = graphe("");
		for (int i = 0; i < 2000; i++) {
			final Noeud enfant = new Noeud();
			enfant.id = "n" + i;
			enfant.nom = "nom" + (i % 50);
			enfant.entier = i;
			enfant.uuid = new UUID(i, -i);
			enfant.date = new Date(i * 1000L);
			enfant.parent = racine;
			racine.enfants.add(enfant);
		}
		for (final Format format : new Format[] { JSON, BINARY })
			verifie(racine, format.roundTrip(racine));
	}

	static class Caracteres {
		String id = "car";
		char c;
		char cZero;
		Character boite;
		Object objet;
	}

	@Test
	void caracteres() throws Exception {
		for (final Format format : new Format[] { JSON, BINARY }) {
			final Caracteres c = new Caracteres();
			c.c = 'é';
			c.cZero = 0;
			c.boite = '<';
			c.objet = '\u2028';
			final Caracteres lu = format.roundTrip(c);
			assertEquals(c.c, lu.c);
			assertEquals(c.cZero, lu.cZero);
			assertEquals(c.boite, lu.boite);
			assertEquals(c.objet, lu.objet);
		}
	}

	enum Operation {
		PLUS {
			@Override
			int applique(final int a, final int b) {
				return a + b;
			}
		},
		MOINS {
			@Override
			int applique(final int a, final int b) {
				return a - b;
			}
		};

		abstract int applique(int a, int b);
	}

	static class AvecOperation {
		String id = "op";
		Operation declaree;
		Object nonDeclaree;
	}

	@Test
	void enumAvecCorps() throws Exception {
		for (final Format format : new Format[] { JSON, BINARY }) {
			final AvecOperation o = new AvecOperation();
			o.declaree = Operation.MOINS;
			o.nonDeclaree = Operation.PLUS;
			final AvecOperation lu = format.roundTrip(o);
			assertSame(Operation.MOINS, lu.declaree);
			assertSame(Operation.PLUS, lu.nonDeclaree);
		}
	}

	@Test
	void binaireReferencesLointaines() throws Exception {
		// références arrière vers des objets, chaînes, dates et UUID dont le smallId dépasse les "very small id"
		final Noeud racine = graphe("");
		final Date[] dates = new Date[700];
		for (int i = 0; i < dates.length; i++)
			dates[i] = new Date(i * 1000L);
		for (int i = 0; i < 1500; i++) {
			final Noeud enfant = new Noeud();
			enfant.id = "n" + i;
			enfant.nom = "nom" + i;
			enfant.entier = i;
			enfant.date = dates[i % dates.length];
			enfant.uuid = new UUID(i % 600, 7);
			enfant.parent = i >= 300 ? racine.enfants.get(i - 300 + 3) : racine;
			racine.enfants.add(enfant);
		}
		for (int i = 0; i < 1500; i += 7)
			racine.tags.add("nom" + i);
		final Noeud lu = BINARY.roundTrip(racine);
		assertEquals(racine.tags, lu.tags);
		assertEquals(racine.enfants.size(), lu.enfants.size());
		for (int i = 3; i < racine.enfants.size(); i++) {
			final Noeud attendu = racine.enfants.get(i);
			final Noeud l = lu.enfants.get(i);
			assertEquals(attendu.id, l.id);
			assertEquals(attendu.nom, l.nom);
			assertEquals(attendu.date, l.date);
			assertEquals(attendu.uuid, l.uuid);
			if (i >= 303)
				assertSame(lu.enfants.get(i - 300), l.parent);
			else
				assertSame(lu, l.parent);
		}
		// les instances partagées restent partagées
		assertSame(lu.enfants.get(3).date, lu.enfants.get(3 + dates.length).date);
	}

	static class Tableaux {
		String id = "t";
		int[] entiers;
		long[] longs;
		double[] reels;
		boolean[] booleens;
		char[] caracteres;
		short[] courts;
		byte[] octets;
		float[] flottants;
		Integer[] boites;
		Object entierDansObjet;
		Integer boiteNulle;
	}

	@Test
	void binaireTableauxDePrimitifs() throws Exception {
		final Tableaux t = new Tableaux();
		t.entiers = new int[] { 0, -1, 63, -64, 64, Integer.MAX_VALUE, Integer.MIN_VALUE };
		t.longs = new long[] { 0, Long.MIN_VALUE, Long.MAX_VALUE, 1L << 40 };
		t.reels = new double[] { 0.0, -0.0, Double.NaN, 1e300 };
		t.booleens = new boolean[] { true, false };
		t.caracteres = new char[] { 'a', 'é', '\u0000', '\uffff' };
		t.courts = new short[] { Short.MIN_VALUE, 0, Short.MAX_VALUE };
		t.octets = new byte[] { -128, 0, 127 };
		t.flottants = new float[] { -1.5f, Float.MAX_VALUE };
		t.boites = new Integer[] { 1, null, -5 };
		t.entierDansObjet = 12;
		final Tableaux lu = BINARY.roundTrip(t);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.entiers, lu.entiers);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.longs, lu.longs);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.reels, lu.reels);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.booleens, lu.booleens);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.caracteres, lu.caracteres);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.courts, lu.courts);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.octets, lu.octets);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.flottants, lu.flottants);
		org.junit.jupiter.api.Assertions.assertArrayEquals(t.boites, lu.boites);
		assertEquals(12, lu.entierDansObjet);
		assertEquals(null, lu.boiteNulle);
	}

	@Test
	void binaireDedoublonnageAdaptatif() throws Exception {
		// d'abord des valeurs toutes différentes (la déduplication est abandonnée pour ce champ), puis répétées
		final Noeud racine = graphe("");
		for (int i = 0; i < 2000; i++) {
			final Noeud n = new Noeud();
			n.id = "a" + i;
			n.nom = i < 1000 ? "unique" + i : "répété" + (i % 3);
			n.tags.add(i < 1000 ? "t" + i : "tag");
			racine.enfants.add(n);
		}
		for (int tour = 0; tour < 2; tour++) {
			final Noeud lu = BINARY.roundTrip(racine);
			for (int i = 3; i < racine.enfants.size(); i++) {
				assertEquals(racine.enfants.get(i).nom, lu.enfants.get(i).nom);
				assertEquals(racine.enfants.get(i).tags, lu.enfants.get(i).tags);
			}
		}
	}

	@Test
	void binaireGrapheProfond() throws Exception {
		// chaîne de 50 000 objets : bien au-delà de la lecture directe (récursive), la pile d'actions prend le relais
		Noeud dernier = null;
		for (int i = 0; i < 50_000; i++) {
			final Noeud n = new Noeud();
			n.id = "p" + i;
			n.entier = i;
			n.parent = dernier;
			if (i % 1000 == 0) { // collections et objets mêlés dans la profondeur
				final Noeud enfant = new Noeud();
				enfant.id = "e" + i;
				n.enfants.add(enfant);
			}
			dernier = n;
		}
		Noeud lu = BINARY.roundTrip(dernier);
		for (int i = 49_999; i >= 0; i--) {
			assertEquals("p" + i, lu.id);
			assertEquals(i, lu.entier);
			assertEquals(i % 1000 == 0 ? 1 : 0, lu.enfants.size());
			lu = lu.parent;
		}
		assertEquals(null, lu);
	}

	@Test
	void jsonGrapheProfond() throws Exception {
		// écriture récursive bornée : au-delà, la pile prend le relais (pas de StackOverflowError)
		Noeud dernier = null;
		for (int i = 0; i < 20_000; i++) {
			final Noeud n = new Noeud();
			n.id = "p" + i;
			n.entier = i;
			n.parent = dernier;
			dernier = n;
		}
		Noeud lu = JSON.roundTrip(dernier);
		for (int i = 19_999; i >= 0; i--) {
			assertEquals("p" + i, lu.id);
			assertEquals(i, lu.entier);
			lu = lu.parent;
		}
		assertEquals(null, lu);
	}

	@Test
	void binaireAppelsSuccessifs() throws Exception {
		// les tables sont réutilisées d'un appel à l'autre sur un même thread : aucun état ne doit fuir,
		// y compris après un flux tronqué
		for (int i = 0; i < 3; i++) {
			grapheComplet(BINARY);
			sansId(BINARY);
			identite(BINARY);
		}
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		BinaryMarshaller.toCompleteBinary(graphe(""), out);
		final byte[] octets = out.toByteArray();
		final byte[] tronque = java.util.Arrays.copyOf(octets, octets.length / 2);
		org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
				() -> BinaryUnmarshaller.fromBinary(new ByteArrayInputStream(tronque)));
		verifie(graphe(""), BinaryUnmarshaller.fromBinary(new ByteArrayInputStream(octets)));
		grapheComplet(BINARY);
	}

	@Test
	void binaireChainesEtDecimaux() throws Exception {
		final Melange m = new Melange();
		m.valeurs.put("long", "é".repeat(70_000) + "fin");
		m.valeurs.put("unicode", "\uD83D\uDE00 surrogate isolé \uD800 nul \u0000 \u07FF \u0800 \uFFFF");
		m.valeurs.put("vide", "");
		m.valeurs.put("negatif", new BigDecimal("-1.5E+30"));
		m.valeurs.put("precis", new BigDecimal("123456789012345678901234567890.123456789"));
		m.valeurs.put("zero", BigDecimal.ZERO);
		m.valeurs.put("petit", new BigDecimal("1E-400"));
		m.valeurs.put("long max", new BigDecimal(Long.MAX_VALUE).movePointLeft(3));
		m.valeurs.put("long min", BigDecimal.valueOf(Long.MIN_VALUE, 2));
		// valeurs immuables : écrites sans identité, une instance partagée reste égale
		final BigDecimal partage = new BigDecimal("42.00");
		m.valeurs.put("partage 1", partage);
		m.valeurs.put("partage 2", partage);
		m.valeurs.put("bigint", new java.math.BigInteger("-123456789012345678901234567890"));
		for (int i = 0; i < 300; i++)
			m.nombres.put(i, (long) i * i);
		// toutes les longueurs autour des tailles d'en-tête (varint 1 à 3 octets), ASCII ou non
		for (int n = 0; n <= 300; n++) {
			m.valeurs.put("ascii" + n, "a".repeat(n));
			m.valeurs.put("latin" + n, "é".repeat(n));
			m.valeurs.put("mixte" + n, "x€".repeat(n / 2) + (n % 2 == 0 ? "" : "y"));
		}
		final Melange lu = BINARY.roundTrip(m);
		assertEquals(m.valeurs, lu.valeurs);
		assertEquals(m.nombres, lu.nombres);
	}
}
