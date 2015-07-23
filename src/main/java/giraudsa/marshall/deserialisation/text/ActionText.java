package giraudsa.marshall.deserialisation.text;

import giraudsa.marshall.deserialisation.ActionAbstrait;

public class ActionText<T> extends ActionAbstrait<T> {

	public ActionText(Class<T> type, String nom) {
		super(type, nom);
	}

}
