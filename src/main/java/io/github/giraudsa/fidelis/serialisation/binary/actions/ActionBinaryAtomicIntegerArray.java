package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicIntegerArray;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryAtomicIntegerArray extends ActionBinary<AtomicIntegerArray> {

	public ActionBinaryAtomicIntegerArray() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final AtomicIntegerArray obj,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, obj);
			final int size = obj.length();
			writeInt(marshaller, size);
			for (int i = 0; i < size; i++)
				writeInt(marshaller, obj.get(i));
		}
	}

}
