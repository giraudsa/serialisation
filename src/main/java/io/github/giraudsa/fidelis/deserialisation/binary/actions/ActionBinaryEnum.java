package io.github.giraudsa.fidelis.deserialisation.binary.actions;

import java.io.IOException;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.binary.BinaryUnmarshaller;
import io.github.giraudsa.fidelis.deserialisation.binary.actions.simple.ActionBinarySimple;
import io.github.giraudsa.fidelis.utils.TypeExtension;

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
		final Enum[] enums = TypeExtension.getEnumConstants(type);
		if (enums.length < 254)
			obj = enums[readByte()];
		else
			obj = enums[readShort()];
	}
}
