package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongArray;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

public class ActionBinaryAtomicLongArray extends ActionBinary<AtomicLongArray> {

	public ActionBinaryAtomicLongArray() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final AtomicLongArray obj,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, obj);
			final int size = obj.length();
			writeInt(marshaller, size);
			for (int i = 0; i < size; i++)
				writeLong(marshaller, obj.get(i));
		}
	}

}
