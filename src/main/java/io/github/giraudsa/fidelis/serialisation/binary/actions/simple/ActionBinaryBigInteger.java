package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigInteger;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class ActionBinaryBigInteger extends ActionBinary<BigInteger> {

	public ActionBinaryBigInteger() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final BigInteger bigInt,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		if (!isDejaVu) { // valeur sans identité : jamais déjà vue
			final byte[] toByte = bigInt.toByteArray();
			writeInt(marshaller, toByte.length);
			for (int i = 0; i < toByte.length; ++i)
				writeByte(marshaller, toByte[i]);
		}
	}

}
