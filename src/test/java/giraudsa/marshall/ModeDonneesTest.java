package giraudsa.marshall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.UnmarshallExeption;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;

/** Mode données : arbre des valeurs, sans type ni identité. */
class ModeDonneesTest {

	enum Couleur {
		BLEU, ROUGE
	}

	static class Adresse {
		String rue;
		String ville;
	}

	static class Base {
		String nom;
	}

	static class Derivee extends Base {
		int niveau;
	}

	static class Personne {
		String id;
		String nom;
		int age;
		long grand;
		double taille;
		float petit;
		boolean actif;
		char lettre;
		Integer boite;
		Date naissance;
		UUID uuid;
		Couleur couleur;
		BigDecimal montant;
		Locale langue;
		Currency devise;
		Adresse adresse;
		Adresse autreAdresse;
		List<Adresse> adresses = new ArrayList<>();
		Map<String, Integer> notes = new LinkedHashMap<>();
		List<List<Integer>> matrice = new ArrayList<>();
		int[] tableau;
		String[] noms;
		Base base;
		Personne ami;
	}

	/** sans champ id : un faux id serait écrit en JSON normal. */
	static class SansId {
		String valeur;
		List<SansId> enfants = new ArrayList<>();
	}

	private static Personne personne() {
		final Personne p = new Personne();
		p.id = "p1";
		p.nom = "texte \"échappé\" \\ \n\t <>&='   中";
		p.age = -42;
		p.grand = Long.MIN_VALUE;
		p.taille = 1.7500000000000002;
		p.petit = -0.5f;
		p.actif = true;
		p.lettre = '"';
		p.boite = 7;
		p.naissance = new Date(1_700_000_000_123L);
		p.uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
		p.couleur = Couleur.ROUGE;
		p.montant = new BigDecimal("-12345.678901234567890");
		p.langue = Locale.FRANCE;
		p.devise = Currency.getInstance("EUR");
		final Adresse a = new Adresse();
		a.rue = "1 rue de la Paix";
		a.ville = "Paris";
		p.adresse = a;
		p.autreAdresse = a; // partagée : dupliquée en mode données
		p.adresses.add(a);
		p.adresses.add(null);
		p.notes.put("un", 1);
		p.notes.put("deux", null);
		p.matrice.add(Arrays.asList(1, 2));
		p.matrice.add(new ArrayList<>());
		p.tableau = new int[] { 1, -2, 3 };
		p.noms = new String[] { "x", null };
		final Base b = new Base();
		b.nom = "base";
		p.base = b;
		final Personne ami = new Personne();
		ami.id = "p2";
		ami.nom = "ami";
		p.ami = ami;
		return p;
	}

	@Test
	void allerRetour() throws Exception {
		final Personne p = personne();
		final String json = JsonMarshaller.toDataJson(p);
		assertFalse(json.contains("__type"), json);
		final Personne lu = JsonUnmarshaller.fromDataJson(json, Personne.class);
		assertEquals(p.nom, lu.nom);
		assertEquals(p.age, lu.age);
		assertEquals(p.grand, lu.grand);
		assertEquals(p.taille, lu.taille);
		assertEquals(p.petit, lu.petit);
		assertEquals(p.lettre, lu.lettre);
		assertEquals(p.naissance, lu.naissance);
		assertEquals(p.uuid, lu.uuid);
		assertEquals(p.couleur, lu.couleur);
		assertEquals(p.montant, lu.montant);
		assertEquals(p.langue, lu.langue);
		assertEquals(p.devise, lu.devise);
		assertEquals(p.notes, lu.notes);
		assertEquals(p.matrice, lu.matrice);
		assertEquals(Arrays.toString(p.tableau), Arrays.toString(lu.tableau));
		assertEquals(Arrays.toString(p.noms), Arrays.toString(lu.noms));
		assertEquals("p2", lu.ami.id);
		// l'objet partagé est dupliqué : deux objets égaux mais distincts
		assertNotSame(lu.adresse, lu.autreAdresse);
		assertEquals(lu.adresse.rue, lu.autreAdresse.rue);
		assertEquals(2, lu.adresses.size());
		assertEquals("base", lu.base.nom);
		// relecture stable : même texte
		assertEquals(json, JsonMarshaller.toDataJson(lu));
	}

	@Test
	void sousClasseSansType() throws Exception {
		// sans type écrit, la sous-classe est relue comme le type déclaré : ses champs propres sont inconnus de lui
		final Personne p = new Personne();
		final Derivee d = new Derivee();
		d.nom = "dérivée";
		d.niveau = 3;
		p.base = d;
		final String json = JsonMarshaller.toDataJson(p);
		// modèle contraignant (par défaut) : erreur plutôt que perte silencieuse
		assertThrows(UnmarshallExeption.class, () -> JsonUnmarshaller.fromDataJson(json, Personne.class));
		utils.ConfigurationMarshalling.setContrainteModel(false);
		try {
			final Personne lu = JsonUnmarshaller.fromDataJson(json, Personne.class);
			assertEquals(Base.class, lu.base.getClass());
			assertEquals("dérivée", lu.base.nom);
		} finally {
			utils.ConfigurationMarshalling.setContrainteModel(true);
		}
	}

	@Test
	void pasDeFauxId() throws Exception {
		final SansId s = new SansId();
		s.valeur = "racine";
		final SansId e = new SansId();
		e.valeur = "enfant";
		s.enfants.add(e);
		final String json = JsonMarshaller.toDataJson(s);
		assertFalse(json.contains("\"id\""), json);
		final SansId lu = JsonUnmarshaller.fromDataJson(json, SansId.class);
		assertEquals("enfant", lu.enfants.get(0).valeur);
	}

	@Test
	void cycleRefuse() {
		final Personne p = new Personne();
		p.id = "boucle";
		p.ami = p;
		assertThrows(MarshallExeption.class, () -> JsonMarshaller.toDataJson(p));
	}

	@Test
	void jsonNormalRelu() throws Exception {
		final Personne p = personne();
		p.base = null;
		final Personne lu = JsonUnmarshaller.fromDataJson(JsonMarshaller.toCompleteJson(p), Personne.class);
		assertEquals(p.nom, lu.nom);
		assertEquals(p.notes, lu.notes);
		assertEquals(p.devise, lu.devise);
	}

	@Test
	void blancsEtErreurs() throws Exception {
		final String json = "{\n\t\"nom\" :\t\"n\",\r\"age\"\t: 3 ,\n\t\"adresse\":{}\n}";
		final Personne lu = JsonUnmarshaller.fromDataJson(json, Personne.class);
		assertEquals("n", lu.nom);
		assertEquals(3, lu.age);
		assertTrue(lu.adresse != null);
		assertThrows(UnmarshallExeption.class, () -> JsonUnmarshaller.fromDataJson("{\"age\":}", Personne.class));
		assertThrows(UnmarshallExeption.class, () -> JsonUnmarshaller.fromDataJson("{\"age\":3", Personne.class));
	}

	@Test
	void grapheProfond() throws Exception {
		SansId racine = new SansId();
		SansId courant = racine;
		for (int i = 0; i < 300; i++) {
			final SansId e = new SansId();
			e.valeur = "n" + i;
			courant.enfants.add(e);
			courant = e;
		}
		final SansId lu = JsonUnmarshaller.fromDataJson(JsonMarshaller.toDataJson(racine), SansId.class);
		SansId l = lu;
		for (int i = 0; i < 300; i++)
			l = l.enfants.get(0);
		assertEquals("n299", l.valeur);
	}
}
