package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderSimpleType;

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
		final HeaderSimpleType<?> header = (HeaderSimpleType<?>) HeaderSimpleType.getHeader(caractere);
		header.writeValue(getOutput(marshaller), caractere);
		return false;
	}

}
