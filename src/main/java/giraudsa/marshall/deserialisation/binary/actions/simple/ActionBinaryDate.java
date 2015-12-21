package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.Date;

public class ActionBinaryDate<DateType extends Date> extends ActionBinarySimple<DateType> {

	public static ActionAbstrait<Date> getInstance(BinaryUnmarshaller<?> bu){
		return new ActionBinaryDate<Date>(Date.class, bu);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends DateType> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryDate<U>(type, (BinaryUnmarshaller<?>)unmarshaller);
	}
	
	private ActionBinaryDate(Class<DateType> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException {
		boolean isDejaVu = isDejaVu();
		if(isDejaVu) obj = getObjetDejaVu();
		else{
			obj = type.newInstance();
			((Date)obj).setTime(readLong());
			stockeObjetId();
		}
	}
}
