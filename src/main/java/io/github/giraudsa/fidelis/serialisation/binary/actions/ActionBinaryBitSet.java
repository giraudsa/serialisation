package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;

import io.github.giraudsa.fidelis.exception.NotImplementedSerializeException;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryBitSet extends ActionBinary<BitSet> {

	public ActionBinaryBitSet() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final BitSet bitSet,
			final FieldInformations fieldInformations, final boolean isDejaVu)
			throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException,
			NoSuchMethodException, NotImplementedSerializeException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, bitSet);
			final int size = bitSet.length();
			writeInt(marshaller, size);
			for (int i = 0; i < size; i++)
				writeBoolean(marshaller, bitSet.get(i));
		}
	}
}
