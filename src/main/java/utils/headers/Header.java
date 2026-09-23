package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import giraudsa.marshall.exception.UnmarshallExeption;

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
					: HeaderTypeDevinable.getHeader(smallId);
		else
			return isTypeDevinable ? HeaderTypeDevinable.getHeader(smallId)
					: HeaderTypeNonDevinable.getHeader(smallId, smallIdType);
	}

	public static Header getHeader(final byte b) {
		Registre.init();
		return Registre.headers[b & 0xFF];
	}

	protected final byte headerByte;

	protected Header() {
		super();
		headerByte = Registre.enregistre(this);
	}

	public short getSmallIdType(final DataInputStream input) throws IOException, UnmarshallExeption {
		return 0;
	}

	public boolean isTypeDevinable() {
		return true;
	}

	public abstract int readSmallId(DataInputStream input, int i) throws IOException, UnmarshallExeption;

	public void write(final DataOutput output, final int smallId, final short smallIdType, final boolean isDejaVuType,
			final Class<?> type) throws IOException {
		// A spécifier dans les classes dérivées ad hoc
	}

	public void writeValue(final DataOutput output, final Object o) throws IOException {
		// A spécifier dans les classes dérivées ad hoc
	}

}
