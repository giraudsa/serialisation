package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderSimpleType;

public class ActionBinaryBoolean extends ActionBinary<Boolean> {

	public ActionBinaryBoolean() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Boolean objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien à faire l'information est dans le header
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Boolean bool, final FieldInformations fi)
			throws IOException {
		if (ecritSansEnTeteSiPrimitif(marshaller, fi, bool))
			return false;
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(bool);
		header.writeValue(getOutput(marshaller), bool);
		return false;
	}

}
