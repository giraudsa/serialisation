package utils.champ;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;

public interface FieldInformations {
	public Object get(Object o) throws IllegalAccessException;

	public Object get(Object o, Map<Object, UUID> dicoObjToFakeId, EntityManager entity) throws IllegalAccessException;

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass);

	public Annotation[] getAnnotations();

	public String getName();

	/** Champ des éléments d'une collection (index 0), des clés (1) ou des valeurs (2) d'une map. */
	public static final int ELEMENT = 0;
	public static final int CLE = 1;
	public static final int VALEUR = 2;

	/**
	 * @return le FakeChamp décrivant les éléments (ELEMENT), clés (CLE) ou valeurs (VALEUR) portés par ce champ,
	 *         construit une seule fois.
	 */
	public FakeChamp getChampParametre(int role);

	public Type[] getParametreType();

	public TypeRelation getRelation();

	public Class<?> getValueType();

	public boolean isChampId();

	public boolean isSimple();

	public boolean isTypeDevinable(Object o);

	public void set(Object obj, Object value, Map<Object, UUID> dicoObjToFakeId) throws SetValueException;
}
