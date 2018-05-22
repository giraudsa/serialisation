package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

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
			final FieldInformations fieldInformations) throws IOException, MarshallExeption {
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(i);
		header.writeValue(getOutput(marshaller), i);
		return false;
	}
}
