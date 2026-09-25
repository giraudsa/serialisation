package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryAtomicInteger extends ActionBinary<AtomicInteger> {

	public ActionBinaryAtomicInteger() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final AtomicInteger atomicInteger,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, atomicInteger);
			writeInt(marshaller, atomicInteger.get());
		}
	}

}
