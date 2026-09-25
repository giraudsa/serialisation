package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderSimpleType;

public class ActionBinaryChar extends ActionBinary<Character> {

	public ActionBinaryChar() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Character objetASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// rien a faire
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Character caractere,
			final FieldInformations fieldInformations) throws IOException {
		if (ecritSansEnTeteSiPrimitif(marshaller, fieldInformations, caractere))
			return false;
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(caractere);
		header.writeValue(getOutput(marshaller), caractere);
		return false;
	}

}
