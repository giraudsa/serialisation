package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderSimpleType;

public class ActionBinaryByte extends ActionBinary<Byte> {

	public ActionBinaryByte() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Byte objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien a faire
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Byte octet, final FieldInformations fi)
			throws IOException {
		if (ecritSansEnTeteSiPrimitif(marshaller, fi, octet))
			return false;
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(octet);
		header.writeValue(getOutput(marshaller), octet);
		return false;
	}
}
