package utils.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * Sortie texte tamponnée et non synchronisée pour les formats JSON et XML : remplace StringWriter (StringBuffer
 * synchronisé) et BufferedWriter, qui prennent un verrou à chaque petite écriture.
 * <p>
 * Deux modes : vers un Writer de destination (tampon vidé au besoin), ou en chaîne ({@link #pourChaine()}) : le
 * texte est accumulé puis rendu par {@link #toString()}.
 */
public final class SortieTexte extends Writer {
	private static final int TAILLE = 8192;

	/** @return une sortie qui accumule le texte, rendu par toString(). */
	public static SortieTexte pourChaine() {
		return new SortieTexte(null);
	}

	private char[] buffer = new char[TAILLE];
	/** null en mode chaîne. */
	private final Writer destination;
	private int position;

	public SortieTexte(final Writer destination) {
		this.destination = destination;
	}

	/** Garantit n caractères de place : vide le tampon vers la destination, ou l'agrandit en mode chaîne. */
	private void assure(final int n) throws IOException {
		if (position + n <= buffer.length)
			return;
		if (destination == null)
			buffer = Arrays.copyOf(buffer, Math.max(buffer.length * 2, position + n));
		else {
			vide();
			if (n > buffer.length)
				buffer = new char[n];
		}
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
		return new String(buffer, 0, position);
	}

	@Override
	public void write(final char[] cbuf, final int off, final int len) throws IOException {
		assure(len);
		System.arraycopy(cbuf, off, buffer, position, len);
		position += len;
	}

	@Override
	public void write(final int c) throws IOException {
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
		s.getChars(off, off + len, buffer, position);
		position += len;
	}

	/** Écrit une clé JSON : "nom": d'un seul tenant. */
	public void writeClef(final String nom) throws IOException {
		final int n = nom.length();
		assure(n + 3);
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
		final char[] b = buffer;
		final int debut = position;
		s.getChars(0, n, b, debut);
		final int nbRemplacables = remplacements.length;
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
			if (r == null)
				buffer[position++] = c;
			else {
				final int lr = r.length();
				assure(lr + n - i); // le remplacement et tous les caractères restants
				r.getChars(0, lr, buffer, position);
				position += lr;
			}
		}
	}

	@Override
	public Writer append(final char c) throws IOException {
		write(c);
		return this;
	}
}
