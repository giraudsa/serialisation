package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryAtomicBoolean extends ActionBinary<AtomicBoolean> {

	public ActionBinaryAtomicBoolean() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final AtomicBoolean atomicBoolean,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, atomicBoolean);
			writeBoolean(marshaller, atomicBoolean.get());
		}
	}
}
