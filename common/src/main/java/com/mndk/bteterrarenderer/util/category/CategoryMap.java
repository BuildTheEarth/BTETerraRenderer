package com.mndk.bteterrarenderer.util.category;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mndk.bteterrarenderer.util.function.ThrowableBiConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@JsonSerialize(using = CategoryMapSerializer.class)
@JsonDeserialize(using = CategoryMapDeserializer.class)
public class CategoryMap<T> {

	@Getter
	private final Map<String, Category<T>> map = new LinkedHashMap<>();

	public void forEach(BiConsumer<String, Category<T>> consumer) {
		map.forEach(consumer);
	}

	public <E extends Throwable> void forEachThrowable(ThrowableBiConsumer<String, Category<T>, E> consumer) throws E {
		for (Map.Entry<String, Category<T>> entry : map.entrySet()) {
			consumer.accept(entry.getKey(), entry.getValue());
		}
	}

	public interface PathConsumer<T> {
		void accept(String[] categoryPath, String id, T item);
	}

	public void forEach(PathConsumer<T> consumer) {
		map.forEach((name, category) -> category.forEach(new String[]{name}, consumer));
	}

	@Nullable
	public T getItem(String categoryName, String elementId) {
		return getItem(new String[]{categoryName}, elementId);
	}

	@Nullable
	public T getItem(String[] categoryPath, String elementId) {
		if (categoryPath == null || categoryPath.length == 0) return null;
		Category<T> current = map.get(categoryPath[0]);
		for (int i = 1; i < categoryPath.length; i++) {
			if (current == null) return null;
			current = current.getSubcategories().get(categoryPath[i]);
		}
		if (current == null) return null;
		return current.get(elementId);
	}

	public void setItem(String categoryName, String elementId, @Nonnull T item) {
		setItem(new String[]{categoryName}, elementId, item);
	}

	public void setItem(String[] categoryPath, String elementId, @Nonnull T item) {
		if (categoryPath == null || categoryPath.length == 0) return;
		Category<T> current = map.computeIfAbsent(categoryPath[0], n -> new Category<>());
		for (int i = 1; i < categoryPath.length; i++) {
			current = current.getSubcategories().computeIfAbsent(categoryPath[i], n -> new Category<>());
		}
		current.put(elementId, item);
	}

	@Getter
	@RequiredArgsConstructor
	public static class Entry<T> {
		private final String categoryName;
		private final T value;
	}

}
