package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryAtomicLong extends ActionBinary<AtomicLong> {

	public ActionBinaryAtomicLong() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final AtomicLong atomicLong,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, atomicLong);
			writeLong(marshaller, atomicLong.get());
		}
	}

}
