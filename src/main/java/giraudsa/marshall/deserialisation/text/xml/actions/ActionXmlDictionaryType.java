package giraudsa.marshall.deserialisation.text.xml.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.xml.XmlUnmarshaller;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.lang.reflect.Type;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ActionXmlDictionaryType<T extends Map> extends ActionXmlComplexeObject<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionXmlDictionaryType.class);
	private Object keyTampon;
	private FakeChamp fakeChampKey;
	private FakeChamp fakeChampValue;
	private ActionXmlDictionaryType(Class<T> type, XmlUnmarshaller<?> xmlUnmarshaller){
		super(type, xmlUnmarshaller);
		if(!type.isInterface()){
			try {
				obj = type.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				LOGGER.error("instanciation impossible pour " + type.getName(), e);
			}
		}
	}

	private FakeChamp getFakeChamp(){
		if(keyTampon == null){
			if (fakeChampKey == null){
				Type[] types = fieldInformations.getParametreType();
				Type typeGeneric = Object.class;
				if(types != null && types.length > 0) 
					typeGeneric = types[0];
				fakeChampKey = new FakeChamp("K", typeGeneric, fieldInformations.getRelation());
			}
			return fakeChampKey;
		}
		if(fakeChampValue == null){
			Type[] types = fieldInformations.getParametreType();
			Type typeGeneric = Object.class;
			if(types != null && types.length > 1)
				typeGeneric = types[1];
			fakeChampValue = new FakeChamp("V", typeGeneric, fieldInformations.getRelation());
		}
		return fakeChampValue;
	}
	
	public static ActionAbstrait<Map> getInstance() {	
		return new ActionXmlDictionaryType<>(Map.class, null);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionXmlDictionaryType<>(type, (XmlUnmarshaller<?>)unmarshaller);
	}
	
	@SuppressWarnings("unchecked") @Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(keyTampon == null){
			keyTampon = objet;
		}else{
			((Map)obj).put(keyTampon, objet);
			keyTampon = null;
		}
	}

	@Override
	protected void construitObjet() {
		//rien a faire
	}

	@Override
	protected FieldInformations getFieldInformationSpecialise(String nom) {
		return getFakeChamp();
	}
	
	@Override
	protected Class<?> getTypeAttribute(String nomAttribut) {
		return getFakeChamp().getValueType();
	}
}
