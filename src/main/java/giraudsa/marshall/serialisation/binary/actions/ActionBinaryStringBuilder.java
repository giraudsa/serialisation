package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionBinaryStringBuilder extends ActionBinary<StringBuilder> {

	public ActionBinaryStringBuilder() {
		super();
	}
	@Override
	protected void ecritValeur(Marshaller marshaller, StringBuilder sb, FieldInformations fieldInformations,boolean isDejaVu) throws IOException {
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, sb);
			writeUTF(marshaller, sb.toString());
		}
	}
}