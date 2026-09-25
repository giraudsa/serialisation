package utils.champ;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Outils communs pour fabriquer, en mémoire et à l'exécution, de petites classes cachées (JDK 15+) membres du nid
 * d'une classe métier : pool de constantes, méthodes, définition. Rien n'est écrit sur disque ni ajouté au code
 * métier.
 */
final class ClasseCachee {

	/** Constructeur de bytecode minimal : pool de constantes. */
	static final class Pool {
		final ByteArrayOutputStream octets = new ByteArrayOutputStream();
		private final Map<String, Integer> index = new HashMap<>();
		final DataOutputStream out = new DataOutputStream(octets);
		int prochain = 1;

		private int ajoute(final String cle, final Ecriture ecriture) throws IOException {
			final Integer existant = index.get(cle);
			if (existant != null)
				return existant;
			ecriture.ecrit(out);
			index.put(cle, prochain);
			return prochain++;
		}

		int classe(final String nomInterne) throws IOException {
			final int nom = utf8(nomInterne);
			return ajoute("C" + nomInterne, o -> {
				o.writeByte(7);
				o.writeShort(nom);
			});
		}

		int champ(final String classe, final String nom, final String descripteur) throws IOException {
			final int c = classe(classe);
			final int nt = nomEtType(nom, descripteur);
			return ajoute("F" + classe + "." + nom + ":" + descripteur, o -> {
				o.writeByte(9);
				o.writeShort(c);
				o.writeShort(nt);
			});
		}

		int methode(final String classe, final String nom, final String descripteur) throws IOException {
			final int c = classe(classe);
			final int nt = nomEtType(nom, descripteur);
			return ajoute("M" + classe + "." + nom + descripteur, o -> {
				o.writeByte(10);
				o.writeShort(c);
				o.writeShort(nt);
			});
		}

		int nomEtType(final String nom, final String descripteur) throws IOException {
			final int n = utf8(nom);
			final int d = utf8(descripteur);
			return ajoute("N" + nom + ":" + descripteur, o -> {
				o.writeByte(12);
				o.writeShort(n);
				o.writeShort(d);
			});
		}

		int utf8(final String s) throws IOException {
			return ajoute("U" + s, o -> {
				o.writeByte(1);
				o.writeUTF(s);
			});
		}
	}

	interface Ecriture {
		void ecrit(DataOutputStream out) throws IOException;
	}

	static final Method DEFINE_HIDDEN_CLASS;
	static final Object OPTIONS_NESTMATE;

	static {
		Method m = null;
		Object options = null;
		try {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			final Class<Enum> classOption = (Class<Enum>) Class
					.forName("java.lang.invoke.MethodHandles$Lookup$ClassOption");
			options = Array.newInstance(classOption, 1);
			Array.set(options, 0, Enum.valueOf(classOption, "NESTMATE"));
			m = Lookup.class.getMethod("defineHiddenClass", byte[].class, boolean.class, options.getClass());
		} catch (final ReflectiveOperationException | RuntimeException e) {
			// JDK < 15 : pas de classes cachées, on reste sur la réflexion
			m = null;
		}
		DEFINE_HIDDEN_CLASS = m;
		OPTIONS_NESTMATE = options;
	}

	/** @return true si le JDK permet de définir des classes cachées. */
	static boolean disponible() {
		return DEFINE_HIDDEN_CLASS != null;
	}

	/** Définit la classe cachée (nid de cible) et renvoie une instance par son constructeur sans argument. */
	static Object instancie(final Class<?> cible, final byte[] classe) throws Throwable {
		final Lookup lookup = MethodHandles.privateLookupIn(cible, MethodHandles.lookup());
		final Lookup cachee = (Lookup) DEFINE_HIDDEN_CLASS.invoke(lookup, classe, true, OPTIONS_NESTMATE);
		return cachee.findConstructor(cachee.lookupClass(), MethodType.methodType(void.class)).invoke();
	}

	/**
	 * Assemble une classe publique finale (super Object) implémentant une interface : en-tête, pool, constructeur par
	 * défaut puis les méthodes déjà écrites (nbMethodes dans methodes).
	 */
	static byte[] assemble(final Pool pool, final String nomClasse, final String nomInterface,
			final ByteArrayOutputStream methodes, final int nbMethodes) throws IOException {
		final int thisClass = pool.classe(nomClasse);
		final int superClass = pool.classe("java/lang/Object");
		final int iface = pool.classe(nomInterface);
		final int init = pool.utf8("<init>");
		final int descInit = pool.utf8("()V");
		final int superInit = pool.methode("java/lang/Object", "<init>", "()V");
		final int code = pool.utf8("Code");
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(bos);
		out.writeInt(0xCAFEBABE);
		out.writeShort(0);
		out.writeShort(52); // Java 8 : les méthodes générées n'ont pas de branchement, donc pas de StackMapTable
		out.writeShort(pool.prochain);
		pool.out.flush();
		out.write(pool.octets.toByteArray());
		out.writeShort(0x0031); // public final super
		out.writeShort(thisClass);
		out.writeShort(superClass);
		out.writeShort(1);
		out.writeShort(iface);
		out.writeShort(0); // aucun champ
		out.writeShort(nbMethodes + 1);
		methode(out, init, descInit, code, 1, 1, new byte[] { 0x2a, (byte) 0xb7, hi(superInit), lo(superInit),
				(byte) 0xb1 });
		out.write(methodes.toByteArray());
		out.writeShort(0); // attributs de classe
		out.flush();
		return bos.toByteArray();
	}

	static String descripteur(final Class<?> type) {
		if (type == int.class)
			return "I";
		if (type == long.class)
			return "J";
		if (type == double.class)
			return "D";
		if (type == float.class)
			return "F";
		if (type == boolean.class)
			return "Z";
		if (type == byte.class)
			return "B";
		if (type == short.class)
			return "S";
		if (type == char.class)
			return "C";
		if (type.isArray())
			return type.getName().replace('.', '/');
		return "L" + type.getName().replace('.', '/') + ";";
	}

	static Class<?> enveloppe(final Class<?> primitif) {
		if (primitif == int.class)
			return Integer.class;
		if (primitif == long.class)
			return Long.class;
		if (primitif == double.class)
			return Double.class;
		if (primitif == float.class)
			return Float.class;
		if (primitif == boolean.class)
			return Boolean.class;
		if (primitif == byte.class)
			return Byte.class;
		if (primitif == short.class)
			return Short.class;
		return Character.class;
	}

	static byte hi(final int v) {
		return (byte) (v >> 8);
	}

	static byte lo(final int v) {
		return (byte) v;
	}

	static void methode(final DataOutputStream out, final int nom, final int descripteur, final int code,
			final int maxStack, final int maxLocals, final byte[] instructions) throws IOException {
		out.writeShort(0x0001); // public
		out.writeShort(nom);
		out.writeShort(descripteur);
		out.writeShort(1); // attribut Code
		out.writeShort(code);
		out.writeInt(12 + instructions.length);
		out.writeShort(maxStack);
		out.writeShort(maxLocals);
		out.writeInt(instructions.length);
		out.write(instructions);
		out.writeShort(0); // table d'exceptions
		out.writeShort(0); // attributs
	}

	/** Nom interne pour checkcast (pour un tableau, c'est son descripteur, que getName donne déjà). */
	static String typeInterne(final Class<?> type) {
		return type.getName().replace('.', '/');
	}

	private ClasseCachee() {
	}
}
