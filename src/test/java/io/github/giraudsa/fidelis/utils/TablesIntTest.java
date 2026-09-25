package io.github.giraudsa.fidelis.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TablesIntTest {

	@Test
	void identiteVideApresPeuOuBeaucoupDEntrees() {
		final IdentiteIntMap table = new IdentiteIntMap(16);
		for (final int n : new int[] { 3, 5000, 2, 40, 100_000, 1 }) {
			final Object[] cles = new Object[n];
			for (int i = 0; i < n; i++) {
				cles[i] = new Object();
				assertEquals(IdentiteIntMap.ABSENT, table.putIfAbsent(cles[i], i));
			}
			for (int i = 0; i < n; i++)
				assertEquals(i, table.get(cles[i]));
			table.vide();
			for (int i = 0; i < n; i++)
				assertEquals(IdentiteIntMap.ABSENT, table.get(cles[i]), "entrée restée après vide()");
		}
	}

	@Test
	void egaliteVideApresPeuOuBeaucoupDEntrees() {
		final EgaliteIntMap table = new EgaliteIntMap(16);
		for (final int n : new int[] { 3, 5000, 2, 40, 100_000, 1 }) {
			// valeurs > 0 : ce sont des smallIds
			for (int i = 0; i < n; i++)
				assertEquals(IdentiteIntMap.ABSENT, table.putIfAbsent("cle" + i, i + 1));
			for (int i = 0; i < n; i++)
				assertEquals(i + 1, table.putIfAbsent(new String("cle" + i), 7)); // égalité, pas identité
			table.vide();
			for (int i = 0; i < n; i++)
				assertEquals(IdentiteIntMap.ABSENT, table.putIfAbsent("cle" + i, i + 1), "entrée restée après vide()");
			table.vide();
		}
	}
}
