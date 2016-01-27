package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;
import java.io.IOException;
import java.math.BigDecimal;

@SuppressWarnings("rawtypes")
public class ActionBinaryBigDecimal extends ActionBinarySimple<BigDecimal> {
	private ActionBinaryBigDecimal(Class<BigDecimal> type, BinaryUnmarshaller<?> b){
		super(type, b);
	}

	public static ActionAbstrait<BigDecimal> getInstance(){ // NOSONAR
		return new ActionBinaryBigDecimal(BigDecimal.class, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <U extends BigDecimal> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryBigDecimal(BigDecimal.class, (BinaryUnmarshaller<?>)unmarshaller);
	}

	@Override
	protected void initialise() throws IOException, InstantiationException, IllegalAccessException {
		if (isDejaVu()){
			obj = getObjet();
		}else{
			obj = new BigDecimal(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
