package utils.champ;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;
import utils.TypeExtension;
import utils.generic.TypeToken;

public class FakeChamp implements FieldInformations {
	private static final Annotation[] noAnnotation = new Annotation[0];
	private final Annotation[] annotations;
	private final boolean isSimple;
	private final String name;
	private final TypeRelation relation;
	private final TypeToken<?> typeToken;
	private final int naturePrimitive;

	/** Construit le FakeChamp des éléments, clés ou valeurs portés par un champ. */
	static FakeChamp pourParametre(final FieldInformations fi, final int role) {
		final Type[] types = fi.getParametreType();
		final Type type;
		if (role == ELEMENT)
			type = types != null && types.length > 0 ? types[0] : Object.class;
		else
			type = types != null && types.length > 1 ? types[role - 1] : Object.class;
		final String nom = role == CLE ? "K" : role == VALEUR ? "V" : null;
		return new FakeChamp(nom, type, fi.getRelation(), fi.getAnnotations());
	}

	public FakeChamp(final String name, final Type type, final TypeRelation relation, final Annotation[] annotations) {
		super();
		this.name = name;
		typeToken = TypeToken.get(type);
		this.relation = relation;
		isSimple = TypeExtension.isSimple(typeToken.getRawType());
		naturePrimitive = AccesChamp.nature(typeToken.getRawType());
		this.annotations = annotations == null ? noAnnotation : annotations;
	}

	@Override
	public Object get(final Object o) throws IllegalAccessException {
		return get(o, null, null);
	}

	@Override
	public Object get(final Object o, final Map<Object, UUID> dicoObjToFakeId, final EntityManager entity)
			throws IllegalAccessException {
		return o;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
		for (final Annotation annotation : getAnnotations())
			if (annotationClass.isInstance(annotation))
				return (T) annotation;
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return annotations;
	}

	@Override
	public String getName() {
		return name;
	}

	private final StatsDedoublonnage statsDedoublonnage = new StatsDedoublonnage();
	/** FakeChamps des paramètres (éléments, clés, valeurs), calculés à la demande. */
	private volatile FakeChamp[] champsParametres;

	@Override
	public boolean isDedoublonnageUtile() {
		return statsDedoublonnage.isUtile();
	}

	@Override
	public void noteDedoublonnage(final boolean trouvee) {
		statsDedoublonnage.note(trouvee);
	}

	@Override
	public FakeChamp getChampParametre(final int role) {
		FakeChamp[] t = champsParametres;
		if (t == null) {
			t = new FakeChamp[3];
			champsParametres = t;
		}
		FakeChamp champ = t[role];
		if (champ == null) {
			champ = FakeChamp.pourParametre(this, role);
			t[role] = champ; // course bénigne : deux calculs donnent des champs équivalents
		}
		return champ;
	}

	@Override
	public int getNaturePrimitive() {
		return naturePrimitive;
	}

	@Override
	public Type[] getParametreType() {
		final Type type = typeToken.getType();
		if (type instanceof ParameterizedType)
			return ((ParameterizedType) type).getActualTypeArguments();
		return new Type[0];
	}

	@Override
	public TypeRelation getRelation() {
		return relation;
	}

	@Override
	public Class<?> getValueType() {
		return typeToken.getRawType();
	}

	@Override
	public boolean isChampId() {
		return false;
	}

	@Override
	public boolean isSimple() {
		return false;
	}

	@Override
	public boolean isTypeDevinable(final Object o) {
		final Class<?> valueType = TypeExtension.getClasseASerialiser(o);
		return isSimple || typeToken.getRawType() == valueType;
	}

	@Override
	public void set(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId)
			throws SetValueException {
		// rien à faire

	}

}
