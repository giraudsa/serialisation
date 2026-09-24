package giraudsa.marshall.deserialisation.text.json.actions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Function;

import giraudsa.marshall.deserialisation.ActionAbstrait;
import giraudsa.marshall.deserialisation.Unmarshaller;
import giraudsa.marshall.deserialisation.text.json.ActionJson;
import giraudsa.marshall.deserialisation.text.json.JsonUnmarshaller;
import giraudsa.marshall.exception.InstanciationException;
import utils.Constants;

public class ActionJsonSimpleComportement<T> extends ActionJson<T> {

	@SuppressWarnings("unchecked")
	public static <U> ActionAbstrait<U> getInstance() {
		return (ActionAbstrait<U>) new ActionJsonSimpleComportement<>(Object.class, null);
	}

	protected ActionJsonSimpleComportement(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@Override
	protected void construitObjet() {
		// rien a faire
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonSimpleComportement<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		if (Constants.VALEUR.equals(nomAttribut))
			return type;
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		// valeur enveloppée {"__type":..., "__valeur":...} quand le type n'est pas devinable
		if (Constants.VALEUR.equals(nomAttribut))
			obj = (T) objet;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void rempliData(final String donnees) throws InstanciationException {
		obj = (T) construit(type, donnees);
	}

	/** @return true si la classe est lue par cette action même (et non par une action dérivée : date, enum...). */
	public static boolean estActionDe(final ActionAbstrait<?> prototype) {
		return prototype != null && prototype.getClass() == ActionJsonSimpleComportement.class;
	}

	/** Construit la valeur de type donné à partir de sa chaîne. */
	public static Object construit(final Class<?> type, final String donnees) throws InstanciationException {
		if (type == Character.class) { // pas de constructeur Character(String)
			if (donnees.length() != 1)
				throw new InstanciationException("un caractère est attendu au lieu de \"" + donnees + "\"");
			return Character.valueOf(donnees.charAt(0));
		}
		final Function<String, Object> constructeur = CONSTRUCTEURS.get(type);
		try {
			if (constructeur == null)
				throw new NoSuchMethodException(type.getName() + ".<init>(String)");
			return constructeur.apply(donnees);
		} catch (NoSuchMethodException | RuntimeException e) {
			final Throwable cause = e instanceof ConstructionImpossible ? e.getCause() : e;
			throw new InstanciationException(
					"impossible de trouver un constructeur avec un string pour le type " + type.getName(),
					cause instanceof Exception ? (Exception) cause : e);
		}
	}

	/** Erreur du constructeur réflexif, relayée à rempliData. */
	private static final class ConstructionImpossible extends RuntimeException {
		private static final long serialVersionUID = 1L;

		private ConstructionImpossible(final Exception cause) {
			super(cause);
		}
	}

	/**
	 * Construction d'une valeur à partir de sa chaîne, résolue une fois par classe (la recherche réflexive du
	 * constructeur était refaite à chaque valeur). Les types de base passent par valueOf, de même sémantique que leur
	 * constructeur (String) ; les autres par leur constructeur (String), gardé. null si aucun.
	 */
	private static final ClassValue<Function<String, Object>> CONSTRUCTEURS = new ClassValue<>() {
		@Override
		protected Function<String, Object> computeValue(final Class<?> t) {
			if (t == Integer.class)
				return Integer::valueOf;
			if (t == Long.class)
				return Long::valueOf;
			if (t == Double.class)
				return Double::valueOf;
			if (t == Boolean.class)
				return Boolean::valueOf;
			if (t == Short.class)
				return Short::valueOf;
			if (t == Byte.class)
				return Byte::valueOf;
			if (t == Float.class)
				return Float::valueOf;
			if (t == String.class)
				return s -> s;
			final Constructor<?> constructeur;
			try {
				constructeur = t.getConstructor(String.class);
			} catch (final NoSuchMethodException | SecurityException e) {
				return null;
			}
			return s -> {
				try {
					return constructeur.newInstance(s);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new ConstructionImpossible(e);
				}
			};
		}
	};

}
