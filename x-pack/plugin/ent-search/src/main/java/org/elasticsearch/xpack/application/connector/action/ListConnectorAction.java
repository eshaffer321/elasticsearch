/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.application.connector.action;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionRequestValidationException;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ActionType;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.xcontent.ConstructingObjectParser;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.xpack.application.connector.Connector;
import org.elasticsearch.xpack.core.action.util.PageParams;
import org.elasticsearch.xpack.core.action.util.QueryPage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.xcontent.ConstructingObjectParser.constructorArg;

public class ListConnectorAction extends ActionType<ListConnectorAction.Response> {

    public static final ListConnectorAction INSTANCE = new ListConnectorAction();
    public static final String NAME = "cluster:admin/xpack/connector/list";

    public ListConnectorAction() {
        super(NAME);
    }

    public static class Request extends ActionRequest implements ToXContentObject {

        private final PageParams pageParams;

        private static final ParseField PAGE_PARAMS_FIELD = new ParseField("pageParams");

        public Request(StreamInput in) throws IOException {
            super(in);
            this.pageParams = new PageParams(in);
        }

        public Request(PageParams pageParams) {
            this.pageParams = pageParams;
        }

        public PageParams getPageParams() {
            return pageParams;
        }

        @Override
        public ActionRequestValidationException validate() {
            // Pagination validation is done as part of PageParams constructor
            return null;
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            super.writeTo(out);
            pageParams.writeTo(out);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ListConnectorAction.Request that = (ListConnectorAction.Request) o;
            return Objects.equals(pageParams, that.pageParams);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pageParams);
        }

        private static final ConstructingObjectParser<ListConnectorAction.Request, String> PARSER = new ConstructingObjectParser<>(
            "list_connector_request",
            p -> new ListConnectorAction.Request((PageParams) p[0])
        );

        static {
            PARSER.declareObject(constructorArg(), (p, c) -> PageParams.fromXContent(p), PAGE_PARAMS_FIELD);
        }

        public static ListConnectorAction.Request parse(XContentParser parser) {
            return PARSER.apply(parser, null);
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            builder.startObject();
            builder.field(PAGE_PARAMS_FIELD.getPreferredName(), pageParams);
            builder.endObject();
            return builder;
        }
    }

    public static class Response extends ActionResponse implements ToXContentObject {

        public static final ParseField RESULT_FIELD = new ParseField("results");

        final QueryPage<Connector> queryPage;

        public Response(StreamInput in) throws IOException {
            super(in);
            this.queryPage = new QueryPage<>(in, Connector::new);
        }

        public Response(List<Connector> items, Long totalResults) {
            this.queryPage = new QueryPage<>(items, totalResults, RESULT_FIELD);
        }

        @Override
        public void writeTo(StreamOutput out) throws IOException {
            queryPage.writeTo(out);
        }

        @Override
        public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
            return queryPage.toXContent(builder, params);
        }

        public QueryPage<Connector> queryPage() {
            return queryPage;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ListConnectorAction.Response that = (ListConnectorAction.Response) o;
            return queryPage.equals(that.queryPage);
        }

        @Override
        public int hashCode() {
            return queryPage.hashCode();
        }
    }

}
