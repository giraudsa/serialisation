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
		final boolean isDejaVu = isDejaVuUUID(marshaller, id);
		final int smallId = getSmallIdUUIDAndStockUUID(marshaller, id);
		final HeaderTypeCourant header = HeaderTypeCourant.getHeader(id, smallId);
		header.write(getOutput(marshaller), smallId);
		return isDejaVu;
	}
}
