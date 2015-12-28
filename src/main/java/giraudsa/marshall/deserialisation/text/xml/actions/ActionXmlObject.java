package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utils.TypeExtension;
import utils.champ.Champ;


public class ActionXmlObject<T> extends ActionXmlComplexeObject<T> {
	private Champ champId;
	private Map<Champ, Object> dicoChampToValue;
	
	public static ActionAbstrait<?> getInstance(XmlUnmarshaller<?> u) {	
		return new ActionXmlObject<Object>(Object.class, u);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlObject<>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	private ActionXmlObject(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
		champId = TypeExtension.getChampId(type);
		dicoChampToValue = new HashMap<>();
	}

	@Override
	protected  void construitObjet() throws InstantiationException, IllegalAccessException {
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
	
    @Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		if (champ.isSimple)
			return TypeExtension.getTypeEnveloppe(champ.valueType);//on renvoie Integer à la place de int, Double au lieu de double, etc...
		return champ.valueType;
	}
}
