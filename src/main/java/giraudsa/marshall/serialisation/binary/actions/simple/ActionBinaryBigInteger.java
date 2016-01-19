package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigInteger;

import giraudsa.marshall.serialisation.Marshaller;
import utils.champ.FieldInformations;

public class ActionBinaryBigInteger  extends ActionBinarySimple<BigInteger>{

	public ActionBinaryBigInteger() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, BigInteger objetASerialiser, FieldInformations fieldInformations) throws IOException {
		byte[] toByte = objetASerialiser.toByteArray();
		writeInt(marshaller, toByte.length);
		for(int i = 0; i<toByte.length; ++i){
			writeByte(marshaller, toByte[i]);
		}
	}

}
