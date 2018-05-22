package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HeaderVerySmallId extends Header<Object> {
	private static int maxVerySmallId = 0;
	private static final Map<Integer, HeaderVerySmallId> verySmallIdToHeader = new HashMap<>();

	protected static Header<?> getHeader(final int smallId) {
		return verySmallIdToHeader.get(smallId);
	}

	protected static int getMaxVerySmallId() {
		return maxVerySmallId;
	}

	private final int smallId;

	HeaderVerySmallId() {
		smallId = ++maxVerySmallId;
		verySmallIdToHeader.put(smallId, this);
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
