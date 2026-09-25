package utils.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

/**
 * Entrée binaire tamponnée, big-endian, non synchronisée : remplace {@code DataInputStream(BufferedInputStream)} sur
 * le chemin critique de la désérialisation.
 */
public final class EntreeBinaire extends InputStream implements DataInput {
	private static final VarHandle INT = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle LONG = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle SHORT = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.BIG_ENDIAN);

	private final byte[] buffer;
	private int fin;
	private int position;
	private InputStream source;

	public EntreeBinaire(final InputStream source) {
		this(source, 8192);
	}

	public EntreeBinaire(final InputStream source, final int taille) {
		this.source = source;
		buffer = new byte[taille];
	}

	/** Change de source (tampon vide) pour réutiliser l'entrée ; null pour la libérer. */
	public void reinitialise(final InputStream nouvelleSource) {
		source = nouvelleSource;
		position = 0;
		fin = 0;
	}

	/** Garantit n octets disponibles dans le tampon (n ≤ taille du tampon). */
	/*
	 * Les chemins courants (donnée déjà dans le tampon) sont gardés très courts pour que le JIT les inline à coup sûr ;
	 * le remplissage du tampon est dans des méthodes à part.
	 */
	private void assure(final int n) throws IOException {
		if (fin - position < n)
			remplit(n);
	}

	private void remplit(final int n) throws IOException {
		final int reste = fin - position;
		System.arraycopy(buffer, position, buffer, 0, reste);
		position = 0;
		fin = reste;
		while (fin < n) {
			final int lu = source.read(buffer, fin, buffer.length - fin);
			if (lu < 0)
				throw new EOFException();
			fin += lu;
		}
	}

	@Override
	public int available() throws IOException {
		return fin - position + source.available();
	}

	@Override
	public void close() throws IOException {
		source.close();
	}

	@Override
	public int read() throws IOException {
		if (position == fin) {
			position = 0;
			fin = 0;
			final int lu = source.read(buffer, 0, buffer.length);
			if (lu <= 0)
				return -1;
			fin = lu;
		}
		return buffer[position++] & 0xFF;
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		if (len == 0)
			return 0;
		final int dispo = fin - position;
		if (dispo > 0) {
			final int n = Math.min(dispo, len);
			System.arraycopy(buffer, position, b, off, n);
			position += n;
			return n;
		}
		if (len >= buffer.length)
			return source.read(b, off, len);
		final int lu = source.read(buffer, 0, buffer.length);
		if (lu <= 0)
			return -1;
		position = 0;
		fin = lu;
		return read(b, off, len);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return readByte() != 0;
	}

	@Override
	public byte readByte() throws IOException {
		final int p = position;
		if (p < fin) {
			position = p + 1;
			return buffer[p];
		}
		return octetApresRemplissage();
	}

	private byte octetApresRemplissage() throws IOException {
		remplit(1);
		return buffer[position++];
	}

	@Override
	public char readChar() throws IOException {
		return (char) readShort();
	}

	@Override
	public double readDouble() throws IOException {
		return Double.longBitsToDouble(readLong());
	}

	@Override
	public float readFloat() throws IOException {
		return Float.intBitsToFloat(readInt());
	}

	@Override
	public void readFully(final byte[] b) throws IOException {
		readFully(b, 0, b.length);
	}

	@Override
	public void readFully(final byte[] b, int off, int len) throws IOException {
		while (len > 0) {
			final int lu = read(b, off, len);
			if (lu < 0)
				throw new EOFException();
			off += lu;
			len -= lu;
		}
	}

	@Override
	public int readInt() throws IOException {
		assure(4);
		final int v = (int) INT.get(buffer, position);
		position += 4;
		return v;
	}

	@Override
	public String readLine() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long readLong() throws IOException {
		assure(8);
		final long v = (long) LONG.get(buffer, position);
		position += 8;
		return v;
	}

	@Override
	public short readShort() throws IOException {
		assure(2);
		final short v = (short) SHORT.get(buffer, position);
		position += 2;
		return v;
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return readByte() & 0xFF;
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return readShort() & 0xFFFF;
	}

	@Override
	public String readUTF() throws IOException {
		return DataInputStream.readUTF(this);
	}

	/** Lit un entier positif codé en varint. */
	public int readVarInt() throws IOException {
		final byte premier = readByte();
		if (premier >= 0) // cas courant : un seul octet
			return premier;
		return readVarIntLong(premier & 0x7F);
	}

	private int readVarIntLong(final int debut) throws IOException {
		int res = debut;
		for (int decalage = 7; decalage < 35; decalage += 7) {
			final int b = readByte() & 0xFF;
			res |= (b & 0x7F) << decalage;
			if (b < 0x80)
				return res;
		}
		throw new IOException("varint mal formé");
	}

	/** Lit un long positif codé en varint. */
	public long readVarLong() throws IOException {
		long res = 0;
		for (int decalage = 0; decalage < 70; decalage += 7) {
			final int b = readByte() & 0xFF;
			res |= (long) (b & 0x7F) << decalage;
			if (b < 0x80)
				return res;
		}
		throw new IOException("varint mal formé");
	}

	/** Lit une chaîne écrite par {@link SortieBinaire#writeString}. */
	@SuppressWarnings("deprecation")
	public String readString() throws IOException {
		final int entete = readVarInt();
		final int nbOctets = entete >>> 1;
		final int p = position;
		// cas courant : chaîne ASCII entièrement dans le tampon, simple copie en latin-1 compact (le constructeur avec
		// Charset, partagé avec tout le JDK, est parfois mal compilé par le JIT et devient alors dominant)
		if ((entete & 1) != 0 && fin - p >= nbOctets) {
			position = p + nbOctets;
			return new String(buffer, 0, p, nbOctets);
		}
		return readStringLent(nbOctets, (entete & 1) != 0);
	}

	@SuppressWarnings("deprecation")
	private String readStringLent(final int nbOctets, final boolean ascii) throws IOException {
		final byte[] octets;
		int i;
		final int finOctets;
		if (nbOctets <= buffer.length) {
			assure(nbOctets);
			octets = buffer;
			i = position;
			position += nbOctets;
		} else {
			octets = new byte[nbOctets];
			readFully(octets);
			i = 0;
		}
		finOctets = i + nbOctets;
		if (ascii)
			return new String(octets, 0, i, nbOctets);
		final String latin1 = decodeLatin1(octets, i, finOctets);
		if (latin1 != null)
			return latin1;
		final char[] chars = new char[nbOctets];
		int nbChars = 0;
		for (; i < finOctets; i++) {
			final int b = octets[i] & 0xFF;
			if (b < 0x80)
				chars[nbChars++] = (char) b;
			else if (b < 0xE0)
				chars[nbChars++] = (char) ((b & 0x1F) << 6 | octets[++i] & 0x3F);
			else {
				chars[nbChars++] = (char) ((b & 0x0F) << 12 | (octets[i + 1] & 0x3F) << 6 | octets[i + 2] & 0x3F);
				i += 2;
			}
		}
		return new String(chars, 0, nbChars);
	}

	/**
	 * Décode directement en latin-1 compact si tous les caractères sont ≤ 0xFF (é, °, à... : séquences C2/C3), sans
	 * tableau de char intermédiaire ni recompression. @return null si un caractère dépasse 0xFF.
	 */
	@SuppressWarnings("deprecation")
	private static String decodeLatin1(final byte[] octets, int i, final int fin) {
		final byte[] latin1 = new byte[fin - i];
		int n = 0;
		for (; i < fin; i++) {
			final int b = octets[i] & 0xFF;
			if (b < 0x80)
				latin1[n++] = (byte) b;
			else if (b == 0xC2 || b == 0xC3)
				latin1[n++] = (byte) ((b & 0x03) << 6 | octets[++i] & 0x3F);
			else
				return null;
		}
		return new String(latin1, 0, 0, n);
	}

	@Override
	public int skipBytes(final int n) throws IOException {
		int reste = n;
		while (reste > 0 && read() >= 0)
			reste--;
		return n - reste;
	}
}
