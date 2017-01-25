package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class ActionBinaryDate<D extends Date> extends ActionBinarySimple<D> {

	private ActionBinaryDate(Class<D> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Date> getInstance(){
		return new ActionBinaryDate<>(Date.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends D> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionBinaryDate<>(type, (BinaryUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException, UnmarshallExeption{
		boolean isDejaVu = isDejaVu();
		if(isDejaVu)
			obj = getObjet();
		else{
			try {
				obj = type.getConstructor(long.class).newInstance(readLong());
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new UnmarshallExeption("impossible de construire la date de type " + type.getName(), e);
			}
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
