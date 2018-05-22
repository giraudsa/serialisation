package giraudsa.marshall.serialisation.binary.actions;

import java.io.IOException;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import utils.champ.FieldInformations;
import utils.headers.HeaderEnum;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum extends ActionBinary<Enum> {

	public ActionBinaryEnum() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Enum enumASerialiser,
			final FieldInformations fieldInformations, final boolean isDejaVu) throws IOException, MarshallExeption {
		final Enum[] enums = enumASerialiser.getClass().getEnumConstants();
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
		final boolean isDejaVuType = isDejaVuType(marshaller, typeObj);
		final short smallIdType = getSmallIdTypeAndStockType(marshaller, typeObj);
		final HeaderEnum header = HeaderEnum.getHeader(smallIdType, isTypeDevinable);
		header.write(getOutput(marshaller), smallIdType, typeObj, isDejaVuType);
		return false;
	}

}
