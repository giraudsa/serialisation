package giraudsa.marshall.serialisation.binary.actions.simple;

import giraudsa.marshall.serialisation.binary.BinaryMarshaller;
import utils.champ.FieldInformations;

import java.io.IOException;

public class ActionBinaryLong  extends ActionBinarySimple<Long>{

	public ActionBinaryLong(BinaryMarshaller b) {
		super(b);
	}
	
	@Override
	protected void ecritValeur(Long objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeLong(objetASerialiser);
	}

}
