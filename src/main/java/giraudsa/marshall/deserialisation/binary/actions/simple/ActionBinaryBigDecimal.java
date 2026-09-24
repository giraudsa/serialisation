package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import utils.headers.ByteHelper;

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

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			final int scale = ByteHelper.unzigzag(readVarInt());
			final int taille = readVarInt();
			if (taille <= 8) {
				// unscaledValue tient dans un long : BigDecimal compact, sans BigInteger intermédiaire
				long unscaled = readByte(); // extension de signe
				for (int i = 1; i < taille; i++)
					unscaled = unscaled << 8 | readByte() & 0xFF;
				obj = BigDecimal.valueOf(unscaled, scale);
			} else
				obj = new BigDecimal(new BigInteger(readBytes(taille)), scale);
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
