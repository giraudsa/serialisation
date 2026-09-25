package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderSimpleType;

public class ActionBinaryDouble extends ActionBinary<Double> {

	public ActionBinaryDouble() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Double objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien a faire
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Double d,
			final FieldInformations fieldInformations) throws IOException {
		if (ecritSansEnTeteSiPrimitif(marshaller, fieldInformations, d))
			return false;
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(d);
		header.writeValue(getOutput(marshaller), d);
		return false;
	}
}
