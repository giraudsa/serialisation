package io.github.giraudsa.fidelis.serialisation.binary.actions;

import java.io.IOException;

import io.github.giraudsa.fidelis.exception.MarshallExeption;
import io.github.giraudsa.fidelis.serialisation.Marshaller;
import io.github.giraudsa.fidelis.serialisation.binary.ActionBinary;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;
import io.github.giraudsa.fidelis.utils.headers.HeaderEnum;
import io.github.giraudsa.fidelis.utils.TypeExtension;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum extends ActionBinary<Enum> {

	public ActionBinaryEnum() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Enum enumASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException, MarshallExeption {
		final Enum[] enums = TypeExtension.getEnumConstants(enumASerialiser.getDeclaringClass());
		if (enums.length < 254)
			writeByte(marshaller, (byte) enumASerialiser.ordinal());
		else
			writeShort(marshaller, (short) enumASerialiser.ordinal());
	}

	@Override
	protected boolean writeHeaders(final Marshaller marshaller, final Enum objetASerialiser,
			final FieldInformations fieldInformations) throws IOException {
		final Class<?> typeObj = getTypeObjProblemeHibernate(objetASerialiser);
		final boolean isTypeDevinable = isTypeDevinable(marshaller, objetASerialiser, fieldInformations);
		// type devinable : ni écrit ni numéroté (même règle à la lecture)
		final int idType = isTypeDevinable ? 0 : smallIdType(marshaller, typeObj);
		final short smallIdType = (short) Math.abs(idType);
		HeaderEnum.getHeader(smallIdType, isTypeDevinable).write(getOutput(marshaller), smallIdType, typeObj,
				idType > 0);
		return false;
	}

}
