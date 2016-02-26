package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Stack;

import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.Marshaller;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.FieldInformations;

public class ActionJsonObject extends ActionJson<Object> {

	public ActionJsonObject() {
		super();
	}


	@Override protected void ecritValeur(Marshaller marshaller, Object obj, FieldInformations fieldInformations, boolean ecrisSeparateur) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException, NotImplementedSerializeException, IOException{
		Class<?> typeObj = (Class<?>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean serialiseTout = serialiseTout(marshaller, obj, fieldInformations);
		setDejaVu(marshaller, obj);
		if(!serialiseTout){
			pushComportement(marshaller, traiteChamp(marshaller, obj, champId, ecrisSeparateur));
			return;
		}
		setDejaTotalementSerialise(marshaller, obj);
		Stack<Comportement> tmp = new Stack<Comportement>();
		boolean virgule = ecrisSeparateur;
		for (Champ champ : champs){
			Comportement comportement = traiteChamp(marshaller, obj, champ, virgule);
			virgule = true;
			if(comportement != null) 
				tmp.push(comportement);
		}
		pushComportements(marshaller, tmp);
	}
	
	@Override protected void clotureObject(Marshaller marshaller, Object obj, boolean typeDevinable) throws IOException {
		fermeAccolade(marshaller);
	}

	@Override protected boolean commenceObject(Marshaller marshaller, Object obj, boolean typeDevinable) throws IOException {
		ouvreAccolade(marshaller);
		if(!typeDevinable) {
			ecritType(marshaller, obj);
			return true;
		}
		return false;
	}
}
