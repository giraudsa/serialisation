package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ActionBinaryCalendar<C extends Calendar> extends ActionBinarySimple<C> {

	private ActionBinaryCalendar(Class<C> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Calendar> getInstance(){
		return new ActionBinaryCalendar<>(Calendar.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends C> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryCalendar<U>(type, (BinaryUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException {
		boolean isDejaVu = isDejaVu();
		if(isDejaVu)
			obj = getObjet();
		else{
			obj = new GregorianCalendar(readInt(), readInt(), readInt(), readInt(), readInt(), readInt());
					//day, month, day of month, hour of day, minute, second
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
