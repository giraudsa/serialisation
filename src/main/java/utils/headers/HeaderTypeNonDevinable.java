package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import giraudsa.marshall.exception.UnmarshallExeption;

public class HeaderTypeNonDevinable extends Header {
	/** headers indexés par [taille de codage du smallId (1 à 4)][taille de codage du smallIdType (1 à 2)]. */
	private static final HeaderTypeNonDevinable[][] encodageSmallIdEtSmallIdTypeToHeader = new HeaderTypeNonDevinable[5][3];

	protected static Header getHeader(final int smallId, final short smallIdType) {
		final int toBeConsideredForNextBytes = smallId > HeaderVerySmallId.getMaxVerySmallId()
				? smallId - HeaderVerySmallId.getMaxVerySmallId()
				: smallId;
		final int encodageSmallId = ByteHelper.getMinimumEncodage(toBeConsideredForNextBytes);
		final int encodageSmallIdType = ByteHelper.getMinimumEncodage(smallIdType);
		return encodageSmallIdEtSmallIdTypeToHeader[encodageSmallId][encodageSmallIdType];
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
		encodageSmallIdEtSmallIdTypeToHeader[encodageSmallId][encodageSmallIdType] = this;
	}

	@Override
	public short getSmallIdType(final DataInputStream input) throws IOException, UnmarshallExeption {
		return (short) ByteHelper.read(input, encodageSmallIdType);
	}

	@Override
	public boolean isTypeDevinable() {
		return false;
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
		ByteHelper.write(output, smallIdType);
		if (!isDejaVuType)
			output.writeUTF(type.getName());

	}

}
