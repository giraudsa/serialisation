package giraudsa.marshall.deserialisation.binary.actions.simple;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.binary.BinaryUnmarshaller;

import java.io.IOException;
import java.util.Currency;


public class ActionBinaryCurrency extends ActionBinarySimple<Currency> {

	private ActionBinaryCurrency(Class<Currency> type, BinaryUnmarshaller<?> unmarshaller) {
		super(type, unmarshaller);
	}

	public static ActionAbstrait<Currency> getInstance(){
		return new ActionBinaryCurrency(Currency.class, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <U extends Currency> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionBinaryCurrency(Currency.class, (BinaryUnmarshaller<?>) unmarshaller);
	}
	
	@Override
	protected void initialise() throws IOException{
		if(isDejaVu())
			obj = getObjet();
		else{
			obj = Currency.getInstance(readUTF());
			stockeObjetId();
			setDejaTotalementDeSerialise();
		}
	}
}
