package com.mndk.bteterrarenderer.util.merge;

import com.mndk.bteterrarenderer.util.category.CategoryMap;

/**
 * MergeStrategy for CategoryMap that appends entries.
 */
public class CategoryMapMergeStrategy<V> implements MergeStrategy<CategoryMap<V>> {
    @Override
    public void merge(CategoryMap<V> original, CategoryMap<V> addition) {
        addition.forEach((categoryPath, id, value) -> original.setItem(categoryPath, id, value));
    }
}
