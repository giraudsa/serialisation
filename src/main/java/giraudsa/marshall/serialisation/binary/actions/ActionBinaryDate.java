package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.serialisation.binary.ActionBinary;
import giraudsa.marshall.serialisation.binary.BinaryMarshaller;

import java.io.IOException;
import java.util.Date;

public class ActionBinaryDate extends ActionBinary<Date> {


	public ActionBinaryDate(BinaryMarshaller b) {
		super(b);
	}

	@Override
	protected void ecritValeur(Date date, TypeRelation relation) throws IOException{
		if(!isDejaTotalementSerialise(date)){
			setDejaTotalementSerialise(date);
			writeLong(date.getTime());
		}
	}
}
