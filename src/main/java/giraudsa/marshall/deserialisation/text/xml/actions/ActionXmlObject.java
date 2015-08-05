package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.ActionXml;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utils.TypeExtension;
import utils.champ.Champ;


public class ActionXmlObject<T> extends ActionXml<T> {
	private Champ champId;
	private Map<Champ, Object> dicoChampToValue;
	
	public ActionXmlObject(Class<T> type, String nom, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, nom, xmlUnmarshaller);
		champId = TypeExtension.getChampId(type);
		dicoChampToValue = new HashMap<>();
	}

	@Override
	protected <U> void construitObjet(Unmarshaller<U> um) throws InstantiationException, IllegalAccessException {
		obj = getObject(dicoChampToValue.get(champId).toString(), type, champId.isFakeId());
		for(Entry<Champ, Object> entry : dicoChampToValue.entrySet()){
			Champ champ = entry.getKey();
			if (!champ.isFakeId()){
				if(!Modifier.isFinal(champ.info.getModifiers())){//on ne modifie pas les attributs finaux
					champ.set(obj, entry.getValue());
				}
			}
		}
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet){
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		dicoChampToValue.put(champ, objet);
	}

}
