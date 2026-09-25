package io.github.giraudsa.fidelis.utils.champ;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;

class GenerateurAccesTest {

	@SuppressWarnings("unused")
	private static class Tous {
		private boolean z;
		private byte b;
		private short s;
		private char c;
		private int i;
		private long l;
		private float f;
		private double d;
		private String texte;
		private int[] tableau;
		private String[][] matrice;
		private List<String> liste;
		private final String fige = "fige";
	}

	private static AccesChamp acces(final String nom) throws Exception {
		final Field champ = Tous.class.getDeclaredField(nom);
		champ.setAccessible(true);
		return GenerateurAcces.cree(champ);
	}

	private static void allerRetour(final String nom, final Object valeur) throws Exception {
		assumeTrue(Runtime.version().feature() >= 15, "classes cachées : JDK 15+");
		final AccesChamp acces = acces(nom);
		assertFalse(acces instanceof GenerateurAcces.AccesParReflexion, "accès généré attendu pour " + nom);
		final Tous t = new Tous();
		acces.set(t, valeur);
		if (valeur instanceof Object[])
			assertArrayEquals((Object[]) valeur, (Object[]) acces.get(t));
		else
			assertEquals(valeur, acces.get(t));
		// setter primitif, sans boxing
		final Tous p = new Tous();
		if (valeur instanceof Boolean)
			acces.setBoolean(p, (Boolean) valeur);
		else if (valeur instanceof Byte)
			acces.setByte(p, (Byte) valeur);
		else if (valeur instanceof Short)
			acces.setShort(p, (Short) valeur);
		else if (valeur instanceof Character)
			acces.setChar(p, (Character) valeur);
		else if (valeur instanceof Integer)
			acces.setInt(p, (Integer) valeur);
		else if (valeur instanceof Long)
			acces.setLong(p, (Long) valeur);
		else if (valeur instanceof Float)
			acces.setFloat(p, (Float) valeur);
		else if (valeur instanceof Double)
			acces.setDouble(p, (Double) valeur);
		else
			return;
		assertEquals(valeur, acces.get(p));
		// getter primitif, sans boxing
		final Object lu;
		if (valeur instanceof Boolean)
			lu = acces.getBoolean(p);
		else if (valeur instanceof Byte)
			lu = acces.getByte(p);
		else if (valeur instanceof Short)
			lu = acces.getShort(p);
		else if (valeur instanceof Character)
			lu = acces.getChar(p);
		else if (valeur instanceof Integer)
			lu = acces.getInt(p);
		else if (valeur instanceof Long)
			lu = acces.getLong(p);
		else if (valeur instanceof Float)
			lu = acces.getFloat(p);
		else
			lu = acces.getDouble(p);
		assertEquals(valeur, lu);
	}

	@Test
	void champsPrivesDeTousTypes() throws Exception {
		allerRetour("z", true);
		allerRetour("b", (byte) -3);
		allerRetour("s", (short) 300);
		allerRetour("c", 'é');
		allerRetour("i", Integer.MIN_VALUE);
		allerRetour("l", Long.MAX_VALUE);
		allerRetour("f", 1.5f);
		allerRetour("d", -2.25);
		allerRetour("texte", "abc");
		allerRetour("liste", List.of("a"));
		final int[] tableau = { 1, 2 };
		final AccesChamp acces = acces("tableau");
		final Tous t = new Tous();
		acces.set(t, tableau);
		assertTrue(acces.get(t) == tableau);
		allerRetour("matrice", new String[][] { { "x" } });
	}

	@Test
	void champFinalParReflexion() throws Exception {
		final AccesChamp acces = acces("fige");
		assertTrue(acces instanceof GenerateurAcces.AccesParReflexion);
		final Tous t = new Tous();
		acces.set(t, "modifié");
		assertEquals("modifié", acces.get(t));
	}

	@Test
	void conversionsCommeLaReflexion() throws Exception {
		// Champ repasse par Field.set quand l'accès direct refuse la valeur : élargissement int -> long
		final Field champ = Tous.class.getDeclaredField("l");
		final Champ c = FabriqueChamp.createChamp(champ);
		champ.setAccessible(true);
		final Tous t = new Tous();
		c.set(t, 42, null);
		assertEquals(42L, t.l);
	}
}
