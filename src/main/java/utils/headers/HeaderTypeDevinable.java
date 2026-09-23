package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import giraudsa.marshall.exception.UnmarshallExeption;

public class HeaderTypeDevinable extends Header {
	/** headers indexés par la taille de codage du smallId (1 à 4). */
	private static final HeaderTypeDevinable[] encodageSmallIdToHeaderTypeDevinable = new HeaderTypeDevinable[5];

	protected static Header getHeader(final int smallId) {
		final int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId()
				? smallId - HeaderVerySmallId.getMaxVerySmallId()
				: smallId;
		return encodageSmallIdToHeaderTypeDevinable[ByteHelper.getMinimumEncodage(toBeConsideredForNextBytes)];
	}

	protected static void init() {
		new HeaderTypeDevinable(1);
		new HeaderTypeDevinable(2);
		new HeaderTypeDevinable(3);
		new HeaderTypeDevinable(4);
	}

	private final int encodageSmallId;

	private HeaderTypeDevinable(final int encodageSmallId) {
		super();
		this.encodageSmallId = encodageSmallId;
		encodageSmallIdToHeaderTypeDevinable[encodageSmallId] = this;
	}

	@Override
	public int readSmallId(final DataInputStream input, final int maxId) throws IOException, UnmarshallExeption {
		final int lu = (int) ByteHelper.read(input, encodageSmallId);
		return maxId >= HeaderVerySmallId.getMaxVerySmallId() ? lu + HeaderVerySmallId.getMaxVerySmallId() : lu;
	}

	@Override
	public void write(final DataOutput output, final int smallId, final short smallIdType, final boolean isDejaVuType,
			final Class<?> type) throws IOException {
		output.writeByte(headerByte);
		final int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId()
				? smallId - HeaderVerySmallId.getMaxVerySmallId()
				: smallId;
		ByteHelper.write(output, toBeConsideredForNextBytes);
	}

}
