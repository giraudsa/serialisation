package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;

import java.io.IOException;
import java.util.Date;

public class ActionBinaryDate<DateType extends Date> extends ActionBinary<DateType> {

	public ActionBinaryDate(Class<? extends DateType> type, Unmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected DateType readObject() throws IOException, InstantiationException, IllegalAccessException{
		DateType date = type.newInstance();
		date.setTime(readLong());
		return date;
	}

}
