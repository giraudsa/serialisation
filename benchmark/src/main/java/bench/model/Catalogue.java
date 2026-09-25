package bench.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.github.giraudsa.fidelis.annotations.Relation;
import io.github.giraudsa.fidelis.annotations.TypeRelation;

/** Racine du jeu de données : un arbre (sans partage de références) de commandes. */
public class Catalogue implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String nom;
	@Relation(type = TypeRelation.COMPOSITION)
	private List<Commande> commandes = new ArrayList<>();

	public Catalogue() {
	}

	public List<Commande> getCommandes() {
		return commandes;
	}

	/**
	 * Construit un catalogue déterministe de {@code nbCommandes} commandes, chacune avec son client, son adresse et
	 * {@code nbLignes} lignes. Nombre d'objets métier ≈ nbCommandes × (3 + nbLignes).
	 */
	public static Catalogue genere(final int nbCommandes, final int nbLignes) {
		final Random r = new Random(42);
		final Statut[] statuts = Statut.values();
		final Catalogue c = new Catalogue();
		c.id = "catalogue";
		c.nom = "Catalogue de test";
		for (int i = 0; i < nbCommandes; i++) {
			final Adresse a = new Adresse("adr" + i, r.nextInt(200) + " rue de la République", "Lyon",
					String.valueOf(69000 + r.nextInt(10)), "France");
			final Client cl = new Client("cli" + i, "Client n°" + i, "client" + i + "@exemple.fr",
					18 + r.nextInt(70), r.nextBoolean(), a);
			final Commande cmd = new Commande("cmd" + i, 1_000_000L + i, new Date(1_700_000_000_000L + i * 60_000L),
					statuts[r.nextInt(statuts.length)], "Commande \"urgente\" à livrer avant 18h", cl);
			for (int j = 0; j < nbLignes; j++)
				cmd.getLignes().add(new Ligne("l" + i + "-" + j, "Produit-" + r.nextInt(1000), 1 + r.nextInt(20),
						r.nextDouble(), BigDecimal.valueOf(r.nextInt(100_000), 2)));
			cmd.getAttributs().put("canal", r.nextBoolean() ? "web" : "magasin");
			cmd.getAttributs().put("priorite", String.valueOf(r.nextInt(5)));
			cmd.getTags().add("tag" + r.nextInt(10));
			cmd.getTags().add("tag" + r.nextInt(10));
			c.commandes.add(cmd);
		}
		return c;
	}
}
