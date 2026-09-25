package bench.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Ligne implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String produit;
	private int quantite;
	private double remise;
	private BigDecimal prixUnitaire;

	public Ligne() {
	}

	public Ligne(final String id, final String produit, final int quantite, final double remise,
			final BigDecimal prixUnitaire) {
		this.id = id;
		this.produit = produit;
		this.quantite = quantite;
		this.remise = remise;
		this.prixUnitaire = prixUnitaire;
	}
}
