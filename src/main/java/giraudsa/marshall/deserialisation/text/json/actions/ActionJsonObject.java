package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.SetValueException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.ChampUid;
import utils.champ.FieldInformations;

public class ActionJsonObject<T> extends ActionJson<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonObject.class);
	private Map<String, Object> dicoNomChampToValue;
	
	private ActionJsonObject(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
		dicoNomChampToValue = new HashMap<>();
	}

	public static ActionAbstrait<Object> getInstance(){
		return new ActionJsonObject<>(Object.class, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonObject<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}

	
	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		FieldInformations champ = TypeExtension.getChampByName(type, nomAttribut);
		if (champ.isSimple())
			return TypeExtension.getTypeEnveloppe(champ.getValueType());//on renvoie Integer à la place de int, Double au lieu de double, etc...
		return champ.getValueType();
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) throws EntityManagerImplementationException, InstanciationException{
		if(!preciseLeTypeSiIdConnu(nomAttribut, objet != null ? objet.toString() : null))
			dicoNomChampToValue.put(nomAttribut, objet);
	}
	
	@Override
	protected void construitObjet() throws EntityManagerImplementationException, InstanciationException, SetValueException{
		for(Entry<String, Object> entry : dicoNomChampToValue.entrySet()){
			FieldInformations champ = TypeExtension.getChampByName(type, entry.getKey());
			champ.set(obj, entry.getValue(), getDicoObjToFakeId());
		}
	}
	
	@Override 
    protected FieldInformations getFieldInformationSpecialise(String nomAttribut){
		return TypeExtension.getChampByName(type, nomAttribut);
	}


	@Override
	protected void rempliData(String donnees){
		LOGGER.error("on est pas supposé avoir de données avec un objet.");
		//rien a faire avec un objet, il n'y a pas de data
	}

}
