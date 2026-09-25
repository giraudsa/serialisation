package utils.io;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

/**
 * Sortie binaire tamponnée, big-endian, non synchronisée : remplace {@code DataOutputStream(BufferedOutputStream)}
 * (verrou et appel virtuel par octet) sur le chemin critique de la sérialisation.
 */
public final class SortieBinaire extends OutputStream implements DataOutput {
	private static final VarHandle INT = MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle LONG = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
	private static final VarHandle SHORT = MethodHandles.byteArrayViewVarHandle(short[].class, ByteOrder.BIG_ENDIAN);

	private final byte[] buffer;
	private OutputStream destination;
	private int position;

	public SortieBinaire(final OutputStream destination) {
		this(destination, 8192);
	}

	public SortieBinaire(final OutputStream destination, final int taille) {
		this.destination = destination;
		buffer = new byte[taille];
	}

	/** Change de destination (tampon vide) pour réutiliser la sortie ; null pour la libérer. */
	public void reinitialise(final OutputStream nouvelleDestination) {
		destination = nouvelleDestination;
		position = 0;
	}

	private void assure(final int n) throws IOException {
		if (position + n > buffer.length)
			vide();
	}

	@Override
	public void close() throws IOException {
		flush();
	}

	@Override
	public void flush() throws IOException {
		vide();
		destination.flush();
	}

	private void vide() throws IOException {
		if (position > 0) {
			destination.write(buffer, 0, position);
			position = 0;
		}
	}

	@Override
	public void write(final byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		if (len > buffer.length - position) {
			vide();
			if (len > buffer.length) {
				destination.write(b, off, len);
				return;
			}
		}
		System.arraycopy(b, off, buffer, position, len);
		position += len;
	}

	@Override
	public void write(final int b) throws IOException {
		if (position == buffer.length)
			vide();
		buffer[position++] = (byte) b;
	}

	@Override
	public void writeBoolean(final boolean v) throws IOException {
		write(v ? 1 : 0);
	}

	@Override
	public void writeByte(final int v) throws IOException {
		write(v);
	}

	@Override
	public void writeBytes(final String s) throws IOException {
		final int n = s.length();
		for (int i = 0; i < n; i++)
			write((byte) s.charAt(i));
	}

	@Override
	public void writeChar(final int v) throws IOException {
		writeShort(v);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		final int n = s.length();
		for (int i = 0; i < n; i++)
			writeChar(s.charAt(i));
	}

	@Override
	public void writeDouble(final double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	@Override
	public void writeFloat(final float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	@Override
	public void writeInt(final int v) throws IOException {
		assure(4);
		INT.set(buffer, position, v);
		position += 4;
	}

	@Override
	public void writeLong(final long v) throws IOException {
		assure(8);
		LONG.set(buffer, position, v);
		position += 8;
	}

	@Override
	public void writeShort(final int v) throws IOException {
		assure(2);
		SHORT.set(buffer, position, (short) v);
		position += 2;
	}

	@Override
	public void writeUTF(final String s) throws IOException {
		// rarement utilisé : on passe par l'implémentation standard
		final java.io.ByteArrayOutputStream tmp = new java.io.ByteArrayOutputStream();
		new DataOutputStream(tmp).writeUTF(s);
		write(tmp.toByteArray());
	}

	/** Écrit un entier positif en varint (7 bits par octet). */
	public void writeVarInt(int v) throws IOException {
		assure(5);
		while ((v & ~0x7F) != 0) {
			buffer[position++] = (byte) (v & 0x7F | 0x80);
			v >>>= 7;
		}
		buffer[position++] = (byte) v;
	}

	/** Écrit un long positif en varint (7 bits par octet). */
	public void writeVarLong(long v) throws IOException {
		assure(10);
		while ((v & ~0x7FL) != 0) {
			buffer[position++] = (byte) (v & 0x7F | 0x80);
			v >>>= 7;
		}
		buffer[position++] = (byte) v;
	}

	/**
	 * Écrit une chaîne : (nombre d'octets &lt;&lt; 1 | 1 si ASCII) en varint, puis chaque char en UTF-8 sur 1 à 3
	 * octets (surrogates codés un par un). Le bit ASCII permet au lecteur de copier les octets sans les examiner.
	 */
	public void writeString(final String s) throws IOException {
		final int nbChars = s.length();
		final int maxOctets = 3 * nbChars;
		// cas courant : la chaîne tient dans le tampon, en un seul parcours. Boucle serrée tant que les caractères
		// sont ASCII (en-tête provisoire de chaîne ASCII) ; au premier qui ne l'est pas, l'encodage continue sur
		// place et l'en-tête est corrigé à la fin (octets recollés si sa taille change).
		if (maxOctets + 5 <= buffer.length) {
			assure(maxOctets + 5);
			final int debut = position;
			writeVarInt(nbChars << 1 | 1);
			final int reserve = position - debut;
			final byte[] b = buffer;
			int j = position;
			int i = 0;
			for (; i < nbChars; i++) {
				final char c = s.charAt(i);
				if (c >= 0x80)
					break;
				b[j++] = (byte) c;
			}
			if (i == nbChars)
				position = j;
			else
				continueNonAscii(s, i, j, debut, reserve);
			return;
		}
		writeStringGenerale(s);
	}

	/**
	 * Suite de writeString à partir du premier caractère non ASCII (indice i, octet j) : encode le reste puis corrige
	 * l'en-tête écrit en debut (taille reserve). Méthode à part pour que la boucle ASCII reste petite et inlinée.
	 */
	private void continueNonAscii(final String s, int i, int j, final int debut, final int reserve) {
		final byte[] b = buffer;
		final int nbChars = s.length();
		for (; i < nbChars; i++) {
			final char c = s.charAt(i);
			if (c < 0x80)
				b[j++] = (byte) c;
			else if (c < 0x800) {
				b[j++] = (byte) (0xC0 | c >> 6);
				b[j++] = (byte) (0x80 | c & 0x3F);
			} else {
				b[j++] = (byte) (0xE0 | c >> 12);
				b[j++] = (byte) (0x80 | c >> 6 & 0x3F);
				b[j++] = (byte) (0x80 | c & 0x3F);
			}
		}
		final int nbOctets = j - debut - reserve;
		final int entete = nbOctets << 1; // non ASCII
		final int taille = tailleVarInt(entete);
		if (taille != reserve)
			System.arraycopy(b, debut + reserve, b, debut + taille, nbOctets);
		int p = debut; // l'espace de l'en-tête est déjà assuré
		int v = entete;
		while ((v & ~0x7F) != 0) {
			b[p++] = (byte) (v & 0x7F | 0x80);
			v >>>= 7;
		}
		b[p] = (byte) v;
		position = debut + taille + nbOctets;
	}

	private static int tailleVarInt(final int v) {
		return v >>> 7 == 0 ? 1 : v >>> 14 == 0 ? 2 : v >>> 21 == 0 ? 3 : v >>> 28 == 0 ? 4 : 5;
	}

	private void writeStringGenerale(final String s) throws IOException {
		final int nbChars = s.length();
		int nbOctets = nbChars;
		for (int i = 0; i < nbChars; i++) {
			final char c = s.charAt(i);
			if (c >= 0x80)
				nbOctets += c < 0x800 ? 1 : 2;
		}
		writeVarInt(nbOctets << 1 | (nbOctets == nbChars ? 1 : 0));
		if (nbOctets > buffer.length) {
			final byte[] octets = new byte[nbOctets];
			encode(s, octets, 0, nbOctets == nbChars);
			write(octets, 0, nbOctets);
			return;
		}
		assure(nbOctets);
		encode(s, buffer, position, nbOctets == nbChars);
		position += nbOctets;
	}

	private static void encode(final String s, final byte[] cible, int j, final boolean ascii) {
		final int nbChars = s.length();
		if (ascii) {
			s.getBytes(0, nbChars, cible, j); // copie ASCII intrinsèque, sans allocation
			return;
		}
		for (int i = 0; i < nbChars; i++) {
			final char c = s.charAt(i);
			if (c < 0x80)
				cible[j++] = (byte) c;
			else if (c < 0x800) {
				cible[j++] = (byte) (0xC0 | c >> 6);
				cible[j++] = (byte) (0x80 | c & 0x3F);
			} else {
				cible[j++] = (byte) (0xE0 | c >> 12);
				cible[j++] = (byte) (0x80 | c >> 6 & 0x3F);
				cible[j++] = (byte) (0x80 | c & 0x3F);
			}
		}
	}
}
