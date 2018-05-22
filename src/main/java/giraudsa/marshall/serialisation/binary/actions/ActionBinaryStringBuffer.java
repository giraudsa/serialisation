package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

public class ActionBinaryStringBuffer extends ActionBinary<StringBuffer> {

	public ActionBinaryStringBuffer() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final StringBuffer sb,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, sb);
			writeUTF(marshaller, sb.toString());
		}
	}
}