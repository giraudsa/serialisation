package io.github.giraudsa.fidelis.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;
import io.github.giraudsa.fidelis.utils.headers.ByteHelper;
import io.github.giraudsa.fidelis.utils.io.EntreeBinaire;

@SuppressWarnings("rawtypes")
public class ActionBinaryBigDecimal extends ActionBinarySimple<BigDecimal> {
	public static ActionAbstrait<BigDecimal> getInstance() { // NOSONAR
		return new ActionBinaryBigDecimal(BigDecimal.class, null);
	}

	private ActionBinaryBigDecimal(final Class<BigDecimal> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends BigDecimal> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryBigDecimal(BigDecimal.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	/** Lit un BigDecimal (scale en zigzag puis unscaledValue en complément à deux). */
	public static BigDecimal lit(final EntreeBinaire entree) throws IOException {
		final int scale = ByteHelper.unzigzag(entree.readVarInt());
		final int taille = entree.readVarInt();
		if (taille <= 8) {
			// unscaledValue tient dans un long : BigDecimal compact, sans BigInteger intermédiaire
			long unscaled = entree.readByte(); // extension de signe
			for (int i = 1; i < taille; i++)
				unscaled = unscaled << 8 | entree.readByte() & 0xFF;
			return BigDecimal.valueOf(unscaled, scale);
		}
		final byte[] octets = new byte[taille];
		entree.readFully(octets);
		return new BigDecimal(new BigInteger(octets), scale);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = lit(getBinaryUnmarshaller().getEntree());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
