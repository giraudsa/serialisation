package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongArray;

@SuppressWarnings("rawtypes")
public class ActionBinaryAtomicLongArray extends ActionBinarySimple<AtomicLongArray> {
	private ActionBinaryAtomicLongArray(Class<AtomicLongArray> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}

	public static ActionAbstrait<AtomicLongArray> getInstance(){ // NOSONAR
		return new ActionBinaryAtomicLongArray(AtomicLongArray.class, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U extends AtomicLongArray> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicLongArray(AtomicLongArray.class, (BinaryUnmarshaller<?>)unmarshaller);
	}

	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException {
		if (isDejaVu()){
			obj = getObjet();
		}else{
			int taille = readInt();
			obj = new AtomicLongArray(taille);
			for (int i = 0 ; i < taille; ++i){
				((AtomicLongArray)obj).set(i, readLong());
			}
			stockeObjetId();
		}
	}
}
