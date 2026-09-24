package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.UUID;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderTypeCourant;

public class ActionBinaryUUID extends ActionBinary<UUID> {

	public ActionBinaryUUID() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final UUID id, final FieldInformations fieldInformations,
			final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			writeLong(marshaller, id.getMostSignificantBits());
			writeLong(marshaller, id.getLeastSignificantBits());
		}
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final UUID id,
			final FieldInformations fieldInformations) throws IOException {
		final int smallIdSigne = smallIdUUID(marshaller, id);
		final boolean isDejaVu = smallIdSigne > 0;
		final int smallId = isDejaVu ? smallIdSigne : -smallIdSigne;
		HeaderTypeCourant.getHeader(id, smallId, isDejaVu).write(getOutput(marshaller), smallId);
		return isDejaVu;
	}
}
