package giraudsa.marshall.serialisation.text.xml.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.xml.ActionXml;
import giraudsa.marshall.serialisation.text.xml.XmlMarshaller;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionXmlObject extends ActionXml<Object> {



	public ActionXmlObject(XmlMarshaller b) {
		super(b);
	}


	@Override protected void ecritValeur(Object obj, FieldInformations fieldInformations) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Class<?> typeObj = (Class<?>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean serialiseTout = serialiseTout(obj, fieldInformations);
		setDejaVu(obj);
		if(!serialiseTout){
			pushComportement(traiteChamp(obj, champId));
			return;
		}
		setDejaTotalementSerialise(obj);
		Deque<Comportement> tmp = new ArrayDeque<>();
		for (Champ champ : champs){
			Comportement comportement = traiteChamp(obj, champ);
			if(comportement != null) 
				tmp.push(comportement);
		}
		pushComportements(tmp);
	}

}
