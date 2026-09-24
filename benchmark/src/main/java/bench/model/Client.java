package bench.model;

import java.io.Serializable;

import giraudsa.marshall.annotations.Relation;
import giraudsa.marshall.annotations.TypeRelation;

public class Client implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String nom;
	private String email;
	private int age;
	private boolean premium;
	@Relation(type = TypeRelation.COMPOSITION)
	private Adresse adresse;

	public Client() {
	}

	public Client(final String id, final String nom, final String email, final int age, final boolean premium,
			final Adresse adresse) {
		this.id = id;
		this.nom = nom;
		this.email = email;
		this.age = age;
		this.premium = premium;
		this.adresse = adresse;
	}
}
