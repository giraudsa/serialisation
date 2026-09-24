package bench.model;

import java.io.Serializable;

public class Adresse implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String rue;
	private String ville;
	private String codePostal;
	private String pays;

	public Adresse() {
	}

	public Adresse(final String id, final String rue, final String ville, final String codePostal, final String pays) {
		this.id = id;
		this.rue = rue;
		this.ville = ville;
		this.codePostal = codePostal;
		this.pays = pays;
	}
}
