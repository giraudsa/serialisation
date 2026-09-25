package utils.headers;

import java.io.IOException;

import giraudsa.marshall.exception.UnmarshallExeption;
import utils.io.EntreeBinaire;
import utils.io.SortieBinaire;

public abstract class Header {
	/**
	 * Registre des headers. Il est porté par une classe à part pour ne pas
	 * dépendre de l'ordre d'initialisation statique entre Header et ses classes
	 * dérivées : l'octet de chaque header dépend de l'ordre de création, qui doit
	 * être identique à l'écriture et à la lecture.
	 */
	static final class Registre {
		private static final Header[] headers = new Header[256];
		private static int prochainOctet = 0;

		static {
			HeaderSimpleType.init();
			HeaderTypeCourant.init();
			HeaderEnum.init();
			HeaderTypeDevinable.init();
			HeaderTypeNonDevinable.init();
			HeaderReference.init();
			while (prochainOctet < 256)
				new HeaderVerySmallId();
		}

		private static byte enregistre(final Header header) {
			final int octet = prochainOctet++;
			headers[octet] = header;
			return (byte) octet;
		}

		/** Force l'initialisation du registre. */
		static void init() {
			// le travail est fait dans le bloc statique
		}

		private Registre() {
		}
	}

	// type autre
	public static Header getHeader(final boolean isDejaVu, final boolean isTypeDevinable, final int smallId,
			final short smallIdType) {
		Registre.init();
		if (isDejaVu)
			return smallId <= HeaderVerySmallId.getMaxVerySmallId() ? HeaderVerySmallId.getHeader(smallId)
					: HeaderReference.getHeader(smallId);
		return isTypeDevinable ? HeaderTypeDevinable.getHeader() : HeaderTypeNonDevinable.getHeader(smallIdType);
	}

	public static Header getHeader(final byte b) {
		Registre.init();
		return Registre.headers[b & 0xFF];
	}

	/** catégories d'en-tête, pour un aiguillage par switch plutôt que par instanceof. */
	public static final int SIMPLE = 0;
	public static final int COURANT = 1;
	public static final int ENUM = 2;
	public static final int COMPLEXE = 3;

	protected final byte headerByte;
	/** voir {@link #SIMPLE}, {@link #COURANT}, {@link #ENUM}, {@link #COMPLEXE}. */
	public final int categorie;

	protected Header() {
		super();
		headerByte = Registre.enregistre(this);
		categorie = categorie();
	}

	/** Catégorie de l'en-tête ; constante pour chaque classe dérivée (appelée depuis le constructeur). */
	protected int categorie() {
		return COMPLEXE;
	}

	public short getSmallIdType(final EntreeBinaire input) throws IOException, UnmarshallExeption {
		return 0;
	}

	/**
	 * @return true si l'objet apparaît pour la première fois : son smallId n'est pas écrit, le lecteur l'attribue
	 *         séquentiellement.
	 */
	public boolean isNouveau() {
		return false;
	}

	public boolean isTypeDevinable() {
		return true;
	}

	public abstract int readSmallId(EntreeBinaire input, int i) throws IOException, UnmarshallExeption;

	public void write(final SortieBinaire output, final int smallId, final short smallIdType, final boolean isDejaVuType,
			final Class<?> type) throws IOException {
		// A spécifier dans les classes dérivées ad hoc
	}

	public void writeValue(final SortieBinaire output, final Object o) throws IOException {
		// A spécifier dans les classes dérivées ad hoc
	}

}
