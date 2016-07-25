package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.math.BigInteger;

@SuppressWarnings("rawtypes")
public class ActionBinaryBigInteger extends ActionBinarySimple<BigInteger> {
	private ActionBinaryBigInteger(Class<BigInteger> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}

	public static ActionAbstrait<BigInteger> getInstance(){ // NOSONAR
		return new ActionBinaryBigInteger(BigInteger.class, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U extends BigInteger> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryBigInteger(BigInteger.class, (BinaryUnmarshaller<?>)unmarshaller);
	}

	@Override
	protected void initialise() throws IOException {
		if (isDejaVu()){
			obj = getObjet();
		}else{
			int taille = readInt();
			byte[] raw = new byte[taille];
			for(int i = 0; i<taille; ++i){
				raw[i] = readByte();
			}
			obj = new BigInteger(raw);
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
