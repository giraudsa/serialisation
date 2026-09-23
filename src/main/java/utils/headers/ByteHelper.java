package utils.headers;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Encodage des entiers sur le nombre minimal d'octets en complément à deux,
 * big-endian. Format identique à {@code BigInteger.toByteArray()} sans
 * allocation.
 */
public class ByteHelper {

	/**
	 * @return le nombre d'octets nécessaires pour coder v en complément à deux
	 *         (0 pour la valeur 0).
	 */
	protected static int getMinimumEncodage(final long v) {
		if (v == 0)
			return 0;
		return tailleComplementADeux(v);
	}

	protected static int getMinimumEncodage(final Number o) {
		if (o == null)
			return 0;
		if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long)
			return getMinimumEncodage(o.longValue());
		if (o instanceof Float)
			return (float) o == 0.0 ? 0 : 4;
		if (o instanceof Double)
			return (double) o == 0.0 ? 0 : 8;
		return -1;
	}

	protected static Object getObject(final Class<?> simpleType, final long valeur) {
		if (simpleType == byte.class)
			return (byte) valeur;
		if (simpleType == short.class)
			return (short) valeur;
		if (simpleType == int.class)
			return (int) valeur;
		if (simpleType == long.class)
			return valeur;
		return 0;
	}

	/** Lit un entier signé codé sur nbOctets octets. */
	protected static long read(final DataInput input, final int nbOctets) throws IOException {
		if (nbOctets == 0)
			return 0;
		long res = input.readByte(); // extension de signe
		for (int i = 1; i < nbOctets; i++)
			res = res << 8 | input.readUnsignedByte();
		return res;
	}

	private static int tailleComplementADeux(final long v) {
		final int bits = 65 - Long.numberOfLeadingZeros(v >= 0 ? v : ~v);
		return (bits + 7) >>> 3;
	}

	/**
	 * Écrit v sur le nombre minimal d'octets (1 octet pour 0), comme
	 * {@code output.write(BigInteger.valueOf(v).toByteArray())}.
	 */
	protected static void write(final DataOutput output, final long v) throws IOException {
		final int nbOctets = tailleComplementADeux(v);
		for (int i = nbOctets - 1; i >= 0; i--)
			output.writeByte((int) (v >>> (i << 3)));
	}

	private ByteHelper() {
		// classe utilitaire
	}
}
