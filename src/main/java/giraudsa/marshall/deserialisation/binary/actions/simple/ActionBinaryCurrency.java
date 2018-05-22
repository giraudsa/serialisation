package giraudsa.marshall.deserialisation.binary.actions.simple;

import java.io.IOException;
import java.util.Currency;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

public class ActionBinaryCurrency extends ActionBinarySimple<Currency> {

	public static ActionAbstrait<Currency> getInstance() {
		return new ActionBinaryCurrency(Currency.class, null);
	}

	private ActionBinaryCurrency(final Class<Currency> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Currency> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryCurrency(Currency.class, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu())
			obj = getObjet();
		else {
			obj = Currency.getInstance(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
