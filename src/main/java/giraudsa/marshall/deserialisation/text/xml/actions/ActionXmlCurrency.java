package giraudsa.marshall.deserialisation.text.xml.actions;

import java.util.Currency;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

public class ActionXmlCurrency extends ActionXmlSimpleComportement<Currency> {

	public static ActionAbstrait<Currency> getInstance() {
		return new ActionXmlCurrency(Currency.class, null);
	}

	private ActionXmlCurrency(final Class<Currency> type, final XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
	}

	@Override
	protected void construitObjet() {
		obj = Currency.getInstance(sb.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <U extends Currency> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return (ActionAbstrait<U>) new ActionXmlCurrency(Currency.class, (XmlUnmarshaller<?>) unmarshaller);
	}

}
