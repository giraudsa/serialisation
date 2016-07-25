package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicIntegerArray;

@SuppressWarnings("rawtypes")
public class ActionBinaryAtomicIntegerArray extends ActionBinarySimple<AtomicIntegerArray> {
	private ActionBinaryAtomicIntegerArray(Class<AtomicIntegerArray> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}

	public static ActionAbstrait<AtomicIntegerArray> getInstance(){ // NOSONAR
		return new ActionBinaryAtomicIntegerArray(AtomicIntegerArray.class, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U extends AtomicIntegerArray> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryAtomicIntegerArray(AtomicIntegerArray.class, (BinaryUnmarshaller<?>)unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu()){
			obj = getObjet();
		}else{
			int taille = readInt();
			obj = new AtomicIntegerArray(taille);
			for (int i = 0 ; i < taille; ++i){
				((AtomicIntegerArray)obj).set(i, readInt());
			}
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
