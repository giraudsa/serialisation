package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigDecimal;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

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
			obj = new BigDecimal(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
