package com.kite9.k9server.domain.github;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SomeJava {

	static interface Table {

		List<Content> getColumnNames();
		
		List<List<Content>> getData();
	}

	static class Places {
		
		public Places(List<Object> in) {
			
		}
	}

	static class Place {
	}

	static interface Content {
		
		public String getText();
		
		public Optional<String> getNth(Class<?> c, int i);
	}

	public static Places upload(Table t) {

		return new Places (
			t.getData().stream()
				.map(r -> {
					Place plc = new Place();
					IntStream.range (0, t.getColumnNames ().size()).forEach(i -> {
						Content c = t.getColumnNames ().get(i);
						c.getNth(String.class, 0)
							.map(w -> camelCase (w))
							.ifPresent(w -> {
								try {
									Field f = plc.getClass().getDeclaredField(w);
									f.setAccessible(true);
									String text = r.get(i).getText().trim();
									if (text.length() > 0) {
										if (Number.class.isAssignableFrom(f.getType())) {
											f.set(plc, new BigDecimal(text));
										} else if (boolean.class.isAssignableFrom(f.getType())) {
											f.set(plc, "1".equals(text));
										} else {
											f.set(plc, text);
										}
									}
								} catch (IndexOutOfBoundsException e) {
									// do nothing with this, carry on.
								} catch (Exception e) {
									throw new UnsupportedOperationException("Unknown field: "+w, e);
								}
							});
						});
					
						return plc;
					})
				.collect(Collectors.toList()));
	}

	private static String camelCase(String s) {
		return Arrays.stream(s.toLowerCase ().split(" "))
			.map(e -> e.substring(0, 1).toUpperCase () + e.substring(1))
			.reduce(String::concat)
			.map (e -> e.substring (0, 1).toLowerCase() + e.substring(1))
			.orElse(null);
	}
}