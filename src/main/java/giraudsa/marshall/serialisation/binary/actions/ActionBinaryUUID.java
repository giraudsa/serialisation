package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;
import java.util.UUID;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;
import utils.headers.HeaderTypeCourant;

public class ActionBinaryUUID extends ActionBinary<UUID> {

	public ActionBinaryUUID() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, UUID id, FieldInformations fieldInformations, boolean isDejaVu) throws IOException{
		if(!isDejaVu){
			writeLong(marshaller, id.getMostSignificantBits());
			writeLong(marshaller, id.getLeastSignificantBits());
		}
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, UUID id, FieldInformations fieldInformations)
			throws MarshallExeption, IOException {
		boolean isDejaVu = isDejaVuUUID(marshaller, id);
		int smallId = getSmallIdUUIDAndStockUUID(marshaller, id);
		HeaderTypeCourant<?> header = HeaderTypeCourant.getHeader(id, smallId);
		header.write(getOutput(marshaller), smallId);
		return isDejaVu;
	}
}
