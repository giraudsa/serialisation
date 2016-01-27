package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class ActionBinaryDate<DateType extends Date> extends ActionBinarySimple<DateType> {

	private ActionBinaryDate(Class<DateType> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Date> getInstance(){
		return new ActionBinaryDate<Date>(Date.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends DateType> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryDate<U>(type, (BinaryUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		boolean isDejaVu = isDejaVu();
		if(isDejaVu)
			obj = getObjet();
		else{
			obj = type.getConstructor(long.class).newInstance(readLong());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
