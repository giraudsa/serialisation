package giraudsa.marshall.deserialisation.binary.actions;

import java.io.IOException;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.deserialisation.binary.actions.simple.ActionBinarySimple;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum<E extends Enum> extends ActionBinarySimple<E> {

	public static ActionAbstrait<Enum> getInstance() {
		return new ActionBinaryEnum<>(Enum.class, null);
	}

	private ActionBinaryEnum(final Class<E> type, final BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	public <U extends E> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionBinaryEnum<>(type, (BinaryUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		final Enum[] enums = type.getEnumConstants();
		if (enums.length < 254)
			obj = enums[readByte()];
		else
			obj = enums[readShort()];
	}
}
