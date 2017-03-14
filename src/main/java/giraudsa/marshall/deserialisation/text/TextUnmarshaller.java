package giraudsa.marshall.deserialisation.text;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.exception.FabriqueInstantiationException;
import utils.EntityManager;
import utils.TypeExtension;
import utils.champ.FakeChamp;
import utils.champ.FieldInformations;

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class TextUnmarshaller<T> extends Unmarshaller<T> {

	protected final Reader reader;
	protected final DateFormat df;
	
	protected TextUnmarshaller(Reader reader, EntityManager entity, SimpleDateFormat dateFormat) throws FabriqueInstantiationException{
		super(entity);
		this.reader = reader;
		df = new SimpleDateFormat(dateFormat.toPattern());
		df.setTimeZone(dateFormat.getTimeZone());
	}
	
	@Override
	public void dispose() throws IOException {
		reader.close();	
	}
	
	protected void setNom(ActionText<?> action, String nom) {
		if(action != null)
			action.setNom(nom);
	}
	
	protected void setFieldInformation(ActionText<?> action){
		FieldInformations fi;
		String nom = action.getNom();
		ActionText<?> actionEnCours = (ActionText<?>)getActionEnCours();
		if(actionEnCours != null){
			fi = actionEnCours.getFieldInformation(nom);
		}else{
			fi = new FakeChamp(nom, Object.class, TypeRelation.COMPOSITION, null);
		}
		action.setFieldInformation(fi);
	}
	
	protected String getNom(ActionText<?> action){
		return action.getNom();
	}
	
	protected Class<?> getType(String nomAttribut) {
		ActionText<?> action = (ActionText<?>) getActionEnCours(); 
		return action == null ? Object.class : TypeExtension.getTypeEnveloppe(action.getType(nomAttribut));
	}
}
