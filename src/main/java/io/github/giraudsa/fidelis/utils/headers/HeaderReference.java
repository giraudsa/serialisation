package io.github.giraudsa.fidelis.utils.headers;

import java.io.IOException;

import io.github.giraudsa.fidelis.utils.io.EntreeBinaire;
import io.github.giraudsa.fidelis.utils.io.SortieBinaire;

/**
 * Référence à un objet déjà vu dont le smallId est trop grand pour {@link HeaderVerySmallId} : on écrit
 * smallId - maxVerySmallId sur le nombre minimal d'octets.
 */
public class HeaderReference extends Header {
	/** headers indexés par la taille de codage (1 à 4). */
	private static final HeaderReference[] encodageToHeader = new HeaderReference[5];

	protected static Header getHeader(final int smallId) {
		return encodageToHeader[ByteHelper.getMinimumEncodage(smallId - HeaderVerySmallId.getMaxVerySmallId())];
	}

	protected static void init() {
		new HeaderReference(1);
		new HeaderReference(2);
		new HeaderReference(3);
		new HeaderReference(4);
	}

	private final int encodageSmallId;

	private HeaderReference(final int encodageSmallId) {
		super();
		this.encodageSmallId = encodageSmallId;
		encodageToHeader[encodageSmallId] = this;
	}

	@Override
	public int readSmallId(final EntreeBinaire input, final int maxId) throws IOException {
		return (int) ByteHelper.read(input, encodageSmallId) + HeaderVerySmallId.getMaxVerySmallId();
	}

	@Override
	public void write(final SortieBinaire output, final int smallId, final short smallIdType, final boolean isDejaVuType,
			final Class<?> type) throws IOException {
		output.writeByte(headerByte);
		ByteHelper.write(output, smallId - HeaderVerySmallId.getMaxVerySmallId());
	}

}
