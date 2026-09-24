package utils.champ;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import utils.TypeExtension;

class GenerateurSerialiseursTest {

	@SuppressWarnings("unused")
	private static class Exemple {
		private String id;
		private boolean z;
		private byte b;
		private short s;
		private char c;
		private int i;
		private long l;
		private float f;
		private double d;
		private String texte;
		private List<String> liste;
		private Object quelconque;
	}

	private static class AvecFinal {
		private String id;
		private final int fige = 1;
	}

	/** Contexte d'écriture de test : enregistre les appels. */
	public static final class Enregistreur {
		final List<Object> valeurs = new ArrayList<>();
		final List<FieldInformations> champs = new ArrayList<>();

		private void note(final Object v, final FieldInformations champ) {
			valeurs.add(v);
			champs.add(champ);
		}

		public void ecritObjet(final Object v, final FieldInformations c) {
			note(v, c);
		}

		public void ecritBoolean(final boolean v, final FieldInformations c) {
			note(v, c);
		}

		public void ecritByte(final byte v, final FieldInformations c) {
			note(v, c);
		}

		public void ecritShort(final short v, final FieldInformations c) {
			note(v, c);
		}

		public void ecritChar(final char v, final FieldInformations c) {
			note(v, c);
		}

		public void ecritInt(final int v, final FieldInformations c) {
			note(v, c);
		}

		public void ecritLong(final long v, final FieldInformations c) {
			note(v, c);
		}

		public void ecritFloat(final float v, final FieldInformations c) {
			note(v, c);
		}

		public void ecritDouble(final double v, final FieldInformations c) {
			note(v, c);
		}
	}

	/** Contexte de lecture de test : renvoie les valeurs d'une liste, dans l'ordre. */
	public static final class Fournisseur {
		final List<Object> valeurs;
		int prochain;

		Fournisseur(final List<Object> valeurs, final int debut) {
			this.valeurs = valeurs;
			prochain = debut;
		}

		public Object litObjet(final FieldInformations c) {
			return valeurs.get(prochain++);
		}

		public boolean litBoolean(final FieldInformations c) {
			return (Boolean) valeurs.get(prochain++);
		}

		public byte litByte(final FieldInformations c) {
			return (Byte) valeurs.get(prochain++);
		}

		public short litShort(final FieldInformations c) {
			return (Short) valeurs.get(prochain++);
		}

		public char litChar(final FieldInformations c) {
			return (Character) valeurs.get(prochain++);
		}

		public int litInt(final FieldInformations c) {
			return (Integer) valeurs.get(prochain++);
		}

		public long litLong(final FieldInformations c) {
			return (Long) valeurs.get(prochain++);
		}

		public float litFloat(final FieldInformations c) {
			return (Float) valeurs.get(prochain++);
		}

		public double litDouble(final FieldInformations c) {
			return (Double) valeurs.get(prochain++);
		}
	}

	@Test
	void ecrivainEtLecteurGeneres() throws Exception {
		assumeTrue(Runtime.version().feature() >= 15, "classes cachées : JDK 15+");
		final Champ[] champs = TypeExtension.getChampsDuType(Exemple.class).getTableauChamps();
		final EcrivainChamps ecrivain = GenerateurSerialiseurs.ecrivain(Exemple.class, champs, Enregistreur.class);
		assertNotNull(ecrivain);
		final Exemple e = new Exemple();
		e.id = "x";
		e.z = true;
		e.b = -3;
		e.s = 300;
		e.c = 'é';
		e.i = -7;
		e.l = Long.MAX_VALUE;
		e.f = 1.5f;
		e.d = -2.25;
		e.texte = "t";
		e.liste = List.of("a");
		e.quelconque = 42;
		final Enregistreur enregistreur = new Enregistreur();
		ecrivain.ecrit(e, enregistreur, champs);
		assertEquals(champs.length, enregistreur.valeurs.size());
		for (int k = 0; k < champs.length; k++) {
			assertEquals(champs[k].get(e), enregistreur.valeurs.get(k), champs[k].getName());
			assertEquals(champs[k], enregistreur.champs.get(k));
		}
		// relecture à partir du champ 1 (l'id est lu par l'appelant)
		final LecteurChamps lecteur = GenerateurSerialiseurs.lecteur(Exemple.class, champs, 1, Fournisseur.class);
		assertNotNull(lecteur);
		final Exemple lu = new Exemple();
		lecteur.lit(lu, new Fournisseur(enregistreur.valeurs, 1), champs);
		assertNull(lu.id);
		for (int k = 1; k < champs.length; k++)
			assertEquals(champs[k].get(e), champs[k].get(lu), champs[k].getName());
	}

	@Test
	void pasDeGenerationSiChampFinal() {
		final Champ[] champs = TypeExtension.getChampsDuType(AvecFinal.class).getTableauChamps();
		assertNull(GenerateurSerialiseurs.ecrivain(AvecFinal.class, champs, Enregistreur.class));
	}
}
