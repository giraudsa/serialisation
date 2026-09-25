package giraudsa.marshall.deserialisation;

import java.util.IdentityHashMap;
import java.util.Map;

import giraudsa.marshall.exception.InstanciationException;

/**
 * Cache des objets par (id, classe exacte). Une map d'id par classe, sans clé composée allouée à chaque accès ; la
 * dernière classe consultée est gardée (les accès se suivent souvent pour une même classe).
 */
public class CacheIdNonUniversel implements CacheObject {

	/**
	 * Table id → objet à adressage ouvert (même sémantique qu'une HashMap&lt;String, Object&gt; : clés comparées par
	 * equals), sans nœud alloué par entrée ; le hachage de chaque clé est gardé pour écarter les comparaisons
	 * inutiles. Recherche et insertion en un seul parcours.
	 */
	private static final class TableIds {
		private String[] cles = new String[16];
		private Object[] objets = new Object[16];
		private int[] hachages = new int[16];
		private int taille;

		private static int melange(final int h) {
			return h ^ h >>> 16;
		}

		/** @return l'indice de la clé, ou -(indice libre où l'insérer) - 1. */
		private int cherche(final String id, final int h) {
			final String[] c = cles;
			final int masque = c.length - 1;
			int i = melange(h) & masque;
			String cle;
			while ((cle = c[i]) != null) {
				if (hachages[i] == h && (cle == id || cle.equals(id)))
					return i;
				i = i + 1 & masque;
			}
			return -i - 1;
		}

		private Object get(final String id) {
			final int i = cherche(id, id.hashCode());
			return i >= 0 ? objets[i] : null;
		}

		private void put(final String id, final Object objet) {
			final int h = id.hashCode();
			final int i = cherche(id, h);
			if (i >= 0)
				objets[i] = objet;
			else
				insere(-i - 1, id, h, objet);
		}

		private void insere(final int i, final String id, final int h, final Object objet) {
			cles[i] = id;
			hachages[i] = h;
			objets[i] = objet;
			if (++taille * 2 > cles.length)
				agrandit();
		}

		/** croissance par 4 : peu de recopies pour les gros graphes. */
		private void agrandit() {
			final String[] anciennesCles = cles;
			final Object[] anciensObjets = objets;
			final int[] anciensHachages = hachages;
			final int n = anciennesCles.length * 4;
			cles = new String[n];
			objets = new Object[n];
			hachages = new int[n];
			final int masque = n - 1;
			for (int j = 0; j < anciennesCles.length; j++)
				if (anciennesCles[j] != null) {
					int i = melange(anciensHachages[j]) & masque;
					while (cles[i] != null)
						i = i + 1 & masque;
					cles[i] = anciennesCles[j];
					objets[i] = anciensObjets[j];
					hachages[i] = anciensHachages[j];
				}
		}
	}

	private final Map<Class<?>, TableIds> dicoClassToIdToObject = new IdentityHashMap<>();
	private Class<?> derniereClasse;
	private TableIds derniereTable;

	private TableIds table(final Class<?> clazz, final boolean creer) {
		if (clazz == derniereClasse)
			return derniereTable;
		TableIds table = dicoClassToIdToObject.get(clazz);
		if (table == null) {
			if (!creer)
				return null;
			table = new TableIds();
			dicoClassToIdToObject.put(clazz, table);
		}
		derniereClasse = clazz;
		derniereTable = table;
		return table;
	}

	/** Création d'un objet absent du cache. */
	public interface Creation {
		Object cree(Class<?> type) throws InstanciationException;
	}

	/**
	 * L'objet de cette classe et de cet id, créé et gardé s'il est absent (même effet que getObject puis, si absent,
	 * création et addObject, en un seul accès à la table).
	 */
	public Object obtient(final Class<?> clazz, final String id, final Creation creation)
			throws InstanciationException {
		final TableIds table = table(clazz, true);
		final int h = id.hashCode();
		final int i = table.cherche(id, h);
		if (i >= 0)
			return table.objets[i];
		final Object objet = creation.cree(clazz);
		if (objet != null) {
			if (objet.getClass() == clazz)
				table.insere(-i - 1, id, h, objet);
			else
				addObject(objet, id);
		}
		return objet;
	}

	@Override
	public <U> void addObject(final U object, final String id) {
		table(object.getClass(), true).put(id, object);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U> U getObject(final Class<U> clazz, final String id) {
		final TableIds table = table(clazz, false);
		return table == null ? null : (U) table.get(id);
	}

}
