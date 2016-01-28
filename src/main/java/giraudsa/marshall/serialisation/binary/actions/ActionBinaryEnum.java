package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;


import java.io.IOException;
import utils.champ.FieldInformations;
import utils.headers.HeaderEnum;

@SuppressWarnings("rawtypes")
public class ActionBinaryEnum extends ActionBinary<Enum> {

	public ActionBinaryEnum() {
		super();
	}
	
	@Override
	protected boolean writeHeaders(Marshaller marshaller, Enum objetASerialiser, FieldInformations fieldInformations)
			throws MarshallExeption, IOException {
		Class<?> typeObj = getTypeObjProblemeHibernate(objetASerialiser);
		boolean isTypeDevinable = isTypeDevinable(marshaller, objetASerialiser, fieldInformations);
		boolean isDejaVuType = isDejaVuType(marshaller, typeObj);
		short smallIdType = getSmallIdTypeAndStockType(marshaller, typeObj);
		HeaderEnum<?> header = HeaderEnum.getHeader(smallIdType, isTypeDevinable);
		header.write(getOutput(marshaller), smallIdType, typeObj, isDejaVuType);
		return false;
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Enum enumASerialiser, FieldInformations fieldInformations, boolean isDejaVu) throws IOException, MarshallExeption {
		Enum[] enums = enumASerialiser.getClass().getEnumConstants();
		if(enums.length < 254) 
			writeByte(marshaller, (byte)enumASerialiser.ordinal());
		else
			writeShort(marshaller, (short)enumASerialiser.ordinal());
	}

}
