package bench;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import bench.model.Catalogue;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 4, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(value = 1, jvmArgs = { "-Xms2g", "-Xmx2g", "--add-opens=java.base/java.util=ALL-UNNAMED",
		"--add-opens=java.base/java.lang=ALL-UNNAMED", "--add-opens=java.base/java.math=ALL-UNNAMED",
		"--add-opens=java.base/java.text=ALL-UNNAMED" })
public class SerialisationBenchmark {

	@Param({ "giraudsa-json", "giraudsa-json-donnees", "giraudsa-xml", "giraudsa-binaire", "jackson-json", "gson", "fastjson2", "fastjson2-ref", "fory-json", "jackson-xml", "xstream",
			"kryo", "fory", "java-natif" })
	public String framework;

	/** petit = 1 commande × 10 lignes (~13 objets) ; gros = 1000 commandes × 10 lignes (~13 000 objets). */
	@Param({ "petit", "gros" })
	public String taille;

	private Codec codec;
	private Catalogue catalogue;
	private Object encode;

	@Setup(Level.Trial)
	public void setup() throws Exception {
		codec = Codec.cree(framework);
		catalogue = "petit".equals(taille) ? Catalogue.genere(1, 10) : Catalogue.genere(1000, 10);
		encode = codec.encode(catalogue);
	}

	@Benchmark
	public Object serialisation() throws Exception {
		return codec.encode(catalogue);
	}

	@Benchmark
	public Catalogue deserialisation() throws Exception {
		return codec.decode(encode);
	}
}
