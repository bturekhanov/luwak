package uk.co.flax.luwak.termextractor;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;
import uk.co.flax.luwak.termextractor.weights.*;

import java.util.List;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Copyright (c) 2014 Lemur Consulting Ltd.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class TestQueryTermComparators {

    private static TermWeightor WEIGHT = CompoundRuleWeightor.newWeightor().build();

    @Test
    public void testAnyTokensAreNotPreferred() {

        QueryTermList list1 = new QueryTermList(WEIGHT, new QueryTerm("f", "foo", QueryTerm.Type.EXACT));
        QueryTermList list2 = new QueryTermList(WEIGHT, new QueryTerm("f", "foo", QueryTerm.Type.WILDCARD));

        assertThat(QueryTermList.selectBest(Lists.newArrayList(list1, list2)))
                .containsExactly(new QueryTerm("f", "foo", QueryTerm.Type.EXACT));

    }

    @Test
    public void testLongerTokensArePreferred() {

        QueryTermList list1 = new QueryTermList(WEIGHT, new QueryTerm("f", "foo", QueryTerm.Type.EXACT));
        QueryTermList list2 = new QueryTermList(WEIGHT, new QueryTerm("f", "foobar", QueryTerm.Type.EXACT));

        assertThat(QueryTermList.selectBest(Lists.newArrayList(list1, list2)))
                .containsExactly(new QueryTerm("f", "foobar", QueryTerm.Type.EXACT));

    }

    @Test
    public void testTermListLengthNorms() {

        List<QueryTerm> list1 = Lists.newArrayList(new QueryTerm("f", "t", QueryTerm.Type.EXACT),
                                                   new QueryTerm("f", "t", QueryTerm.Type.EXACT));
        List<QueryTerm> list2 = Lists.newArrayList(new QueryTerm("f", "t", QueryTerm.Type.EXACT));

        WeightRule rule = new LengthNorm(3, 0.3f);
        assertThat(rule.weigh(list2)).isGreaterThan(rule.weigh(list1));

    }

    @Test
    public void testShorterTermListsArePreferred() {

        QueryTermList list1 = new QueryTermList(WEIGHT, new QueryTerm("f", "foobar", QueryTerm.Type.EXACT));
        QueryTermList list2 = new QueryTermList(WEIGHT, new QueryTerm("f", "foobar", QueryTerm.Type.EXACT),
                new QueryTerm("f", "foobar", QueryTerm.Type.EXACT));

        assertThat(QueryTermList.selectBest(Lists.newArrayList(list1, list2)))
                .containsExactly(new QueryTerm("f", "foobar", QueryTerm.Type.EXACT));
    }

    @Test
    public void testUndesireableFieldsAreNotPreferred() {

        TermWeightor weight = CompoundRuleWeightor.newWeightor()
                .withRule(new FieldWeightRule(Sets.newSet("g"), 0.7f))
                .build();

        QueryTermList list1 = new QueryTermList(weight, new QueryTerm("f", "foo", QueryTerm.Type.WILDCARD));
        QueryTermList list2 = new QueryTermList(weight, new QueryTerm("g", "bar", QueryTerm.Type.EXACT));

        assertThat(QueryTermList.selectBest(Lists.newArrayList(list1, list2)))
                .containsExactly(new QueryTerm("f", "foo", QueryTerm.Type.WILDCARD));

    }

    @Test
    public void testUndesireableFieldsAreStillSelectedIfNecessary() {

        TermWeightor weight = CompoundRuleWeightor.newWeightor()
                .withRule(new FieldWeightRule(Sets.newSet("f"), 0.7f)).build();

        QueryTermList list = new QueryTermList(weight, new QueryTerm("f", "foo", QueryTerm.Type.EXACT));
        assertThat(QueryTermList.selectBest(Lists.newArrayList(list, list)))
                .containsExactly(new QueryTerm("f", "foo", QueryTerm.Type.EXACT));

    }

    @Test
    public void testUndesirableTokensAreNotPreferred() {

        Map<String, Float> termweights = ImmutableMap.of("START", 0.01f);
        TermWeightor weight = CompoundRuleWeightor.newWeightor()
                .withRule(new TermWeightRule(termweights)).build();

        QueryTermList list1 = new QueryTermList(weight, new QueryTerm("f", "START", QueryTerm.Type.EXACT));
        QueryTermList list2 = new QueryTermList(weight, new QueryTerm("f", "a", QueryTerm.Type.EXACT));

        assertThat(QueryTermList.selectBest(Lists.newArrayList(list1, list2)))
                .containsExactly(new QueryTerm("f", "a", QueryTerm.Type.EXACT));
    }

}
