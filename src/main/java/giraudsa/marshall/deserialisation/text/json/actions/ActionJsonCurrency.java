package giraudsa.marshall.deserialisation.text.json.actions;

import java.util.Currency;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;

public class ActionJsonCurrency extends ActionJsonSimpleComportement<Currency> {

	private ActionJsonCurrency(Class<Currency> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}
	
	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {	
		return (ActionAbstrait<U>) new ActionJsonCurrency(Currency.class, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Currency> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionJsonSimpleComportement<>(Currency.class, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	@Override
	protected void rempliData(String donnees)  {
		obj = Currency.getInstance(donnees);
	}


}
