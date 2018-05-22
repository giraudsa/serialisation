package giraudsa.marshall.deserialisation;

import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.exception.EntityManagerImplementationException;
import giraudsa.marshall.exception.InstanciationException;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;
import utils.champ.FieldInformations;

public abstract class ActionAbstrait<T> {

	protected FieldInformations fieldInformations;
	protected Object obj;
	protected Class<T> type;
	protected Unmarshaller<?> unmarshaller;

	protected ActionAbstrait(final Class<T> type, final Unmarshaller<?> unmarshaller) {
		this.type = type;
		this.unmarshaller = unmarshaller;
	}

	protected abstract void construitObjet()
			throws EntityManagerImplementationException, InstanciationException, SetValueException;

	protected Map<Object, UUID> getDicoObjToFakeId() {
		return unmarshaller.getDicoObjToFakeId();
	}

	protected EntityManager getEntityManager() {
		return unmarshaller.getEntityManager();
	}

	@SuppressWarnings("rawtypes")
	public abstract <U extends T> ActionAbstrait<U> getNewInstance(Class<U> type, Unmarshaller unmarshaller);

	protected <W> W getObject(final String id, final Class<W> type)
			throws EntityManagerImplementationException, InstanciationException {
		return unmarshaller.getObject(id, type);
	}

	protected Object getObjet() {
		return obj;
	}

	protected abstract <W> void integreObjet(String nomAttribut, W objet) throws EntityManagerImplementationException,
			InstanciationException, IllegalAccessException, SetValueException;

	protected boolean isUniversalId() {
		return true;
	}

	protected T newInstanceOfType() throws InstanciationException {
		return unmarshaller.newInstance(type);
	}

	protected abstract void rempliData(String donnees) throws InstanciationException;
}
