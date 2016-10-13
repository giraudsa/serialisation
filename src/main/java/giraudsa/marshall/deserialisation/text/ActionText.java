package giraudsa.marshall.deserialisation.text;

import java.text.DateFormat;
import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import utils.champ.ChampUid;
import utils.champ.FieldInformations;

public abstract class ActionText<T> extends ActionAbstrait<T> {
	
	protected String nom;
	
	protected <U> ActionText(Class<T> type, TextUnmarshaller<U> unmarshaller) {
		super(type, unmarshaller);
	}
	void setNom(String nom){
		this.nom = nom;
	}
	void setFieldInformation(FieldInformations fi) {
		fieldInformations = fi;
	}

	String getNom() {
		return nom;
	}
	
	@SuppressWarnings("unchecked")
	protected <U> TextUnmarshaller<U> getTextUnarshaller(){
		return (TextUnmarshaller<U>)unmarshaller;
	}
	
	protected DateFormat getDateFormat(){
		return getTextUnarshaller().df;
	}

	Class<?> getType(String nomAttribut) {
		return getTypeAttribute(nomAttribut);
	}
	
	protected abstract Class<?> getTypeAttribute(String nomAttribut);
	
	FieldInformations getFieldInformation(String nom) {
		return getFieldInformationSpecialise(nom);
	}
	protected abstract FieldInformations getFieldInformationSpecialise(String nom);
	
	@SuppressWarnings("unchecked")
	protected <W> void preciseLeTypeSiIdConnu(String idAttribut, String id)
			throws EntityManagerImplementationException, InstanciationException {
		if(ChampUid.UID_FIELD_NAME.equals(idAttribut)){
			obj = getObject(id, type);
			if(obj != null)
				type = (Class<T>) obj.getClass();
		}
	}
}
