package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
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


	@Override protected void ecritValeur(Marshaller marshaller, Object obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Class<?> typeObj = (Class<?>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean serialiseTout = serialiseTout(marshaller, obj, fieldInformations);
		setDejaVu(marshaller, obj);
		if(!serialiseTout){
			pushComportement(marshaller, traiteChamp(marshaller, obj, champId));
			return;
		}
		setDejaTotalementSerialise(marshaller, obj);
		Deque<Comportement> tmp = new ArrayDeque<Comportement>();
		for (Champ champ : champs){
			Comportement comportement = traiteChamp(marshaller, obj, champ);
			if(comportement != null) 
				tmp.push(comportement);
		}
		pushComportements(marshaller, tmp);
	}

}
