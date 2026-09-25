package utils.champ;

import static utils.champ.ClasseCachee.descripteur;
import static utils.champ.ClasseCachee.enveloppe;
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
 * Crée l'accès à un champ : de préférence une petite classe cachée (JDK 15+), membre du nid de la classe du champ, qui
 * lit et écrit le champ directement, même privé ; à défaut (JDK plus ancien, champ final ou statique, classe d'un
 * module fermé...), un accès par réflexion.
 */
final class GenerateurAcces {

	/** Accès par réflexion : solution de repli. */
	static final class AccesParReflexion implements AccesChamp {
		private final Field champ;

		AccesParReflexion(final Field champ) {
			this.champ = champ;
		}

		@Override
		public Object get(final Object objet) {
			try {
				return champ.get(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void set(final Object objet, final Object valeur) {
			try {
				champ.set(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public boolean getBoolean(final Object objet) {
			try {
				return champ.getBoolean(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public byte getByte(final Object objet) {
			try {
				return champ.getByte(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public char getChar(final Object objet) {
			try {
				return champ.getChar(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public double getDouble(final Object objet) {
			try {
				return champ.getDouble(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public float getFloat(final Object objet) {
			try {
				return champ.getFloat(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public int getInt(final Object objet) {
			try {
				return champ.getInt(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public long getLong(final Object objet) {
			try {
				return champ.getLong(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public short getShort(final Object objet) {
			try {
				return champ.getShort(objet);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void setBoolean(final Object objet, final boolean valeur) {
			try {
				champ.setBoolean(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void setByte(final Object objet, final byte valeur) {
			try {
				champ.setByte(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void setChar(final Object objet, final char valeur) {
			try {
				champ.setChar(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void setDouble(final Object objet, final double valeur) {
			try {
				champ.setDouble(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void setFloat(final Object objet, final float valeur) {
			try {
				champ.setFloat(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void setInt(final Object objet, final int valeur) {
			try {
				champ.setInt(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void setLong(final Object objet, final long valeur) {
			try {
				champ.setLong(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public void setShort(final Object objet, final short valeur) {
			try {
				champ.setShort(objet, valeur);
			} catch (final IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private static final String INTERFACE = AccesChamp.class.getName().replace('.', '/');
	static AccesChamp cree(final Field champ) {
		final int modificateurs = champ.getModifiers();
		if (ClasseCachee.disponible() && !Modifier.isFinal(modificateurs) && !Modifier.isStatic(modificateurs))
			try {
				return genere(champ);
			} catch (final Throwable e) { // NOSONAR : toute erreur de génération ramène à la réflexion
				// classe d'un module fermé, chargeur qui ne voit pas AccesChamp...
			}
		return new AccesParReflexion(champ);
	}

	private static AccesChamp genere(final Field champ) throws Throwable {
		return (AccesChamp) ClasseCachee.instancie(champ.getDeclaringClass(), octets(champ));
	}

	private static byte[] octets(final Field champ) throws IOException {
		final Class<?> cible = champ.getDeclaringClass();
		final String nomCible = cible.getName().replace('.', '/');
		final String nomClasse = nomCible + "$$AccesChamp";
		final Class<?> typeChamp = champ.getType();
		final String desc = descripteur(typeChamp);
		final Class<?> enveloppe = typeChamp.isPrimitive() ? enveloppe(typeChamp) : null;
		final String nomEnveloppe = enveloppe == null ? null : enveloppe.getName().replace('.', '/');

		final Pool pool = new Pool();
		final int code = pool.utf8("Code");
		final int nomGet = pool.utf8("get");
		final int descGet = pool.utf8("(Ljava/lang/Object;)Ljava/lang/Object;");
		final int nomSet = pool.utf8("set");
		final int descSet = pool.utf8("(Ljava/lang/Object;Ljava/lang/Object;)V");
		final int classeCible = pool.classe(nomCible);
		final int ref = pool.champ(nomCible, champ.getName(), desc);
		final int castValeur = pool.classe(enveloppe == null ? typeInterne(typeChamp) : nomEnveloppe);
		final int boxe = enveloppe == null ? 0
				: pool.methode(nomEnveloppe, "valueOf", "(" + desc + ")L" + nomEnveloppe + ";");
		final int deboxe = enveloppe == null ? 0
				: pool.methode(nomEnveloppe, typeChamp.getName() + "Value", "()" + desc);

		// setter primitif : setInt(Object, int)...
		final String suffixe = enveloppe == null ? null
				: Character.toUpperCase(typeChamp.getName().charAt(0)) + typeChamp.getName().substring(1);
		final int nomSetPrimitif = enveloppe == null ? 0 : pool.utf8("set" + suffixe);
		final int descSetPrimitif = enveloppe == null ? 0 : pool.utf8("(Ljava/lang/Object;" + desc + ")V");
		final int nomGetPrimitif = enveloppe == null ? 0 : pool.utf8("get" + suffixe);
		final int descGetPrimitif = enveloppe == null ? 0 : pool.utf8("(Ljava/lang/Object;)" + desc);

		final ByteArrayOutputStream methodes = new ByteArrayOutputStream();
		final DataOutputStream out = new DataOutputStream(methodes);

		// public Object get(Object o) { return ((Cible) o).champ; } (+ valueOf pour un primitif)
		final ByteArrayOutputStream g = new ByteArrayOutputStream();
		g.write(0x2b); // aload_1
		g.write(0xc0); // checkcast Cible
		g.write(hi(classeCible));
		g.write(lo(classeCible));
		g.write(0xb4); // getfield
		g.write(hi(ref));
		g.write(lo(ref));
		if (enveloppe != null) {
			g.write(0xb8); // invokestatic Enveloppe.valueOf
			g.write(hi(boxe));
			g.write(lo(boxe));
		}
		g.write(0xb0); // areturn
		methode(out, nomGet, descGet, code, 2, 2, g.toByteArray());

		// public void set(Object o, Object v) { ((Cible) o).champ = (Type) v; } (+ xxxValue pour un primitif)
		final ByteArrayOutputStream s = new ByteArrayOutputStream();
		s.write(0x2b); // aload_1
		s.write(0xc0); // checkcast Cible
		s.write(hi(classeCible));
		s.write(lo(classeCible));
		s.write(0x2c); // aload_2
		s.write(0xc0); // checkcast Type (ou enveloppe)
		s.write(hi(castValeur));
		s.write(lo(castValeur));
		if (enveloppe != null) {
			s.write(0xb6); // invokevirtual Enveloppe.xxxValue
			s.write(hi(deboxe));
			s.write(lo(deboxe));
		}
		s.write(0xb5); // putfield
		s.write(hi(ref));
		s.write(lo(ref));
		s.write(0xb1); // return
		methode(out, nomSet, descSet, code, 3, 3, s.toByteArray());

		if (enveloppe != null) {
			// public void setXxx(Object o, xxx v) { ((Cible) o).champ = v; } : sans boxing
			final ByteArrayOutputStream p = new ByteArrayOutputStream();
			p.write(0x2b); // aload_1
			p.write(0xc0); // checkcast Cible
			p.write(hi(classeCible));
			p.write(lo(classeCible));
			final boolean large = typeChamp == long.class || typeChamp == double.class;
			// lload_2, dload_2, fload_2 ou iload_2
			p.write(typeChamp == long.class ? 0x20 : typeChamp == double.class ? 0x28 : typeChamp == float.class ? 0x24 : 0x1c);
			p.write(0xb5); // putfield
			p.write(hi(ref));
			p.write(lo(ref));
			p.write(0xb1); // return
			methode(out, nomSetPrimitif, descSetPrimitif, code, large ? 3 : 2, large ? 4 : 3, p.toByteArray());

			// public xxx getXxx(Object o) { return ((Cible) o).champ; } : sans boxing
			final ByteArrayOutputStream q = new ByteArrayOutputStream();
			q.write(0x2b); // aload_1
			q.write(0xc0); // checkcast Cible
			q.write(hi(classeCible));
			q.write(lo(classeCible));
			q.write(0xb4); // getfield
			q.write(hi(ref));
			q.write(lo(ref));
			// lreturn, dreturn, freturn ou ireturn
			q.write(typeChamp == long.class ? 0xad : typeChamp == double.class ? 0xaf : typeChamp == float.class ? 0xae : 0xac);
			methode(out, nomGetPrimitif, descGetPrimitif, code, 2, 2, q.toByteArray());
		}

		out.flush();
		return ClasseCachee.assemble(pool, nomClasse, INTERFACE, methodes, enveloppe == null ? 2 : 4);
	}

	private GenerateurAcces() {
	}
}
