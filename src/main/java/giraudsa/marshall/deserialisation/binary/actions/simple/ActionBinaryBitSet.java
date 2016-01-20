package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.util.BitSet;

@SuppressWarnings("rawtypes")
public class ActionBinaryBitSet extends ActionBinarySimple<BitSet> {
	private ActionBinaryBitSet(Class<BitSet> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}

	public static ActionAbstrait<BitSet> getInstance(){ // NOSONAR
		return new ActionBinaryBitSet(BitSet.class, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U extends BitSet> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryBitSet(BitSet.class, (BinaryUnmarshaller<?>)unmarshaller);
	}

	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException {
		if (isDejaVu()){
			obj = getObjet();
		}else{
			int taille = readInt();
			obj = new BitSet(taille);
			for (int i = 0; i < taille; i++) {
				((BitSet)obj).set(i, readBoolean());
			}
			stockeObjetId();
		}
	}
}
