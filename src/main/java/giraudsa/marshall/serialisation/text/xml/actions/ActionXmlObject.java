package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Stack;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;
import utils.TypeExtension;
import utils.champ.Champ;

public class ActionXmlObject extends ActionXml<Object> {



	public ActionXmlObject(XmlMarshaller b) {
		super(b);
	}


	@Override protected void ecritValeur(Object obj, TypeRelation relation) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Stack<Comportement> tmp = new Stack<>();
		Class<?> typeObj = (Class<?>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean serialiseTout = (isCompleteMarshalling() && ! isDejaVu(obj)) ||
									(!isCompleteMarshalling() && relation == TypeRelation.COMPOSITION && !isDejaTotalementSerialise(obj));
		tmp.push(traiteChamp(obj, champId));
		setDejaVu(obj);
		if(serialiseTout){
			setDejaTotalementSerialise(obj);
			for (Champ champ : champs){
				if (champ != champId){
					Comportement comportement = traiteChamp(obj, champ);
					if(comportement != null) tmp.push(comportement);
				}
			}
		}
		pushComportements(tmp);
	}
}
