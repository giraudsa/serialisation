package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

public class HeaderVerySmallId extends Header {
	private static int maxVerySmallId = 0;
	/** headers indexés par smallId (1 à maxVerySmallId). */
	private static final HeaderVerySmallId[] verySmallIdToHeader = new HeaderVerySmallId[256];

	protected static Header getHeader(final int smallId) {
		return verySmallIdToHeader[smallId];
	}

	protected static int getMaxVerySmallId() {
		Registre.init();
		return maxVerySmallId;
	}

	private final int smallId;

	HeaderVerySmallId() {
		smallId = ++maxVerySmallId;
		verySmallIdToHeader[smallId] = this;
	}

	@Override
	public int readSmallId(final DataInputStream input, final int maxId) {
		return smallId;
	}

	@Override
	public void write(final DataOutput output, final int smallId, final short smallIdType, final boolean isDejaVuType,
			final Class<?> type) throws IOException {
		output.writeByte(headerByte);
	}

}
