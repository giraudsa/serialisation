package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.math.BigInteger;

import giraudsa.marshall.exception.UnmarshallExeption;
import utils.BiHashMap;

public class HeaderTypeNonDevinable extends Header {
	private static final BiHashMap<Integer, Integer, HeaderTypeNonDevinable> encodageSmallIdEtSmallIdTypeToHeader = new BiHashMap<>();

	protected static Header getHeader(final int smallId, final short smallIdType) {
		final int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId()
				? smallId - HeaderVerySmallId.getMaxVerySmallId()
				: smallId;
		int encodageSmallId = 0;
		int encodageSmallIdType = 0;
		encodageSmallId = ByteHelper.getMinimumEncodage(toBeConsideredForNextBytes);
		encodageSmallIdType = ByteHelper.getMinimumEncodage(smallIdType);
		return encodageSmallIdEtSmallIdTypeToHeader.get(encodageSmallId, encodageSmallIdType);
	}

	protected static void init() {
		new HeaderTypeNonDevinable(1, 1);
		new HeaderTypeNonDevinable(2, 1);
		new HeaderTypeNonDevinable(3, 1);
		new HeaderTypeNonDevinable(4, 1);
		new HeaderTypeNonDevinable(1, 2);
		new HeaderTypeNonDevinable(2, 2);
		new HeaderTypeNonDevinable(3, 2);
		new HeaderTypeNonDevinable(4, 2);
	}

	private final int encodageSmallId;
	private final int encodageSmallIdType;

	public HeaderTypeNonDevinable(final int encodageSmallId, final int encodageSmallIdType) {
		super();
		this.encodageSmallId = encodageSmallId;
		this.encodageSmallIdType = encodageSmallIdType;
		encodageSmallIdEtSmallIdTypeToHeader.put(encodageSmallId, encodageSmallIdType, this);
	}

	@Override
	public short getSmallIdType(final DataInputStream input) throws IOException, UnmarshallExeption {
		final byte[] tmp = new byte[encodageSmallIdType];
		input.readFully(tmp);
		final BigInteger bi = new BigInteger(tmp);
		return bi.shortValue();
	}

	@Override
	public boolean isTypeDevinable() {
		return false;
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
		output.write(BigInteger.valueOf(smallIdType).toByteArray());
		if (!isDejaVuType)
			output.writeUTF(type.getName());

	}

}
