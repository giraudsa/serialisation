package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderSimpleType;

public class ActionBinaryFloat extends ActionBinary<Float> {

	public ActionBinaryFloat() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Float objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien a faire
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Float f,
			final FieldInformations fieldInformations) throws IOException {
		if (ecritSansEnTeteSiPrimitif(marshaller, fieldInformations, f))
			return false;
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(f);
		header.writeValue(getOutput(marshaller), f);
		return false;
	}
}
