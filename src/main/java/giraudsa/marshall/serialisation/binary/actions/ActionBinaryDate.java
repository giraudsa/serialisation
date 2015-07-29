package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.util.Date;

public class ActionBinaryDate<DateType extends Date> extends ActionBinary<DateType> {


	public ActionBinaryDate(Class<? super DateType> type, BinaryMarshaller b) {
		super(type, b);
	}

	@Override
	protected void serialise(Object objetASerialiser, TypeRelation typeRelation, boolean couldBeLessSpecific) {
		boolean isDejaVu = writeHeaders(objetASerialiser, typeRelation, couldBeLessSpecific);
		try {
			if(!isDejaVu) writeLong(((Date)objetASerialiser).getTime());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
