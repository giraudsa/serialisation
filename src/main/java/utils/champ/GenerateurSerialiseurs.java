package utils.champ;

import static utils.champ.ClasseCachee.descripteur;
import static utils.champ.ClasseCachee.hi;
import static utils.champ.ClasseCachee.lo;
import static utils.champ.ClasseCachee.methode;
import static utils.champ.ClasseCachee.typeInterne;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import utils.champ.ClasseCachee.Pool;

/**
 * Fabrique en mémoire, pour une classe métier, un écrivain ou un lecteur qui traite tous ses champs à la suite
 * (getfield/putfield directs, sans aiguillage par champ). Le bytecode généré est linéaire : un appel au contexte par
 * champ ; toute la logique (en-têtes, attente, références) reste dans le contexte. Rien n'est ajouté au code métier.
 */
public final class GenerateurSerialiseurs {

	private static final String CHAMP = "utils/champ/Champ";
	private static final String INFOS = "Lutils/champ/FieldInformations;";

	/**
	 * @param contexte classe dont les méthodes publiques ecritXxx(xxx, FieldInformations) seront appelées
	 * @return l'écrivain des champs, ou null si ce n'est pas possible (faux id, champ final, statique, hérité
	 *         inaccessible, JDK sans classes cachées...)
	 */
	public static EcrivainChamps ecrivain(final Class<?> type, final Champ[] champs, final Class<?> contexte) {
		if (!generable(type, champs, 0))
			return null;
		try {
			return (EcrivainChamps) ClasseCachee.instancie(type, octets(type, champs, 0, contexte, true));
		} catch (final Throwable e) { // NOSONAR : toute erreur de génération ramène au chemin générique
			return null;
		}
	}

	/**
	 * @param debut indice du premier champ à lire (les précédents, comme l'id, sont lus par l'appelant)
	 * @param contexte classe dont les méthodes publiques litXxx(FieldInformations) seront appelées
	 * @return le lecteur des champs, ou null si ce n'est pas possible
	 */
	public static LecteurChamps lecteur(final Class<?> type, final Champ[] champs, final int debut,
			final Class<?> contexte) {
		if (!generable(type, champs, debut))
			return null;
		try {
			return (LecteurChamps) ClasseCachee.instancie(type, octets(type, champs, debut, contexte, false));
		} catch (final Throwable e) { // NOSONAR : toute erreur de génération ramène au chemin générique
			return null;
		}
	}

	/**
	 * Lecteur qui passe au contexte la valeur courante de chaque champ : t.champ = ctx.litXxx(t.champ, champs[i]). Le
	 * contexte peut ainsi laisser un champ inchangé (clé absente d'un texte, par exemple) en rendant la valeur reçue.
	 *
	 * @param contexte classe dont les méthodes publiques litXxx(xxx, FieldInformations) seront appelées
	 * @return le lecteur des champs, ou null si ce n'est pas possible
	 */
	public static LecteurChamps lecteurAvecValeurCourante(final Class<?> type, final Champ[] champs, final int debut,
			final Class<?> contexte) {
		if (!generable(type, champs, debut))
			return null;
		try {
			return (LecteurChamps) ClasseCachee.instancie(type, octetsLecteurAvecValeur(type, champs, debut, contexte));
		} catch (final Throwable e) { // NOSONAR : toute erreur de génération ramène au chemin générique
			return null;
		}
	}

	private static byte[] octetsLecteurAvecValeur(final Class<?> type, final Champ[] champs, final int debut,
			final Class<?> contexte) throws IOException {
		final String nomCible = type.getName().replace('.', '/');
		final String nomContexte = contexte.getName().replace('.', '/');
		final Pool pool = new Pool();
		final int code = pool.utf8("Code");
		final int classeCible = pool.classe(nomCible);
		final int classeContexte = pool.classe(nomContexte);
		final int nom = pool.utf8("lit");
		final int desc = pool.utf8("(Ljava/lang/Object;Ljava/lang/Object;[L" + CHAMP + ";)V");

		final ByteArrayOutputStream c = new ByteArrayOutputStream();
		c.write(0x2b); // aload_1
		c.write(0xc0); // checkcast
		c.write(hi(classeCible));
		c.write(lo(classeCible));
		c.write(0x3a); // astore 4
		c.write(4);
		c.write(0x2c); // aload_2
		c.write(0xc0); // checkcast
		c.write(hi(classeContexte));
		c.write(lo(classeContexte));
		c.write(0x3a); // astore 5
		c.write(5);
		for (int k = debut; k < champs.length; k++) {
			final Field f = champs[k].getInfo();
			final Class<?> t = f.getType();
			final String d = descripteur(t);
			final String declarant = f.getDeclaringClass().getName().replace('.', '/');
			final int ref = pool.champ(declarant, f.getName(), d);
			final String dValeur = t.isPrimitive() ? d : "Ljava/lang/Object;";
			// t.champ = (Type) ctx.litXxx(t.champ, champs[k])
			final int m = pool.methode(nomContexte, "lit" + suffixe(t), "(" + dValeur + INFOS + ")" + dValeur);
			c.write(0x19); // aload 4
			c.write(4);
			c.write(0x19); // aload 5
			c.write(5);
			c.write(0x19); // aload 4
			c.write(4);
			c.write(0xb4); // getfield
			c.write(hi(ref));
			c.write(lo(ref));
			ecritChamp(c, k);
			c.write(0xb6); // invokevirtual
			c.write(hi(m));
			c.write(lo(m));
			if (!t.isPrimitive() && t != Object.class) {
				final int cast = pool.classe(typeInterne(t));
				c.write(0xc0); // checkcast
				c.write(hi(cast));
				c.write(lo(cast));
			}
			c.write(0xb5); // putfield
			c.write(hi(ref));
			c.write(lo(ref));
		}
		c.write(0xb1); // return

		final ByteArrayOutputStream methodes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(methodes);
		// pile : cible, contexte, cible puis valeur courante (2 cases pour long/double) et champ
		methode(out, nom, desc, code, 7, 6, c.toByteArray());
		out.flush();
		return ClasseCachee.assemble(pool, nomCible + "$$LecteurJson", LecteurChamps.class.getName().replace('.', '/'),
				methodes, 1);
	}

	private static boolean generable(final Class<?> type, final Champ[] champs, final int debut) {
		if (!ClasseCachee.disponible() || type.isArray() || type.isInterface() || champs.length > 30_000)
			return false;
		for (int i = debut; i < champs.length; i++) {
			final Field f = champs[i].getInfo();
			if (f == null) // faux id
				return false;
			final int m = f.getModifiers();
			if (Modifier.isFinal(m) || Modifier.isStatic(m))
				return false;
			// champ hérité : accessible à la classe cachée (nid de type) seulement s'il est public, ou non privé et
			// non protégé dans le même paquetage
			final Class<?> declarant = f.getDeclaringClass();
			if (declarant != type && !Modifier.isPublic(m) && (Modifier.isPrivate(m) || Modifier.isProtected(m)
					|| declarant.getClassLoader() != type.getClassLoader()
					|| !declarant.getPackageName().equals(type.getPackageName())))
				return false;
		}
		return true;
	}

	private static String suffixe(final Class<?> t) {
		if (!t.isPrimitive())
			return "Objet";
		return Character.toUpperCase(t.getName().charAt(0)) + t.getName().substring(1);
	}

	private static byte[] octets(final Class<?> type, final Champ[] champs, final int debut, final Class<?> contexte,
			final boolean ecriture) throws IOException {
		final String nomCible = type.getName().replace('.', '/');
		final String nomContexte = contexte.getName().replace('.', '/');
		final Pool pool = new Pool();
		final int code = pool.utf8("Code");
		final int classeCible = pool.classe(nomCible);
		final int classeContexte = pool.classe(nomContexte);
		final int nom = pool.utf8(ecriture ? "ecrit" : "lit");
		final int desc = pool.utf8("(Ljava/lang/Object;Ljava/lang/Object;[L" + CHAMP + ";)V");

		final ByteArrayOutputStream c = new ByteArrayOutputStream();
		// Cible t = (Cible) objet ; Contexte ctx = (Contexte) contexte
		c.write(0x2b); // aload_1
		c.write(0xc0); // checkcast
		c.write(hi(classeCible));
		c.write(lo(classeCible));
		c.write(0x3a); // astore 4
		c.write(4);
		c.write(0x2c); // aload_2
		c.write(0xc0); // checkcast
		c.write(hi(classeContexte));
		c.write(lo(classeContexte));
		c.write(0x3a); // astore 5
		c.write(5);
		for (int k = debut; k < champs.length; k++) {
			final Field f = champs[k].getInfo();
			final Class<?> t = f.getType();
			final String d = descripteur(t);
			final String declarant = f.getDeclaringClass().getName().replace('.', '/');
			final int ref = pool.champ(declarant, f.getName(), d);
			if (ecriture) {
				// ctx.ecritXxx(t.champ, champs[k])
				final int m = pool.methode(nomContexte, "ecrit" + suffixe(t),
						"(" + (t.isPrimitive() ? d : "Ljava/lang/Object;") + INFOS + ")V");
				c.write(0x19); // aload 5
				c.write(5);
				c.write(0x19); // aload 4
				c.write(4);
				c.write(0xb4); // getfield
				c.write(hi(ref));
				c.write(lo(ref));
				ecritChamp(c, k);
				c.write(0xb6); // invokevirtual
				c.write(hi(m));
				c.write(lo(m));
			} else {
				// t.champ = (Type) ctx.litXxx(champs[k])
				final int m = pool.methode(nomContexte, "lit" + suffixe(t),
						"(" + INFOS + ")" + (t.isPrimitive() ? d : "Ljava/lang/Object;"));
				c.write(0x19); // aload 4
				c.write(4);
				c.write(0x19); // aload 5
				c.write(5);
				ecritChamp(c, k);
				c.write(0xb6); // invokevirtual
				c.write(hi(m));
				c.write(lo(m));
				if (!t.isPrimitive() && t != Object.class) {
					final int cast = pool.classe(typeInterne(t));
					c.write(0xc0); // checkcast
					c.write(hi(cast));
					c.write(lo(cast));
				}
				c.write(0xb5); // putfield
				c.write(hi(ref));
				c.write(lo(ref));
			}
		}
		c.write(0xb1); // return

		final ByteArrayOutputStream methodes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(methodes);
		methode(out, nom, desc, code, 5, 6, c.toByteArray());
		out.flush();
		final String nomInterface = (ecriture ? EcrivainChamps.class : LecteurChamps.class).getName().replace('.',
				'/');
		return ClasseCachee.assemble(pool, nomCible + (ecriture ? "$$Ecrivain" : "$$Lecteur"), nomInterface,
				methodes, 1);
	}

	/** aload_3 ; indice k ; aaload : empile champs[k]. */
	private static void ecritChamp(final ByteArrayOutputStream c, final int k) {
		c.write(0x2d); // aload_3
		if (k <= 5)
			c.write(0x03 + k); // iconst_k
		else if (k <= 127) {
			c.write(0x10); // bipush
			c.write(k);
		} else {
			c.write(0x11); // sipush
			c.write(k >> 8);
			c.write(k);
		}
		c.write(0x32); // aaload
	}

	private GenerateurSerialiseurs() {
	}
}
