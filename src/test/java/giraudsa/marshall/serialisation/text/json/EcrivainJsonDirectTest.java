package giraudsa.marshall.serialisation.text.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import giraudsa.marshall.annotations.Relation;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.strategie.StrategieDeSerialisation;
import giraudsa.marshall.strategie.StrategieParComposition;
import giraudsa.marshall.strategie.StrategieParCompositionOuAgregationEtClasseConcrete;
import giraudsa.marshall.strategie.StrategieSerialisationComplete;
import utils.ConfigurationMarshalling;

/** L'écriture directe doit produire exactement le texte des actions, quels que soient la stratégie et les réglages. */
class EcrivainJsonDirectTest {

	enum Couleur {
		BLEU, ROUGE {
			@Override
			public String toString() {
				return "rouge !";
			}
		}
	}

	static class Base {
		String id;
		String nom;
	}

	static class Derivee extends Base {
		int niveau;
	}

	static class Tout {
		String id;
		int i;
		long l;
		double d;
		float f;
		short s;
		byte b;
		char c;
		boolean v;
		Integer boiteI;
		Long boiteL;
		Double boiteD;
		Boolean boiteB;
		Character boiteC;
		String texte;
		Date date;
		Date horodatage;
		UUID uuid;
		Couleur couleur;
		Couleur couleurAvecCorps;
		BigDecimal montant;
		BigInteger grand;
		Locale langue;
		Currency devise;
		URI adresse;
		Calendar calendrier;
		BitSet bits;
		AtomicBoolean drapeau;
		AtomicInteger compteur;
		int[] tableau;
		String[] noms;
		Object libre;
		Object libreLong;
		Object libreListe;
		Object libreDate;
		Base base;
		@Relation(type = TypeRelation.COMPOSITION)
		Base baseDerivee;
		@Relation(type = TypeRelation.AGGREGATION)
		Base agregee;
		Tout parent;
		@Relation(type = TypeRelation.COMPOSITION)
		List<Tout> enfants = new ArrayList<>();
		@Relation(type = TypeRelation.COMPOSITION)
		LinkedList<Object> melange = new LinkedList<>();
		@Relation(type = TypeRelation.COMPOSITION)
		Set<String> ensemble = new HashSet<>();
		@Relation(type = TypeRelation.COMPOSITION)
		Map<String, Integer> notes = new LinkedHashMap<>();
		@Relation(type = TypeRelation.COMPOSITION)
		Map<Couleur, List<String>> parCouleur = new TreeMap<>();
		@Relation(type = TypeRelation.COMPOSITION)
		Map<Object, Object> quelconque = new HashMap<>();
		@Relation(type = TypeRelation.COMPOSITION)
		List<List<Integer>> matrice = new ArrayList<>();
		List<Base> liste;
	}

	static class SansId {
		String valeur;
		@Relation(type = TypeRelation.COMPOSITION)
		List<SansId> liste = new ArrayList<>();
		SansId autre;
	}

	private static Tout tout(final String id) {
		final Tout t = new Tout();
		t.id = id;
		t.i = -42;
		t.l = Long.MIN_VALUE;
		t.d = 3.25e-7;
		t.f = -1.5f;
		t.s = Short.MAX_VALUE;
		t.b = -128;
		t.c = '"';
		t.v = true;
		t.boiteI = 7;
		t.boiteL = 1L << 40;
		t.boiteD = Double.NaN;
		t.boiteB = false;
		t.boiteC = ' ';
		t.texte = "texte \"échappé\" \\ / \n\t \u0001 <>&='   中 fin";
		t.date = new Date(1_700_000_000_123L);
		t.horodatage = new java.sql.Timestamp(1_700_000_000_123L);
		t.uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		t.couleur = Couleur.BLEU;
		t.couleurAvecCorps = Couleur.ROUGE;
		t.montant = new BigDecimal("-12345.678901234567890");
		t.grand = new BigInteger("123456789012345678901234567890");
		t.langue = Locale.FRANCE;
		t.devise = Currency.getInstance("EUR");
		t.adresse = URI.create("http://exemple.fr/a?b=c&d=é");
		final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		cal.setTimeInMillis(123456789L);
		t.calendrier = cal;
		t.bits = BitSet.valueOf(new long[] { 0b1011 });
		t.drapeau = new AtomicBoolean(true);
		t.compteur = new AtomicInteger(5);
		t.tableau = new int[] { 1, -2 };
		t.noms = new String[] { "x", null };
		t.libre = 12;
		t.libreLong = 12L;
		t.libreListe = new ArrayList<>(Arrays.asList("a", 1));
		t.libreDate = new Date(0);
		final Base base = new Base();
		base.id = id + "-base";
		base.nom = "base";
		t.base = base;
		final Derivee derivee = new Derivee();
		derivee.id = id + "-derivee";
		derivee.niveau = 3;
		t.baseDerivee = derivee;
		t.agregee = base; // référence partagée
		t.liste = new ArrayList<>(Arrays.asList(base, derivee, null));
		for (int k = 0; k < 3; k++) {
			final Tout e = new Tout();
			e.id = id + "-enfant" + k;
			e.parent = t; // cycle
			e.date = t.date;
			t.enfants.add(e);
		}
		t.melange.addAll(Arrays.asList(1, "deux", 3L, 4.5, 2.5f, new Date(0), Couleur.BLEU, null, true, 'c',
				UUID.fromString("00000000-0000-0000-0000-000000000001"), new BigDecimal("1.10"), t.enfants.get(1),
				new HashMap<>(Map.of("k", 1)), new LinkedList<>(List.of(1)), new int[] { 9 }));
		t.ensemble.addAll(Arrays.asList("a", "", "b"));
		t.notes.put("un", 1);
		t.notes.put("deux, \"trois\"", null);
		t.parCouleur.put(Couleur.BLEU, new ArrayList<>(Arrays.asList("ciel", "mer")));
		t.quelconque.put(1, "un");
		t.quelconque.put("liste", new ArrayList<>(List.of(1, 2)));
		t.quelconque.put(null, null);
		t.matrice.add(Arrays.asList(1, 2));
		t.matrice.add(new ArrayList<>());
		return t;
	}

	private static SansId sansId(final int profondeur) {
		final SansId racine = new SansId();
		racine.valeur = "racine";
		SansId courant = racine;
		for (int k = 0; k < profondeur; k++) {
			final SansId e = new SansId();
			e.valeur = "n" + k;
			e.autre = racine;
			courant.liste.add(e);
			courant = e;
		}
		return racine;
	}

	private static final Pattern UUID_TEXTE = Pattern
			.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

	/** les faux ids (UUID aléatoires) renumérotés dans l'ordre d'apparition. */
	private static String normalise(final String json) {
		final Map<String, String> numeros = new HashMap<>();
		final Matcher m = UUID_TEXTE.matcher(json);
		final StringBuilder sb = new StringBuilder();
		while (m.find())
			m.appendReplacement(sb, numeros.computeIfAbsent(m.group(), k -> "uuid" + numeros.size()));
		m.appendTail(sb);
		return sb.toString();
	}

	private static void compare(final Object o) throws Exception {
		final StrategieDeSerialisation[] strategies = { new StrategieSerialisationComplete(),
				new StrategieParComposition(), new StrategieParCompositionOuAgregationEtClasseConcrete() };
		for (final StrategieDeSerialisation strategie : strategies)
			for (final boolean writeType : new boolean[] { true, false }) {
				final String attendu = JsonMarshaller.toJsonParActions(o, strategie, writeType);
				final String direct = JsonMarshaller.toJson(o, strategie, null, writeType);
				assertEquals(normalise(attendu), normalise(direct),
						strategie.getClass().getSimpleName() + " writeType=" + writeType);
			}
	}

	private static void compareAussiEnIdsUniversels(final Object o) throws Exception {
		compare(o);
		final Field champ = ConfigurationMarshalling.class.getDeclaredField("idEstUniversel");
		champ.setAccessible(true);
		final boolean avant = champ.getBoolean(null);
		champ.setBoolean(null, true);
		try {
			compare(o);
		} finally {
			champ.setBoolean(null, avant);
		}
	}

	@Test
	void grapheVarie() throws Exception {
		compareAussiEnIdsUniversels(tout("t"));
	}

	@Test
	void fauxIdsEtGrapheProfond() throws Exception {
		compareAussiEnIdsUniversels(sansId(3));
		compare(sansId(2000)); // au-delà de la profondeur d'écriture directe
	}

	@Test
	void racinesDiverses() throws Exception {
		for (final Object o : new Object[] { "chaine", 12, 12L, 2.5, true, new Date(5), Couleur.ROUGE,
				UUID.randomUUID(), new BigDecimal("1.5"), 'x', new int[] { 1 },
				new ArrayList<>(Arrays.asList(tout("a"), null, "x")), new LinkedList<>(List.of(1, 2)),
				new HashSet<>(List.of("a")), new LinkedHashMap<>(Map.of("k", tout("m"))), new TreeMap<>(Map.of(1, 2)),
				new ArrayList<>(), new HashMap<>() })
			compare(o);
	}
}
