/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.segment.virtual;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import org.apache.druid.common.guava.SettableSupplier;
import org.apache.druid.math.expr.ExprEval;
import org.apache.druid.query.monomorphicprocessing.RuntimeShapeInspector;
import org.apache.druid.segment.BaseSingleValueDimensionSelector;
import org.apache.druid.segment.ColumnValueSelector;
import org.apache.druid.segment.DimensionSelector;
import org.apache.druid.segment.TestObjectColumnSelector;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ExpressionColumnValueSelectorTest
{
  @Test
  public void testSupplierFromDimensionSelector()
  {
    final SettableSupplier<String> settableSupplier = new SettableSupplier<>();
    final Supplier<Object> supplier = ExpressionSelectors.supplierFromDimensionSelector(
        dimensionSelectorFromSupplier(settableSupplier),
        false
    );

    Assert.assertNotNull(supplier);
    Assert.assertEquals(null, supplier.get());

    settableSupplier.set(null);
    Assert.assertEquals(null, supplier.get());

    settableSupplier.set("1234");
    Assert.assertEquals("1234", supplier.get());
  }

  @Test
  public void testSupplierFromObjectSelectorObject()
  {
    final SettableSupplier<Object> settableSupplier = new SettableSupplier<>();
    final Supplier<Object> supplier = ExpressionSelectors.supplierFromObjectSelector(
        objectSelectorFromSupplier(settableSupplier, Object.class)
    );

    Assert.assertNotNull(supplier);
    Assert.assertEquals(null, supplier.get());

    settableSupplier.set(1.1f);
    Assert.assertEquals(1.1f, supplier.get());

    settableSupplier.set(1L);
    Assert.assertEquals(1L, supplier.get());

    settableSupplier.set("1234");
    Assert.assertEquals("1234", supplier.get());

    settableSupplier.set("1.234");
    Assert.assertEquals("1.234", supplier.get());
  }

  @Test
  public void testSupplierFromObjectSelectorNumber()
  {
    final SettableSupplier<Number> settableSupplier = new SettableSupplier<>();
    final Supplier<Object> supplier = ExpressionSelectors.supplierFromObjectSelector(
        objectSelectorFromSupplier(settableSupplier, Number.class)
    );


    Assert.assertNotNull(supplier);
    Assert.assertEquals(null, supplier.get());

    settableSupplier.set(1.1f);
    Assert.assertEquals(1.1f, supplier.get());

    settableSupplier.set(1L);
    Assert.assertEquals(1L, supplier.get());
  }

  @Test
  public void testSupplierFromObjectSelectorString()
  {
    final SettableSupplier<String> settableSupplier = new SettableSupplier<>();
    final Supplier<Object> supplier = ExpressionSelectors.supplierFromObjectSelector(
        objectSelectorFromSupplier(settableSupplier, String.class)
    );

    Assert.assertNotNull(supplier);
    Assert.assertEquals(null, supplier.get());

    settableSupplier.set("1.1");
    Assert.assertEquals("1.1", supplier.get());

    settableSupplier.set("1");
    Assert.assertEquals("1", supplier.get());
  }

  @Test
  public void testSupplierFromObjectSelectorList()
  {
    final SettableSupplier<List> settableSupplier = new SettableSupplier<>();
    final Supplier<Object> supplier = ExpressionSelectors.supplierFromObjectSelector(
        objectSelectorFromSupplier(settableSupplier, List.class)
    );

    Assert.assertNotNull(supplier);
    Assert.assertEquals(null, supplier.get());

    settableSupplier.set(ImmutableList.of("1", "2", "3"));
    Assert.assertArrayEquals(new String[]{"1", "2", "3"}, (Object[]) supplier.get());

  }

  @Test
  public void testCoerceListToArray()
  {
    List<Long> longList = ImmutableList.of(1L, 2L, 3L);
    Assert.assertArrayEquals(new Long[]{1L, 2L, 3L}, (Long[]) ExpressionSelectors.coerceListToArray(longList));

    List<Integer> intList = ImmutableList.of(1, 2, 3);
    Assert.assertArrayEquals(new Long[]{1L, 2L, 3L}, (Long[]) ExpressionSelectors.coerceListToArray(intList));

    List<Float> floatList = ImmutableList.of(1.0f, 2.0f, 3.0f);
    Assert.assertArrayEquals(new Double[]{1.0, 2.0, 3.0}, (Double[]) ExpressionSelectors.coerceListToArray(floatList));

    List<Double> doubleList = ImmutableList.of(1.0, 2.0, 3.0);
    Assert.assertArrayEquals(new Double[]{1.0, 2.0, 3.0}, (Double[]) ExpressionSelectors.coerceListToArray(doubleList));

    List<String> stringList = ImmutableList.of("a", "b", "c");
    Assert.assertArrayEquals(new String[]{"a", "b", "c"}, (String[]) ExpressionSelectors.coerceListToArray(stringList));

    List<String> withNulls = new ArrayList<>();
    withNulls.add("a");
    withNulls.add(null);
    withNulls.add("c");
    Assert.assertArrayEquals(new String[]{"a", null, "c"}, (String[]) ExpressionSelectors.coerceListToArray(withNulls));

    List<Long> withNumberNulls = new ArrayList<>();
    withNumberNulls.add(1L);
    withNumberNulls.add(null);
    withNumberNulls.add(3L);

    Assert.assertArrayEquals(new Long[]{1L, null, 3L}, (Long[]) ExpressionSelectors.coerceListToArray(withNumberNulls));

    List<Object> withStringMix = ImmutableList.of(1L, "b", 3L);
    Assert.assertArrayEquals(
        new String[]{"1", "b", "3"},
        (String[]) ExpressionSelectors.coerceListToArray(withStringMix)
    );

    List<Number> withIntsAndLongs = ImmutableList.of(1, 2L, 3);
    Assert.assertArrayEquals(
        new Long[]{1L, 2L, 3L},
        (Long[]) ExpressionSelectors.coerceListToArray(withIntsAndLongs)
    );

    List<Number> withFloatsAndLongs = ImmutableList.of(1, 2L, 3.0f);
    Assert.assertArrayEquals(
        new Double[]{1.0, 2.0, 3.0},
        (Double[]) ExpressionSelectors.coerceListToArray(withFloatsAndLongs)
    );

    List<Number> withDoublesAndLongs = ImmutableList.of(1, 2L, 3.0);
    Assert.assertArrayEquals(
        new Double[]{1.0, 2.0, 3.0},
        (Double[]) ExpressionSelectors.coerceListToArray(withDoublesAndLongs)
    );

    List<Number> withFloatsAndDoubles = ImmutableList.of(1L, 2.0f, 3.0);
    Assert.assertArrayEquals(
        new Double[]{1.0, 2.0, 3.0},
        (Double[]) ExpressionSelectors.coerceListToArray(withFloatsAndDoubles)
    );

    List<String> withAllNulls = new ArrayList<>();
    withAllNulls.add(null);
    withAllNulls.add(null);
    withAllNulls.add(null);
    Assert.assertArrayEquals(
        new String[]{null, null, null},
        (String[]) ExpressionSelectors.coerceListToArray(withAllNulls)
    );
  }

  @Test
  public void testCoerceExprToValue()
  {
    Assert.assertEquals(
        ImmutableList.of(1L, 2L, 3L),
        ExpressionSelectors.coerceEvalToSelectorObject(ExprEval.ofLongArray(new Long[]{1L, 2L, 3L}))
    );

    Assert.assertEquals(
        ImmutableList.of(1.0, 2.0, 3.0),
        ExpressionSelectors.coerceEvalToSelectorObject(ExprEval.ofDoubleArray(new Double[]{1.0, 2.0, 3.0}))
    );

    Assert.assertEquals(
        ImmutableList.of("a", "b", "c"),
        ExpressionSelectors.coerceEvalToSelectorObject(ExprEval.ofStringArray(new String[]{"a", "b", "c"}))
    );

    List<String> withNulls = new ArrayList<>();
    withNulls.add("a");
    withNulls.add(null);
    withNulls.add("c");
    Assert.assertEquals(
        withNulls,
        ExpressionSelectors.coerceEvalToSelectorObject(ExprEval.ofStringArray(new String[]{"a", null, "c"}))
    );
  }

  private static DimensionSelector dimensionSelectorFromSupplier(
      final Supplier<String> supplier
  )
  {
    return new BaseSingleValueDimensionSelector()
    {
      @Override
      protected String getValue()
      {
        return supplier.get();
      }

      @Override
      public void inspectRuntimeShape(final RuntimeShapeInspector inspector)
      {
        inspector.visit("supplier", supplier);
      }
    };
  }

  private static <T> ColumnValueSelector<T> objectSelectorFromSupplier(
      final Supplier<T> supplier,
      final Class<T> clazz
  )
  {
    return new TestObjectColumnSelector<T>()
    {
      @Override
      public Class<T> classOfObject()
      {
        return clazz;
      }

      @Override
      public T getObject()
      {
        return supplier.get();
      }
    };
  }
}
