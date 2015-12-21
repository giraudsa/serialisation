package giraudsa.marshall.serialisation.text.json.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Stack;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.NotImplementedSerializeException;
import giraudsa.marshall.serialisation.text.json.ActionJson;
import giraudsa.marshall.serialisation.text.json.JsonMarshaller;
import utils.TypeExtension;
import utils.champ.Champ;

public class ActionJsonObject extends ActionJson<Object> {

	public ActionJsonObject(JsonMarshaller b) {
		super(b);
	}


	@Override protected void ecritValeur(Object obj, TypeRelation relation, boolean ecrisSeparateur) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NotImplementedSerializeException {
		Stack<Comportement> tmp = new Stack<>();
		Class<?> typeObj = (Class<?>) obj.getClass();
		List<Champ> champs = TypeExtension.getSerializableFields(typeObj);
		Champ champId = TypeExtension.getChampId(typeObj);
		boolean serialiseTout = (isCompleteMarshalling() && ! isDejaVu(obj)) ||
									(!isCompleteMarshalling() && relation == TypeRelation.COMPOSITION && !isDejaTotalementSerialise(obj));
		tmp.push(traiteChamp(obj, champId, ecrisSeparateur));
		if(serialiseTout){
			getSmallIdAndStockObj(obj);
			for (Champ champ : champs){
				if (champ != champId){
					Comportement comportement = traiteChamp(obj, champ);
					if(comportement != null) tmp.push(comportement);
				}
			}
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
