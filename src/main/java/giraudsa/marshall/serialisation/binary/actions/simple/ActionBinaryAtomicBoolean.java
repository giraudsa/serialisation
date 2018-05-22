package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

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
