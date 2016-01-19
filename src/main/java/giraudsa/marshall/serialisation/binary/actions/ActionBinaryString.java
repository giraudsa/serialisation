package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionBinaryString extends ActionBinary<String> {

	public ActionBinaryString() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, String string, FieldInformations fieldInformations) throws IOException {
		if(!isDejaTotalementSerialise(marshaller, string)){
			setDejaTotalementSerialise(marshaller, string);
			writeUTF(marshaller, string);
		}
	}
}