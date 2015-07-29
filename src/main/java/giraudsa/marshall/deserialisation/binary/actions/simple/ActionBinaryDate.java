package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.ActionBinary;
import giraudsa.marshall.exception.NotImplementedSerializeException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class ActionBinaryDate extends ActionBinary<Date> {

	public ActionBinaryDate(Class<? extends Date> type, Unmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected Date readObject(Class<? extends Date> typeADeserialiser, TypeRelation typeRelation, int smallId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException, IOException, NotImplementedSerializeException {
		boolean isDejaVu = isDejaVu(smallId);
		if(isDejaVu) return (Date) getObjet(smallId);
		Date date = type.newInstance();
		date.setTime(readLong());
		stockeObjetId(smallId, date);
		return date;
	}
}
