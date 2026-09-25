package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderTypeCourant;

public class ActionBinaryString extends ActionBinary<String> {

	public ActionBinaryString() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final String string,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu)
			writeUTF(marshaller, string);
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final String string,
			final FieldInformations fieldInformations) throws IOException {
		final int id = smallIdString(marshaller, string);
		final boolean isDejaVu = id > 0;
		final int smallId = isDejaVu ? id : -id;
		HeaderTypeCourant.getHeader(string, smallId, isDejaVu).write(getOutput(marshaller), smallId);
		return isDejaVu;
	}
}