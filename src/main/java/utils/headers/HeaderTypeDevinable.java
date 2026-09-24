package utils.headers;

import java.io.IOException;

import utils.io.EntreeBinaire;
import utils.io.SortieBinaire;

/**
 * Premier passage d'un objet dont le type est déduit du champ. Le smallId n'est pas écrit : il est attribué
 * séquentiellement à la lecture, dans l'ordre d'apparition.
 */
public class HeaderTypeDevinable extends Header {
	private static HeaderTypeDevinable instance;

	protected static Header getHeader() {
		return instance;
	}

	protected static void init() {
		instance = new HeaderTypeDevinable();
	}

	private HeaderTypeDevinable() {
		super();
	}

	@Override
	public boolean isNouveau() {
		return true;
	}

	@Override
	public int readSmallId(final EntreeBinaire input, final int maxId) {
		return 0;
	}

	@Override
	public void write(final SortieBinaire output, final int smallId, final short smallIdType, final boolean isDejaVuType,
			final Class<?> type) throws IOException {
		output.writeByte(headerByte);
	}

}
