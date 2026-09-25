package io.github.giraudsa.fidelis.strategie;

import io.github.giraudsa.fidelis.annotations.TypeRelation;
import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class StrategieParComposition extends StrategieDeSerialisation {

	public StrategieParComposition() {
	}

	@Override
	public boolean serialiseTout(final int profondeur, final FieldInformations fieldInformation) {
		return fieldInformation.getRelation() == TypeRelation.COMPOSITION;
	}

}
