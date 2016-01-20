package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.Currency;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlCurrency extends ActionXmlSimpleComportement<Currency>{

	private ActionXmlCurrency(Class<Currency> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	public static ActionAbstrait<Currency> getInstance() {	
		return new ActionXmlCurrency(Currency.class, null);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Currency> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlCurrency(Currency.class, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@Override protected void construitObjet() {
		obj = Currency.getInstance(sb.toString());
	}

}
