package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlEscapeUtil;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.SetValueException;
import giraudsa.marshall.exception.UnmarshallExeption;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

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
	
	public void setId(String id) throws InstanciationException, EntityManagerImplementationException{
		preciseLeTypeSiIdConnu(ChampUid.UID_FIELD_NAME, id);
		Champ champId = TypeExtension.getChampId(type);
		Class<?> typeId = champId.getValueType();
		if (typeId.isAssignableFrom(UUID.class)){
			dicoNomChampToValue.put(ChampUid.UID_FIELD_NAME, UUID.fromString(id));
			return;
		}
		Object objId;
		typeId = TypeExtension.getTypeEnveloppe(typeId);
		if (TypeExtension.isEnveloppe(typeId) || typeId.isAssignableFrom(String.class)){
			try {
				objId = typeId.getConstructor(String.class).newInstance(XmlEscapeUtil.unescape(id));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new InstanciationException("impossible d'instancier un objet de type " + typeId 
						+ " avec la valeur " + System.lineSeparator() +  XmlEscapeUtil.unescape(id), e);
			}
			dicoNomChampToValue.put(ChampUid.UID_FIELD_NAME, objId);
		}
	}
	
	
	@Override
	protected  void construitObjet() throws EntityManagerImplementationException, InstanciationException, SetValueException{
		for(Entry<String, Object> entry : dicoNomChampToValue.entrySet()){
			FieldInformations champ = TypeExtension.getChampByName(type, entry.getKey());
			champ.set(obj, entry.getValue(), getDicoObjToFakeId());
		}
	}
	
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) throws EntityManagerImplementationException, InstanciationException{
		preciseLeTypeSiIdConnu(nomAttribut, objet != null ? objet.toString() : null);
		dicoNomChampToValue.put(nomAttribut, objet);
	}
	
    @Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
    	FieldInformations champ = TypeExtension.getChampByName(type, nomAttribut);
		if (champ.isSimple())
			return TypeExtension.getTypeEnveloppe(champ.getValueType());//on renvoie Integer à la place de int, Double au lieu de double, etc...
		return champ.getValueType();
	}
    
    @Override 
    protected FieldInformations getFieldInformationSpecialise(String nomAttribut){
		return TypeExtension.getChampByName(type, nomAttribut);
	}
}
