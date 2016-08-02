package giraudsa.marshall.strategie;

import giraudsa.marshall.annotations.TypeRelation;
import utils.champ.FieldInformations;

public class StrategieParComposition extends StrategieDeSerialisation {

	public StrategieParComposition() {
	}

	@Override
	public boolean serialiseTout(int profondeur, FieldInformations fieldInformation) {
		return fieldInformation.getRelation() == TypeRelation.COMPOSITION;
	}

}
