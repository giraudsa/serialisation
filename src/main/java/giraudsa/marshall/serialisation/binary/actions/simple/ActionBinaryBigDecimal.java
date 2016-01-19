package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigDecimal;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionBinaryBigDecimal  extends ActionBinarySimple<BigDecimal>{

	public ActionBinaryBigDecimal() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, BigDecimal objetASerialiser, FieldInformations fieldInformations) throws IOException {
		writeUTF(marshaller, objetASerialiser.toString());
	}

}
