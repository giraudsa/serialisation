package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderSimpleType;

public class ActionBinaryInteger extends ActionBinary<Integer> {

	public ActionBinaryInteger() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Integer objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien a faire
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Integer i,
			final FieldInformations fieldInformations) throws IOException {
		if (ecritSansEnTeteSiPrimitif(marshaller, fieldInformations, i))
			return false;
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(i);
		header.writeValue(getOutput(marshaller), i);
		return false;
	}
}
