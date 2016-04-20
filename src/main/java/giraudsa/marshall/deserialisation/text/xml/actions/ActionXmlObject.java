package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import utils.TypeExtension;
import utils.champ.Champ;
import utils.champ.ChampUid;
import utils.champ.FieldInformations;


public class ActionXmlObject<T> extends ActionXmlComplexeObject<T> {
	private Map<String, Object> dicoNomChampToValue;
	
	private ActionXmlObject(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller) {
		super(type, xmlUnmarshaller);
		dicoNomChampToValue = new HashMap<>();
	}

	public static  ActionAbstrait<Object> getInstance() {	
		return new ActionXmlObject<>(Object.class, null);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlObject<>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected  void construitObjet() throws InstantiationException, IllegalAccessException {
		obj = getObject(dicoNomChampToValue.get(ChampUid.UID_FIELD_NAME).toString(), type);
		if(obj == null)
			return;
		type = (Class<T>) obj.getClass();
		for(Entry<String, Object> entry : dicoNomChampToValue.entrySet()){
			Champ champ = TypeExtension.getChampByName(type, entry.getKey());
			if (champ != null && !champ.isFakeId()){
				champ.set(obj, entry.getValue());
			}
		}
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet){
		dicoNomChampToValue.put(nomAttribut, objet);
	}
	
    @Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		Champ champ = TypeExtension.getChampByName(type, nomAttribut);
		if (champ.isSimple())
			return TypeExtension.getTypeEnveloppe(champ.getValueType());//on renvoie Integer à la place de int, Double au lieu de double, etc...
		return champ.getValueType();
	}
    
    @Override 
    protected FieldInformations getFieldInformationSpecialise(String nomAttribut){
		return TypeExtension.getChampByName(type, nomAttribut);
	}
}
