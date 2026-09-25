package bench.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.giraudsa.fidelis.annotations.Relation;
import io.github.giraudsa.fidelis.annotations.TypeRelation;

public class Commande implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private long numero;
	private Date date;
	private Statut statut;
	private String commentaire;
	@Relation(type = TypeRelation.COMPOSITION)
	private Client client;
	@Relation(type = TypeRelation.COMPOSITION)
	private List<Ligne> lignes = new ArrayList<>();
	@Relation(type = TypeRelation.COMPOSITION)
	private Map<String, String> attributs = new LinkedHashMap<>();
	private List<String> tags = new ArrayList<>();

	public Commande() {
	}

	public Commande(final String id, final long numero, final Date date, final Statut statut,
			final String commentaire, final Client client) {
		this.id = id;
		this.numero = numero;
		this.date = date;
		this.statut = statut;
		this.commentaire = commentaire;
		this.client = client;
	}

	public List<Ligne> getLignes() {
		return lignes;
	}

	public Map<String, String> getAttributs() {
		return attributs;
	}

	public List<String> getTags() {
		return tags;
	}
}
