package io.github.giraudsa.fidelis.deserialisation.text.json.actions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.giraudsa.fidelis.deserialisation.ActionAbstrait;
import io.github.giraudsa.fidelis.deserialisation.Unmarshaller;
import io.github.giraudsa.fidelis.deserialisation.text.json.JsonUnmarshaller;
import io.github.giraudsa.fidelis.utils.io.DatesIso;

public class ActionJsonDate<T extends Date> extends ActionJsonSimpleComportement<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ActionJsonDate.class);

	@SuppressWarnings("unchecked")
	public static ActionAbstrait<Date> getInstance() {
		return new ActionJsonDate<>(Date.class, null);
	}

	private ActionJsonDate(final Class<T> type, final JsonUnmarshaller<?> jsonUnmarshaller) {
		super(type, jsonUnmarshaller);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <U extends T> ActionAbstrait<U> getNewInstance(final Class<U> type, final Unmarshaller unmarshaller) {
		return new ActionJsonDate<>(type, (JsonUnmarshaller<?>) unmarshaller);
	}

	@Override
	protected Class<?> getTypeAttribute(final String nomAttribut) {
		return Date.class;
	}

	@Override
	protected <W> void integreObjet(final String nomAttribut, final W objet) {
		obj = objet;
	}

	/** constructeur (long) des sous-classes de Date, recherché une fois par classe. */
	private static final ClassValue<Constructor<?>> CONSTRUCTEURS = new ClassValue<>() {
		@Override
		protected Constructor<?> computeValue(final Class<?> t) {
			try {
				return t.getConstructor(long.class);
			} catch (final NoSuchMethodException | SecurityException e) {
				return null;
			}
		}
	};

	@Override
	protected void rempliData(final String donnees) {
		Date date;
		long time = 0;
		try {
			final long rapide = isDateIsoUtc() ? DatesIso.lit(donnees) : DatesIso.INVALIDE;
			if (rapide != DatesIso.INVALIDE)
				time = rapide;
			else {
				date = getDateFormat().parse(donnees);
				time = date.getTime();
			}
			if (type == Date.class) {
				obj = new Date(time);
				return;
			}
			final Constructor<?> constructeur = CONSTRUCTEURS.get(type);
			if (constructeur == null)
				throw new NoSuchMethodException(type.getName() + ".<init>(long)");
			obj = constructeur.newInstance(time);
		} catch (ParseException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			LOGGER.error("pas de constructeur avec un long pour le type date " + type.getName(), e);
			obj = new Date(time);
		}
	}
}
