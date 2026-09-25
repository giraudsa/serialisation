package io.github.giraudsa.fidelis.serialisation.binary.actions.simple;

import java.io.IOException;
import java.math.BigDecimal;

import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.ByteHelper;
import io.github.giraudsa.fidelis.utils.io.SortieBinaire;

public class ActionBinaryBigDecimal extends ActionBinary<BigDecimal> {

	public ActionBinaryBigDecimal() {
		super();
	}

	/** @return l'unscaledValue si elle tient dans un long (hors Long.MIN_VALUE), sinon Long.MIN_VALUE. */
	private static long unscaledLong(final BigDecimal bigDec) {
		try {
			// même valeur entière, scale 0 : pour un BigDecimal compact, longValueExact renvoie l'unscaledValue
			return bigDec.scaleByPowerOfTen(bigDec.scale()).longValueExact();
		} catch (final ArithmeticException e) {
			return Long.MIN_VALUE; // trop grand pour un long : chemin BigInteger
		}
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final BigDecimal bigDec,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException {
		// valeur sans identité (TypeExtension.isValeurImmuableBinaire) : toujours écrite.
		ecrit(getOutput(marshaller), bigDec);
	}

	/** Écrit la valeur : scale (zigzag) puis unscaledValue en complément à deux, sur le nombre minimal d'octets. */
	public static void ecrit(final SortieBinaire sortie, final BigDecimal bigDec) throws IOException {
		sortie.writeVarInt(ByteHelper.zigzag(bigDec.scale()));
		final long unscaled = unscaledLong(bigDec);
		if (unscaled != Long.MIN_VALUE) {
			// l'unscaledValue tient dans un long : on évite BigInteger et toByteArray
			sortie.writeVarInt(ByteHelper.taille(unscaled));
			ByteHelper.ecrit(sortie, unscaled);
		} else {
			final byte[] octets = bigDec.unscaledValue().toByteArray();
			sortie.writeVarInt(octets.length);
			sortie.write(octets);
		}
	}

}
