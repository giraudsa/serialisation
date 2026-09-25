package io.github.giraudsa.fidelis.strategie;

import io.github.giraudsa.fidelis.utils.champ.FieldInformations;

public class StrategieSerialisationComplete extends StrategieDeSerialisation {

	public StrategieSerialisationComplete() {

	}

	@Override
	public boolean serialiseTout(final int profondeur, final FieldInformations fieldInformation) {
		return true;
	}

}
