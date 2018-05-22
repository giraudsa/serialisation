package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionXmlObject extends ActionXml<Object> {

	public ActionXmlObject() {
		super();
	}

	@Override
	protected void ecritValeur(final Marshaller marshaller, final Object obj, final FieldInformations fieldInformations,
			final boolean serialiseTout)
			throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException,
			NotImplementedSerializeException, IOException, MarshallExeption {
		final Class<?> typeObj = obj.getClass();
		final List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		setDejaTotalementSerialise(marshaller, obj);
		final Deque<Comportement> tmp = new ArrayDeque<>();
		for (final Champ champ : champs) {
			final Comportement comportement = traiteChamp(marshaller, obj, champ);
			if (comportement != null)
				tmp.push(comportement);
		}
		pushComportements(marshaller, tmp);
	}

	@Override
	protected void pushComportementParticulier(final Marshaller marshaller, final Object obj, final String nomBalise,
			final FieldInformations fieldInformations) {
		pushComportement(marshaller, newComportementSerialiseObject(obj, nomBalise, fieldInformations));
	}

}
