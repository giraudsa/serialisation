package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;

import giraudsa.marshall.exception.MarshallExeption;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

public class ActionXmlArrayType  extends ActionXml<Object> {
	
	public ActionXmlArrayType() {
		super();
	}
	
	@Override
	protected void ecritValeur(Marshaller marshaller, Object obj, FieldInformations fi, boolean serialiseTout) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException, MarshallExeption{
		Type genericType = obj.getClass().getComponentType();
		String clef = genericType instanceof Class ? ((Class<?>)genericType).getSimpleName() : "Value";
		FakeChamp fakeChamp = new FakeChamp(clef, genericType, fi.getRelation(), fi.getAnnotations());
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (int i = 0; i < Array.getLength(obj); ++i) {
			tmp.push(traiteChamp(marshaller, Array.get(obj, i), fakeChamp));
		}
		pushComportements(marshaller, tmp);
	}
	
	@Override
	protected void pushComportementParticulier(Marshaller marshaller, Object obj, String nomBalise,
			FieldInformations fieldInformations) {
		newComportementFermeBalise(nomBalise);
		newComportementOuvreBaliseEtEcritValeur(obj, nomBalise, fieldInformations);
	}
}
