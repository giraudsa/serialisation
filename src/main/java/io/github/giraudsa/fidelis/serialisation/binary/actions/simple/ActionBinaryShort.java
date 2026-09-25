package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderSimpleType;

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
		if (ecritSansEnTeteSiPrimitif(marshaller, fieldInformations, s))
			return false;
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(s);
		header.writeValue(getOutput(marshaller), s);
		return false;
	}
}
