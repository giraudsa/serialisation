package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

public class ActionBinaryLong extends ActionBinary<Long> {

	public ActionBinaryLong() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Long objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien a faire
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Long l, final FieldInformations fieldInformations)
			throws IOException {
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(l);
		header.writeValue(getOutput(marshaller), l);
		return false;
	}

}
