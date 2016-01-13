package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionBinaryString extends ActionBinary<String> {

	public ActionBinaryString(BinaryMarshaller b) {
		super(b);
	}
	@Override
	protected void ecritValeur(String string, FieldInformations fieldInformations) throws IOException {
		if(!isDejaTotalementSerialise(string)){
			setDejaTotalementSerialise(string);
			writeUTF(string);
		}
	}
}