package utils.headers;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import giraudsa.marshall.exception.UnmarshallExeption;

public abstract class Header<T> {
	private static byte constructionByte = 0;
	private static final Map<Byte, Header<?>> dicoByteToHeader = new HashMap<>();
	static {
		HeaderSimpleType.init();
		HeaderTypeCourant.init();
		HeaderEnum.init();
		HeaderTypeDevinable.init();
		HeaderTypeNonDevinable.init();
		while (constructionByte != 0)
			new HeaderVerySmallId();
	}

	// type autre
	public static Header<?> getHeader(final boolean isDejaVu, final boolean isTypeDevinable, final int smallId,
			final short smallIdType) {
		if (isDejaVu)
			return smallId <= HeaderVerySmallId.getMaxVerySmallId() ? HeaderVerySmallId.getHeader(smallId)
					: HeaderTypeDevinable.getHeader(smallId);
		else
			return isTypeDevinable ? HeaderTypeDevinable.getHeader(smallId)
					: HeaderTypeNonDevinable.getHeader(smallId, smallIdType);
	}

	public static Header<?> getHeader(final byte b) {
		return dicoByteToHeader.get(b);
	}

	protected byte headerByte;

	protected Header() {
		super();
		headerByte = constructionByte++;
		dicoByteToHeader.put(headerByte, this);
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
