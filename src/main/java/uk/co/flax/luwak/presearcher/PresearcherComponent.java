package uk.co.flax.luwak.presearcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import uk.co.flax.luwak.termextractor.QueryAnalyzer;
import uk.co.flax.luwak.termextractor.QueryTerm;
import uk.co.flax.luwak.termextractor.QueryTreeBuilder;
import uk.co.flax.luwak.termextractor.querytree.TreeWeightor;

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

/**
 * Class that wraps a set of Presearcher behaviours
 */
public class PresearcherComponent {

    private final List<? extends QueryTreeBuilder<?>> builders;

    /**
     * Create a new PresearcherComponent from a list of QueryTreeBuilders
     * @param builders the builders
     */
    public PresearcherComponent(List<? extends QueryTreeBuilder<?>> builders) {
        this.builders = builders;
    }

    /**
     * Create a new PresearcherComponent from a list of QueryTreeBuilders
     * @param builders the builders
     */
    public PresearcherComponent(QueryTreeBuilder<?>... builders) {
        this(Arrays.asList(builders));
    }

    /**
     * @return the QueryTreeBuilders for this component
     */
    public List<? extends QueryTreeBuilder<?>> getQueryTreeBuilders() {
        return builders;
    }

    /**
     * Filter the TokenStream used by the Presearcher to create it's document query
     * @param ts a TokenStream generated by examining the presearcher's InputDocument
     * @return a filtered TokenStream
     */
    public TokenStream filterDocumentTokens(TokenStream ts) {
        return ts;
    }

    /**
     * Build a new QueryAnalyzer using a TreeWeightor and a list of PresearcherComponents
     *
     * A list of QueryTreeBuilders is extracted from each component, and combined to use
     * on the QueryAnalyzer
     *
     * @param weightor a TreeWeightor
     * @param components a list of PresearcherComponents
     * @return a QueryAnalyzer
     */
    public static QueryAnalyzer buildQueryAnalyzer(TreeWeightor weightor, PresearcherComponent... components) {
        List<QueryTreeBuilder<?>> builders = new ArrayList<>();
        for (PresearcherComponent component : components) {
            builders.addAll(component.getQueryTreeBuilders());
        }
        builders.addAll(DefaultPresearcherComponent.DEFAULT_BUILDERS);
        return new QueryAnalyzer(weightor, builders);
    }

    /**
     * Build a new QueryAnalyzer using a list of PresearcherComponents
     *
     * A list of QueryTreeBuilders is extracted from each component, and combined to use
     * on the QueryAnalyzer with a default TreeWeightor.
     *
     * @param components a list of PresearcherComponents
     * @return a QueryAnalyzer
     */
    public static QueryAnalyzer buildQueryAnalyzer(PresearcherComponent... components) {
        return buildQueryAnalyzer(TreeWeightor.DEFAULT_WEIGHTOR, components);
    }

    /**
     * Add an extra token to the Document used to index a Query
     *
     * For example, if one of the QueryTreeBuilders injects a CUSTOM QueryTerm.Type
     * into the query tree, and the relevant term is collected, you may want to add
     * a specific token here
     *
     * @param type the type of a collected QueryTerm
     *
     * @return a token to index, or null if no extra token is required
     */
    public String extraToken(QueryTerm.Type type) {
        return null;
    }
}
