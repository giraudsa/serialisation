package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.UnmarshallExeption;

public class HeaderTypeDevinable extends Header {
	private static final Map<Integer, HeaderTypeDevinable> encodageSmallIdToHeaderTypeDevinable = new HashMap<>();

	protected static Header getHeader(final int smallId) {
		final int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId()
				? smallId - HeaderVerySmallId.getMaxVerySmallId()
				: smallId;
		int encodageSmallId = 0;
		encodageSmallId = ByteHelper.getMinimumEncodage(toBeConsideredForNextBytes);
		return encodageSmallIdToHeaderTypeDevinable.get(encodageSmallId);
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
		encodageSmallIdToHeaderTypeDevinable.put(encodageSmallId, this);
	}

	@Override
	public int readSmallId(final DataInputStream input, final int maxId) throws IOException, UnmarshallExeption {
		final byte[] tmp = new byte[encodageSmallId];
		input.readFully(tmp);
		final BigInteger bi = new BigInteger(tmp);
		return maxId >= HeaderVerySmallId.getMaxVerySmallId() ? bi.intValue() + HeaderVerySmallId.getMaxVerySmallId()
				: bi.intValue();
	}

	@Override
	public void write(final DataOutput output, final int smallId, final short smallIdType, final boolean isDejaVuType,
			final Class<?> type) throws IOException {
		output.writeByte(headerByte);
		final int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId()
				? smallId - HeaderVerySmallId.getMaxVerySmallId()
				: smallId;
		output.write(BigInteger.valueOf(toBeConsideredForNextBytes).toByteArray());
	}

}
