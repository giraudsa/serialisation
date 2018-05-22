package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigInteger;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

@SuppressWarnings("rawtypes")
public class ActionBinaryBigInteger extends ActionBinarySimple<BigInteger> {
	public static ActionAbstrait<BigInteger> getInstance() { // NOSONAR
		return new ActionBinaryBigInteger(BigInteger.class, null);
	}

	private ActionBinaryBigInteger(final Class<BigInteger> type, final BinaryUnmarshaller<?> b) {
		super(type, b);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <U extends BigInteger> ActionAbstrait<U> getNewInstance(final Class<U> type,
			final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryBigInteger(BigInteger.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			final int taille = readInt();
			final byte[] raw = new byte[taille];
			for (int i = 0; i < taille; ++i)
				raw[i] = readByte();
			obj = new BigInteger(raw);
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
