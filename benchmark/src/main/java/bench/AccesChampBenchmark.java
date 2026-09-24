package bench;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;

/** Coût d'une écriture de champ par réflexion, selon la technique (références non constantes, comme dans la lib). */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class AccesChampBenchmark {
	public static class Cible {
		private String a;
		private String b;
		private double d;
		private int i;
	}

	interface Setter {
		void set(Object o, Object v);
	}

	Field[] champs;
	VarHandle[] vhs;
	MethodHandle[] mhs;
	Object[] valeurs = { "x", "y", 1.5, 3 };
	Cible cible = new Cible();
	int k;

	@Setup
	public void setup() throws Exception {
		final String[] noms = { "a", "b", "d", "i" };
		champs = new Field[4];
		vhs = new VarHandle[4];
		mhs = new MethodHandle[4];
		final MethodHandles.Lookup l = MethodHandles.privateLookupIn(Cible.class, MethodHandles.lookup());
		for (int j = 0; j < 4; j++) {
			champs[j] = Cible.class.getDeclaredField(noms[j]);
			champs[j].setAccessible(true);
			vhs[j] = l.unreflectVarHandle(champs[j]);
			mhs[j] = l.unreflectSetter(champs[j]).asType(MethodType.methodType(void.class, Object.class, Object.class));
		}
	}

	@Benchmark
	public Object field() throws Exception {
		for (int j = 0; j < 4; j++)
			champs[j].set(cible, valeurs[j]);
		return cible;
	}

	@Benchmark
	public Object methodHandle() throws Throwable {
		for (int j = 0; j < 4; j++)
			mhs[j].invokeExact((Object) cible, valeurs[j]);
		return cible;
	}

	@Benchmark
	public Object varHandle() {
		for (int j = 0; j < 4; j++)
			vhs[j].set((Object) cible, valeurs[j]);
		return cible;
	}

	@Benchmark
	public Object direct() {
		cible.a = (String) valeurs[0];
		cible.b = (String) valeurs[1];
		cible.d = (Double) valeurs[2];
		cible.i = (Integer) valeurs[3];
		return cible;
	}
}
