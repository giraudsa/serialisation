package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigDecimal;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

public class ActionBinaryBigDecimal  extends ActionBinary<BigDecimal>{

	public ActionBinaryBigDecimal() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, BigDecimal bigDec, FieldInformations fieldInformations, boolean isDejaVu) throws IOException {
		if(!isDejaVu){
			setDejaTotalementSerialise(marshaller, bigDec);
			writeUTF(marshaller, bigDec.toString());
		}
	}

}
