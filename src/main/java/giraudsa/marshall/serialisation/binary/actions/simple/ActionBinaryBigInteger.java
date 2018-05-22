package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigInteger;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;

public class ActionBinaryBigInteger extends ActionBinary<BigInteger> {

	public ActionBinaryBigInteger() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final BigInteger bigInt,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) {
			setDejaTotalementSerialise(marshaller, bigInt);
			final byte[] toByte = bigInt.toByteArray();
			writeInt(marshaller, toByte.length);
			for (int i = 0; i < toByte.length; ++i)
				writeByte(marshaller, toByte[i]);
		}
	}

}
