package giraudsa.marshall.serialisation.binary.actions;

import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.binary.ActionBinary;

import utils.champ.FieldInformations;

import java.io.IOException;
import java.util.Date;

public class ActionBinaryDate extends ActionBinary<Date> {


	public ActionBinaryDate() {
		super();
	}

	@Override
	protected void ecritValeur(Marshaller marshaller, Date date, FieldInformations fieldInformations) throws IOException{
		if(!isDejaTotalementSerialise(marshaller, date)){
			setDejaTotalementSerialise(marshaller, date);
			writeLong(marshaller, date.getTime());
		}
	}
}
