package giraudsa.marshall.deserialisation.text;

import giraudsa.marshall.deserialisation.EntityManager;
import giraudsa.marshall.deserialisation.Unmarshaller;
import utils.ConfigurationMarshalling;

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public abstract class TextUnmarshaller<T> extends Unmarshaller<T> {

	protected final Reader reader;
	protected final DateFormat df;
	
	protected TextUnmarshaller(Reader reader, EntityManager entity, SimpleDateFormat dateFormat) throws ClassNotFoundException, IOException {
		super(entity, ConfigurationMarshalling.getEstIdUniversel());
		this.reader = reader;
		df = new SimpleDateFormat(dateFormat.toPattern());
		df.setTimeZone(dateFormat.getTimeZone());
	}
	
	public void dispose() throws IOException {
		reader.close();	
	}
	
	protected void setNom(ActionText<?> action, String nom) {
		action.setNom(nom);
	}
	
	protected String getNom(ActionText<?> action){
		return action.getNom();
	}
	
	protected Class<?> getType(ActionText<?> action, String nomAttribut) {
		return action.getType(nomAttribut);
	}

}
