package giraudsa.marshall.strategie;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import giraudsa.marshall.annotations.TypeRelation;
import utils.champ.FieldInformations;

public class StrategieParCompositionOuAgregationEtClasseConcrete extends StrategieDeSerialisation {

	public StrategieParCompositionOuAgregationEtClasseConcrete() {
	}

	@Override
	public boolean serialiseTout(final int profondeur, final FieldInformations fieldInformation) {
		Class<?> type = fieldInformation.getValueType();
		if (type.isArray())
			type = type.getComponentType();
		else if (Collection.class.isAssignableFrom(type)) {
			type = Object.class;
			final Type[] types = fieldInformation.getParametreType();
			if (types != null && types.length > 0)
				type = (Class<?>) types[0];
		} else if (Map.class.isAssignableFrom(type))
			type = Object.class;
		final boolean isConcrete = !Modifier.isAbstract(fieldInformation.getValueType().getModifiers());
		return fieldInformation.getRelation() == TypeRelation.COMPOSITION
				|| fieldInformation.getRelation() == TypeRelation.AGGREGATION && isConcrete;
	}

}