package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionJsonObject extends ActionJson<Object> {

	public ActionJsonObject(JsonMarshaller b) {
		super(b);
	}


	@Override protected void ecritValeur(Object obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Class<?> typeObj = (Class<?>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean serialiseTout = serialiseTout(obj, fieldInformations);
		setDejaVu(obj);
		if(!serialiseTout){
			pushComportement(traiteChamp(obj, champId, ecrisSeparateur));
			return;
		}
		setDejaTotalementSerialise(obj);
		Deque<Comportement> tmp = new ArrayDeque<>();
		boolean virgule = ecrisSeparateur;
		for (Champ champ : champs){
			Comportement comportement = traiteChamp(obj, champ, virgule);
			virgule = true;
			if(comportement != null) 
				tmp.push(comportement);
		}
		pushComportements(tmp);
	}
	
	@Override protected void fermeAccolade(Object obj, boolean typeDevinable) throws IOException {
		write("}");
	}

	@Override protected boolean ouvreAccolade(Object obj, boolean typeDevinable) throws IOException {
		write("{");
		if(!typeDevinable) {
			ecritType(obj);
			return true;
		}
		return false;
	}
}
