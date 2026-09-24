package utils.champ;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

import giraudsa.marshall.annotations.MarshallAsAttribute;
import giraudsa.marshall.annotations.Relation;
import giraudsa.marshall.annotations.TypeRelation;
import giraudsa.marshall.exception.SetValueException;
import utils.EntityManager;
import utils.TypeExtension;
import utils.generic.TypeToken;

public class Champ implements Comparable<Champ>, FieldInformations {
	private static final Annotation[] noAnnotation = new Annotation[0];
	private String comparaison;
	private int hash;
	/** le nom sérialisé est "id" (peut venir de MarshallAsAttribute). */
	private boolean nomEstId;
	private final Field info;
	/** nature primitive du champ (voir {@link AccesChamp}), AUCUNE pour un type objet. */
	private final int naturePrimitive;
	/**
	 * accès direct au champ, créé au premier usage. Pas volatile : les accès sont immuables (champs final), une
	 * publication concurrente est donc sûre ; au pire deux threads en créent chacun un.
	 */
	private AccesChamp acces;

	private final boolean isChampId;
	private final boolean isSimple;
	protected String name;
	private TypeRelation relation;
	protected TypeToken<?> typeToken;
	protected Class<?> valueType;

	Champ(final Field info, final boolean isSimple, final boolean isChampId) {
		this.info = info;
		this.isSimple = isSimple;
		this.isChampId = isChampId;
		naturePrimitive = info == null ? AccesChamp.AUCUNE : AccesChamp.nature(info.getType());
		if (info != null) {
			typeToken = TypeToken.get(info.getGenericType());
			valueType = info.getType();
			final MarshallAsAttribute metadata = info.getAnnotation(MarshallAsAttribute.class);
			name = metadata != null ? metadata.name() : info.getName();
			nomEstId = ChampUid.UID_FIELD_NAME.equals(name);
			final Relation maRelation = info.getAnnotation(Relation.class);
			if (isSimple)
				relation = TypeRelation.COMPOSITION;
			else
				relation = maRelation != null ? maRelation.type() : TypeRelation.ASSOCIATION;

		}
	}

	@Override
	public int compareTo(final Champ other) {
		int res = -1;
		if (isSimple == other.isSimple)
			res = getComparaison().compareTo(other.getComparaison());
		else if (!isSimple && other.isSimple)
			res = 1;
		return res;
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Champ)
			return compareTo((Champ) other) == 0;
		return false;
	}

	@Override
	public Object get(final Object o) throws IllegalAccessException {
		return get(o, null, null);
	}

	@Override
	public Object get(final Object obj, final Map<Object, UUID> dicoObjToFakeId, final EntityManager entity)
			throws IllegalAccessException {
		if (obj == null)
			return info.get(obj); // même exception qu'avant
		return acces().get(obj);
	}

	/** @return la nature primitive du champ (constantes de {@link AccesChamp}), AUCUNE pour un type objet. */
	@Override
	public int getNaturePrimitive() {
		return naturePrimitive;
	}

	/** @return l'accès direct au champ (null pour un faux id). */
	public AccesChamp getAcces() {
		return info == null ? null : acces();
	}

	private AccesChamp acces() {
		AccesChamp a = acces;
		if (a == null) {
			a = GenerateurAcces.cree(info);
			acces = a; // course bénigne : deux accès équivalents
		}
		return a;
	}

	/**
	 * Écrit la valeur par l'accès direct ; en cas de valeur à convertir (élargissement, null dans un primitif,
	 * mauvais type), on repasse par {@link Field#set} qui applique les règles et exceptions de la réflexion.
	 */
	private void ecrit(final Object obj, final Object value) throws IllegalAccessException {
		try {
			acces().set(obj, value);
		} catch (final ClassCastException | NullPointerException e) {
			info.set(obj, value);
		}
	}

	@Override
	public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
		if (info == null)
			return null;
		return info.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getAnnotations() {
		if (info == null)
			return noAnnotation;
		return info.getAnnotations();
	}

	private String getComparaison() {
		if (comparaison == null) {
			final StringBuilder sb = new StringBuilder();
			if (name.equals(ChampUid.UID_FIELD_NAME))
				sb.append("0");
			sb.append(name);
			sb.append(info.getDeclaringClass().getName());
			comparaison = sb.toString();
		}
		return comparaison;
	}

	public Field getInfo() {
		return info;
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
	public Type[] getParametreType() {
		if (typeToken == null)
			return new Type[0];
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
		return valueType;
	}

	@Override
	public int hashCode() {
		if (hash == 0)
			hash = (name + info.getDeclaringClass().getName()).hashCode();
		return hash;
	}

	@Override
	public boolean isChampId() {
		return isChampId;
	}

	public boolean isFakeId() {
		return info == null;
	}

	@Override
	public boolean isSimple() {
		return isSimple;
	}

	@Override
	public boolean isTypeDevinable(final Object value) {
		final Class<?> type = TypeExtension.getClasseASerialiser(value);
		return TypeExtension.getTypeEnveloppe(valueType) == TypeExtension.getTypeEnveloppe(type);
	}

	@Override
	public void set(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId)
			throws SetValueException {
		try {
			if (obj != null)
				if (nomEstId)
					setChampId(obj, value);
				else
					ecrit(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SetValueException(
					"impossible de setter " + value.toString() + " de type " + value.getClass().getName()
							+ " dans le champ " + name + " de la classe " + info.getDeclaringClass(),
					e);
		}
	}

	/**
	 * Affecte la valeur à un objet qui vient d'être créé (champs vierges) : pas de contrôle de l'id existant.
	 */
	public void affecte(final Object obj, final Object value, final Map<Object, UUID> dicoObjToFakeId)
			throws SetValueException {
		try {
			ecrit(obj, value);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new SetValueException("impossible de setter " + value + " dans le champ " + name + " de la classe "
					+ info.getDeclaringClass(), e);
		}
	}

	private void setChampId(final Object obj, final Object value)
			throws IllegalArgumentException, IllegalAccessException {
		final Object actuel = acces().get(obj);
		if (actuel == null || "0".equals(actuel.toString()))
			ecrit(obj, value);
	}

}
