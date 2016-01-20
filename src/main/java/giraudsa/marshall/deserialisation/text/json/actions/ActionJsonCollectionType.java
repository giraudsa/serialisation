package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType<T extends Collection> extends ActionJsonComplexeObject<T> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonCollectionType.class);
	private FakeChamp fakeChamp;
	private ActionJsonCollectionType(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller){
		super(type, jsonUnmarshaller);
		Class<?> ttype = type;
		if(type.getName().toLowerCase().indexOf("hibernate") != -1 || type.isInterface())
			ttype = ArrayList.class;
		try {
			obj = ttype.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			LOGGER.debug("impossible de créer une instance de " + ttype.getName(), e);
			obj = new ArrayList<>();
		}
	}

	private FakeChamp getFakeChamp(){
		if(fakeChamp == null){
			Type[] types = fieldInformations.getParametreType();
			Type typeGeneric = Object.class;
			if(types != null && types.length > 0)
				typeGeneric = types[0];
			fakeChamp = new FakeChamp("V", typeGeneric, fieldInformations.getRelation());
		}
		return fakeChamp;
	}
	
	public static ActionAbstrait<Collection> getInstance(){
		return new ActionJsonCollectionType<>(Collection.class, null);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonCollectionType<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(nomAttribut == null){
			((Collection)obj).add(objet);
		}else{
			for(Object o : (ArrayList)objet){
				((Collection)obj).add(o);
			}
		}
	}
	
	@Override protected Class<?> getTypeAttribute(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut))
			return ArrayList.class;
		return getFakeChamp().getValueType();
	}
	
	@Override
	protected FieldInformations getFieldInformationSpecialise(String nomAttribut) {
		if(Constants.VALEUR.equals(nomAttribut))
			return fieldInformations;
		return getFakeChamp();
	}

	@Override
	protected void construitObjet() throws InstantiationException, IllegalAccessException {
		//rien a faire
	}

}
