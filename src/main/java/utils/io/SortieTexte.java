package utils.io;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Sortie texte tamponnée et non synchronisée pour les formats JSON et XML : remplace StringWriter (StringBuffer
 * synchronisé) et BufferedWriter, qui prennent un verrou à chaque petite écriture.
 * <p>
 * Deux modes : vers un Writer de destination (tampon vidé au besoin), ou en chaîne ({@link #pourChaine()}) : le
 * texte est accumulé puis rendu par {@link #toString()} ou {@link #termine()}. En chaîne, le texte est gardé en
 * octets Latin-1 tant que ses caractères le permettent (la chaîne rendue en est une simple copie, sans compression
 * d'un tableau de caractères), puis en caractères à partir du premier qui dépasse U+00FF.
 */
public final class SortieTexte extends Writer {
	private static final int TAILLE = 8192;
	/** au-delà, le tampon d'une sortie en chaîne n'est pas gardé pour la suivante. */
	private static final int TAILLE_GARDEE = 1 << 19;
	private static final int LATIN1_MAX = 0xFF;

	/** tampon libre de la dernière sortie en chaîne terminée, par thread (évite l'allocation et sa mise à zéro). */
	private static final ThreadLocal<byte[][]> TAMPONS = ThreadLocal.withInitial(() -> new byte[1][]);

	/** @return une sortie qui accumule le texte, rendu par toString() ou {@link #termine()}. */
	public static SortieTexte pourChaine() {
		final byte[][] libre = TAMPONS.get();
		final byte[] tampon = libre[0];
		libre[0] = null;
		final SortieTexte sortie = new SortieTexte(null, null);
		sortie.octets = tampon != null ? tampon : new byte[TAILLE];
		sortie.capacite = sortie.octets.length;
		return sortie;
	}

	/** tampon en caractères (null tant que la sortie en chaîne est en Latin-1). */
	private char[] buffer;
	/** tampon Latin-1 de la sortie en chaîne (null sinon). */
	private byte[] octets;
	/** null en mode chaîne. */
	private final Writer destination;
	private int position;
	/** taille du tampon en cours (octets ou caractères) : test de place d'une seule comparaison. */
	private int capacite;
	/** date en cours d'écriture, en mode Latin-1. */
	private char[] date;

	public SortieTexte(final Writer destination) {
		this(destination, new char[TAILLE]);
	}

	private SortieTexte(final Writer destination, final char[] tampon) {
		this.destination = destination;
		buffer = tampon;
		capacite = tampon == null ? 0 : tampon.length;
	}

	/**
	 * Sortie en chaîne : rend le texte et libère le tampon pour la sortie suivante du thread ; la sortie ne doit plus
	 * servir.
	 */
	public String termine() {
		final String s = toString();
		if (octets != null && octets.length <= TAILLE_GARDEE)
			TAMPONS.get()[0] = octets;
		octets = null;
		buffer = null;
		position = 0;
		capacite = 0;
		return s;
	}

	/** passe du tampon Latin-1 au tampon en caractères (un caractère au-delà de U+00FF arrive). */
	private void versCaracteres() {
		final byte[] o = octets;
		final char[] b = new char[Math.max(TAILLE, o.length)];
		for (int i = 0; i < position; i++)
			b[i] = (char) (o[i] & 0xFF);
		buffer = b;
		octets = null;
		capacite = b.length;
	}

	/** Garantit n caractères de place (chemin rapide, inliné ; l'agrandissement est à part). */
	private void assure(final int n) throws IOException {
		if (position + n > capacite)
			agrandit(n);
	}

	/** Vide le tampon vers la destination, ou l'agrandit en mode chaîne. */
	private void agrandit(final int n) throws IOException {
		if (octets != null) {
			if (position + n > octets.length)
				octets = Arrays.copyOf(octets, Math.max(octets.length * 2, position + n));
			capacite = octets.length;
			return;
		}
		if (position + n <= buffer.length)
			return;
		if (destination == null)
			buffer = Arrays.copyOf(buffer, Math.max(buffer.length * 2, position + n));
		else {
			vide();
			if (n > buffer.length)
				buffer = new char[n];
		}
		capacite = buffer.length;
	}

	private void vide() throws IOException {
		if (destination != null && position > 0) {
			destination.write(buffer, 0, position);
			position = 0;
		}
	}

	@Override
	public void close() throws IOException {
		flush();
		if (destination != null)
			destination.close();
	}

	@Override
	public void flush() throws IOException {
		if (destination != null) {
			vide();
			destination.flush();
		}
	}

	@Override
	public String toString() {
		if (octets != null)
			return new String(octets, 0, position, StandardCharsets.ISO_8859_1);
		return new String(buffer, 0, position);
	}

	@Override
	public void write(final char[] cbuf, final int off, final int len) throws IOException {
		assure(len);
		if (octets != null) {
			final byte[] o = octets;
			for (int i = 0; i < len; i++) {
				final char c = cbuf[off + i];
				if (c > LATIN1_MAX) {
					versCaracteres();
					write(cbuf, off + i, len - i);
					return;
				}
				o[position++] = (byte) c;
			}
			return;
		}
		System.arraycopy(cbuf, off, buffer, position, len);
		position += len;
	}

	@Override
	public void write(final int c) throws IOException {
		final byte[] o = octets;
		if (o != null && (char) c <= LATIN1_MAX && position < capacite) { // cas courant, inliné
			o[position++] = (byte) c;
			return;
		}
		ecritCaractere(c);
	}

	private void ecritCaractere(final int c) throws IOException {
		if (octets != null) {
			if ((char) c <= LATIN1_MAX) {
				if (position == octets.length)
					assure(1);
				octets[position++] = (byte) c;
				return;
			}
			versCaracteres();
		}
		if (position == buffer.length)
			assure(1);
		buffer[position++] = (char) c;
	}

	@Override
	public void write(final String s) throws IOException {
		write(s, 0, s.length());
	}

	@Override
	public void write(final String s, final int off, final int len) throws IOException {
		assure(len);
		if (octets != null) {
			final byte[] o = octets;
			for (int i = 0; i < len; i++) {
				final char c = s.charAt(off + i);
				if (c > LATIN1_MAX) {
					versCaracteres();
					write(s, off + i, len - i);
					return;
				}
				o[position++] = (byte) c;
			}
			return;
		}
		s.getChars(off, off + len, buffer, position);
		position += len;
	}

	/** Écrit l'entier en décimal (mêmes caractères que Long.toString), sans allocation. */
	public void writeLong(final long v) throws IOException {
		if (v == Long.MIN_VALUE) {
			write("-9223372036854775808");
			return;
		}
		assure(20);
		long reste = v;
		int n = 1;
		for (long p = 10; n < 19 && (reste < 0 ? -reste : reste) >= p; p *= 10)
			n++;
		if (octets != null) {
			final byte[] o = octets;
			if (reste < 0) {
				o[position++] = '-';
				reste = -reste;
			}
			for (int i = position + n - 1; i >= position; i--) {
				o[i] = (byte) ('0' + reste % 10);
				reste /= 10;
			}
		} else {
			final char[] b = buffer;
			if (reste < 0) {
				b[position++] = '-';
				reste = -reste;
			}
			for (int i = position + n - 1; i >= position; i--) {
				b[i] = (char) ('0' + reste % 10);
				reste /= 10;
			}
		}
		position += n;
	}

	/** Écrit "date" au format ISO UTC par défaut (voir DatesIso), entre guillemets. */
	public void writeDateIso(final long millis) throws IOException {
		assure(DatesIso.LONGUEUR + 2);
		if (octets != null) {
			if (date == null)
				date = new char[DatesIso.LONGUEUR];
			DatesIso.ecris(millis, date, 0);
			final byte[] o = octets;
			o[position++] = '"';
			for (int i = 0; i < DatesIso.LONGUEUR; i++)
				o[position++] = (byte) date[i];
			o[position++] = '"';
			return;
		}
		buffer[position++] = '"';
		DatesIso.ecris(millis, buffer, position);
		position += DatesIso.LONGUEUR;
		buffer[position++] = '"';
	}

	/**
	 * Écrit des octets Latin-1 préparés, si la sortie est en mode Latin-1. @return false sinon (rien n'est écrit :
	 * l'appelant écrit le texte équivalent).
	 */
	public boolean writeOctets(final byte[] texte) throws IOException {
		if (octets == null)
			return false;
		assure(texte.length);
		System.arraycopy(texte, 0, octets, position, texte.length);
		position += texte.length;
		return true;
	}

	/** Écrit une clé JSON déjà mise en octets Latin-1 ("nom":, voir Champ.getClefJson). */
	public void writeClef(final byte[] clef, final String nom) throws IOException {
		if (octets == null) {
			writeClef(nom);
			return;
		}
		assure(clef.length);
		System.arraycopy(clef, 0, octets, position, clef.length);
		position += clef.length;
	}

	/** Écrit une clé JSON : "nom": d'un seul tenant. */
	public void writeClef(final String nom) throws IOException {
		final int n = nom.length();
		assure(n + 3);
		if (octets != null) {
			final byte[] o = octets;
			int p = position;
			o[p++] = '"';
			for (int i = 0; i < n; i++) {
				final char c = nom.charAt(i);
				if (c > LATIN1_MAX) {
					position = p;
					versCaracteres();
					write(nom, i, n - i);
					write('"');
					write(':');
					return;
				}
				o[p++] = (byte) c;
			}
			o[p++] = '"';
			o[p++] = ':';
			position = p;
			return;
		}
		final char[] b = buffer;
		int p = position;
		b[p++] = '"';
		nom.getChars(0, n, b, p);
		p += n;
		b[p++] = '"';
		b[p++] = ':';
		position = p;
	}

	/** Écrit "chaine" entre guillemets, échappée (voir writeEchappe), d'un seul tenant. */
	public void writeEntreGuillemets(final String s, final String[] remplacements) throws IOException {
		write('"');
		writeEchappe(s, remplacements);
		write('"');
	}

	/**
	 * Écrit la chaîne en remplaçant chaque caractère c qui a un remplacement (remplacements[c] non nul), directement
	 * dans le tampon. Cas courant (rien à remplacer) : copie d'un bloc (getChars, intrinsèque) puis vérification.
	 */
	public void writeEchappe(final String s, final String[] remplacements) throws IOException {
		final int n = s.length();
		assure(n);
		final int nbRemplacables = remplacements.length;
		if (octets != null) {
			final byte[] o = octets;
			int p = position;
			for (int k = 0; k < n; k++) {
				final char c = s.charAt(k);
				if (c < nbRemplacables && remplacements[c] != null || c > LATIN1_MAX) {
					// à remplacer ou hors Latin-1 : on reprend à partir de lui, caractère par caractère
					position = p;
					writeEchappeDepuis(s, k, remplacements);
					return;
				}
				o[p++] = (byte) c;
			}
			position = p;
			return;
		}
		final char[] b = buffer;
		final int debut = position;
		s.getChars(0, n, b, debut);
		for (int k = 0; k < n; k++) {
			final char c = b[debut + k];
			if (c < nbRemplacables && remplacements[c] != null) {
				// un caractère à remplacer : on reprend à partir de lui, caractère par caractère
				position = debut + k;
				writeEchappeDepuis(s, k, remplacements);
				return;
			}
		}
		position = debut + n;
	}

	private void writeEchappeDepuis(final String s, final int depuis, final String[] remplacements)
			throws IOException {
		final int n = s.length();
		assure(n - depuis);
		final int nbRemplacables = remplacements.length;
		for (int i = depuis; i < n; i++) {
			final char c = s.charAt(i);
			final String r = c < nbRemplacables ? remplacements[c] : null;
			if (r == null) {
				if (octets != null) {
					if (c <= LATIN1_MAX) {
						octets[position++] = (byte) c;
						continue;
					}
					versCaracteres();
				}
				buffer[position++] = c;
			} else {
				final int lr = r.length();
				assure(lr + n - i); // le remplacement et tous les caractères restants
				write(r, 0, lr);
			}
		}
	}

	@Override
	public Writer append(final char c) throws IOException {
		write(c);
		return this;
	}
}
