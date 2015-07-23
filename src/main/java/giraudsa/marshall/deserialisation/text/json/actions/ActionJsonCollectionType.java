package giraudsa.marshall.deserialisation.text.json.actions;

import giraudsa.marshall.deserialisation.text.json.ActionJson;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings("rawtypes")
public class ActionJsonCollectionType<T extends Collection> extends ActionJson<T> {
	ArrayList<?> remplacement = new ArrayList<>();
	public ActionJsonCollectionType(Class<T> type, String nom) throws InstantiationException, IllegalAccessException {
		super(type, nom);
		obj = type.newInstance();
	}

	@Override
	protected Class<?> getType(String clefEnCours) {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(String nomAttribut, W objet) {
		if(nomAttribut == null){
			obj.add(objet);
		}else{
			for(Object o : (ArrayList)objet){
				obj.add(o);
			}
		}
	}
}
