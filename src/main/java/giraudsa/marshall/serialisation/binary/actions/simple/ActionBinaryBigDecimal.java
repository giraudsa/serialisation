package giraudsa.marshall.serialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigDecimal;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.ByteHelper;

public class ActionBinaryBigDecimal extends ActionBinary<BigDecimal> {

	public ActionBinaryBigDecimal() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final BigDecimal bigDec,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// valeur sans identité (TypeExtension.isValeurImmuableBinaire) : toujours écrite.
		// scale (zigzag) puis unscaledValue en complément à deux, sur le nombre minimal d'octets
		writeVarInt(marshaller, ByteHelper.zigzag(bigDec.scale()));
		if (bigDec.precision() <= 18) {
			// l'unscaledValue tient dans un long : on évite BigInteger et toByteArray
			final long unscaled = bigDec.scaleByPowerOfTen(bigDec.scale()).longValueExact();
			writeVarInt(marshaller, ByteHelper.taille(unscaled));
			ByteHelper.ecrit(getOutput(marshaller), unscaled);
		} else {
			final byte[] unscaled = bigDec.unscaledValue().toByteArray();
			writeVarInt(marshaller, unscaled.length);
			writeByteArray(marshaller, unscaled);
		}
	}

}
