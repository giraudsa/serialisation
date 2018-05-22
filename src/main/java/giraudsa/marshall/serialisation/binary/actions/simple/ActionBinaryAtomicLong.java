package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

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
