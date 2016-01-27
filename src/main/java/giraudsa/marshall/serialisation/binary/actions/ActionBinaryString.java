package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;
import utils.headers.HeaderTypeCourant;

import java.io.IOException;

public class ActionBinaryString extends ActionBinary<String> {

	public ActionBinaryString() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, String string, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		if(!isDejaVu)
			writeUTF(marshaller, string);
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, String string, FieldInformations fieldInformations)
			throws MarshallExeption, IOException {
		boolean isDejaVu = isDejaVuString(marshaller, string);
		int smallId = getSmallIdStringAndStockString(marshaller, string);
		HeaderTypeCourant<?> header = HeaderTypeCourant.getHeader(string, smallId);
		header.write(getOutput(marshaller), smallId);
		return isDejaVu;
	}
}