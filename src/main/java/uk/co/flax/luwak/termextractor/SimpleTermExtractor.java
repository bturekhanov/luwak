package uk.co.flax.luwak.termextractor;

import org.apache.lucene.search.TermQuery;

import java.util.List;

/**
 * Copyright (c) 2013 Lemur Consulting Ltd.
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
 * An Extractor for TermQueries
 */
public class SimpleTermExtractor extends Extractor<TermQuery> {

    public SimpleTermExtractor() {
        super(TermQuery.class);
    }

    @Override
    public void extract(TermQuery query, List<QueryTerm> terms,
                        List<Extractor<?>> extractors) {
        terms.add(new QueryTerm(query.getTerm().field(), query.getTerm().text(), QueryTerm.Type.EXACT));
    }
}
