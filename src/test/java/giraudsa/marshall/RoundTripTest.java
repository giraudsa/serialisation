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
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;

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
	private static final Format XML = new Format() {
		@Override
		public <T> T roundTrip(final T obj) throws Exception {
			return XmlUnmarshaller.fromXml(XmlMarshaller.toCompleteXml(obj));
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
	void xmlGrapheComplet() throws Exception {
		grapheComplet(XML);
	}

	@Test
	void binaireGrapheComplet() throws Exception {
		grapheComplet(BINARY);
	}

	@Test
	void jsonSansId() throws Exception {
		sansId(JSON);
	}

	@Test
	void xmlSansId() throws Exception {
		sansId(XML);
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
	void xmlIdentite() throws Exception {
		identite(XML);
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
	void xmlConcurrence() throws Exception {
		concurrence(XML);
	}

	@Test
	void binaireConcurrence() throws Exception {
		concurrence(BINARY);
	}

	@Test
	void caracteresDeControle() throws Exception {
		// \u0000 n'est représentable dans aucune version de XML : testé à part
		final String texte = "a\r\nb\u0001c\u001fd\u2029e\bf\fg\u0085h\u007fi\u2028j\r";
		for (final Format format : new Format[] { JSON, XML, BINARY }) {
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
		final Noeud racine = graphe("");
		racine.nom = "a\u0000b";
		assertEquals("a\uFFFDb", XML.roundTrip(racine).nom);
	}

	@Test
	void texteQuiRessembleAUneEntite() throws Exception {
		for (final Format format : new Format[] { JSON, XML, BINARY }) {
			final Noeud racine = graphe("");
			racine.nom = "a &lt; b &amp; c &#65; &#x42; &quot; d";
			verifie(racine, format.roundTrip(racine));
		}
	}

	@Test
	void idAvecCaracteresSpeciaux() throws Exception {
		for (final Format format : new Format[] { JSON, XML, BINARY }) {
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
		for (final Format format : new Format[] { JSON, XML, BINARY }) {
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
		for (final Format format : new Format[] { JSON, XML, BINARY }) {
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
		for (final Format format : new Format[] { JSON, XML, BINARY })
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
		for (final Format format : new Format[] { JSON, XML, BINARY }) {
			final Caracteres c = new Caracteres();
			c.c = 'é';
			c.cZero = format == XML ? '"' : 0; // \u0000 impossible en XML
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
		for (final Format format : new Format[] { JSON, XML, BINARY }) {
			final AvecOperation o = new AvecOperation();
			o.declaree = Operation.MOINS;
			o.nonDeclaree = Operation.PLUS;
			final AvecOperation lu = format.roundTrip(o);
			assertSame(Operation.MOINS, lu.declaree);
			assertSame(Operation.PLUS, lu.nonDeclaree);
		}
	}
}
