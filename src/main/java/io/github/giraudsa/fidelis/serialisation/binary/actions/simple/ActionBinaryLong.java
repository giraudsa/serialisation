package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderSimpleType;

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
		if (ecritSansEnTeteSiPrimitif(marshaller, fieldInformations, l))
			return false;
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(l);
		header.writeValue(getOutput(marshaller), l);
		return false;
	}

}
