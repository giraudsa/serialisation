package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import utils.Constants;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType<T extends Collection> extends ActionJsonComplexeObject<T> {
	ArrayList<?> remplacement = new ArrayList<>();
	
	public static ActionAbstrait<Collection> getInstance(JsonUnmarshaller<?> jsonUnmarshaller){
		return new ActionJsonCollectionType<>(Collection.class, jsonUnmarshaller);
	}
	
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller) {
		return new ActionJsonCollectionType<>(type, (JsonUnmarshaller<?>)unmarshaller);
	}
	
	private ActionJsonCollectionType(Class<T> type, JsonUnmarshaller<?> jsonUnmarshaller){
		super(type, jsonUnmarshaller);
		Class<?> _type = type;
		if(type.getName().toLowerCase().indexOf("hibernate") != -1) _type = ArrayList.class;
		try {
			obj = _type.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
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
		if(Constants.VALEUR.equals(nomAttribut)) return ArrayList.class;
		return null;
	}

	@Override
	protected void construitObjet() throws InstantiationException, IllegalAccessException {
		//rien a faire
	}

}
