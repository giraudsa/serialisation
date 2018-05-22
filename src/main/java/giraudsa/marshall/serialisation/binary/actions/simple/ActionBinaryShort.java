package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

public class ActionBinaryShort extends ActionBinary<Short> {

	public ActionBinaryShort() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Short objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien a faire
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Short s,
			final FieldInformations fieldInformations) throws IOException {
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(s);
		header.writeValue(getOutput(marshaller), s);
		return false;
	}
}
