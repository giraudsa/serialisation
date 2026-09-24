package utils.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

class DatesIsoTest {

	private static SimpleDateFormat reference() {
		final SimpleDateFormat f = new SimpleDateFormat(DatesIso.MOTIF);
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		return f;
	}

	private static void verifie(final SimpleDateFormat f, final long t) {
		final char[] b = new char[DatesIso.LONGUEUR];
		DatesIso.ecris(t, b, 0);
		assertEquals(f.format(new Date(t)), new String(b), "t=" + t);
	}

	@Test
	void identiqueASimpleDateFormat() {
		final SimpleDateFormat f = reference();
		assertTrue(DatesIso.estMotifIsoUtc(f));
		assertEquals("1583-01-01T00:00:00.000Z", f.format(new Date(DatesIso.MIN)));
		assertEquals("9999-12-31T23:59:59.999Z", f.format(new Date(DatesIso.MAX)));
		for (final long t : new long[] { DatesIso.MIN, DatesIso.MAX, 0, -1, 1, 951_782_400_000L /* 2000-02-29 */,
				-2_208_988_800_000L /* 1900-01-01 */, 4_107_542_399_999L /* 2100-02-28 fin */ })
			verifie(f, t);
		final Random r = new Random(7);
		for (int i = 0; i < 200_000; i++)
			verifie(f, DatesIso.MIN + (long) (r.nextDouble() * (DatesIso.MAX - DatesIso.MIN)));
	}

	@Test
	void autresFormatsExclus() {
		final SimpleDateFormat autre = new SimpleDateFormat("yyyy-MM-dd");
		autre.setTimeZone(TimeZone.getTimeZone("UTC"));
		assertTrue(!DatesIso.estMotifIsoUtc(autre));
		final SimpleDateFormat paris = new SimpleDateFormat(DatesIso.MOTIF);
		paris.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		assertTrue(!DatesIso.estMotifIsoUtc(paris));
	}
}
