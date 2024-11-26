/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.inference.action;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.support.ActionFilters;
import org.elasticsearch.inference.InferenceService;
import org.elasticsearch.inference.InferenceServiceRegistry;
import org.elasticsearch.inference.InferenceServiceResults;
import org.elasticsearch.inference.Model;
import org.elasticsearch.inference.UnparsedModel;
import org.elasticsearch.injection.guice.Inject;
import org.elasticsearch.transport.TransportService;
import org.elasticsearch.xpack.core.inference.action.InferenceAction;
import org.elasticsearch.xpack.inference.action.task.StreamingTaskManager;
import org.elasticsearch.xpack.inference.registry.ModelRegistry;
import org.elasticsearch.xpack.inference.telemetry.InferenceStats;

public class TransportInferenceAction extends BaseTransportInferenceAction<InferenceAction.Request> {

    @Inject
    public TransportInferenceAction(
        TransportService transportService,
        ActionFilters actionFilters,
        ModelRegistry modelRegistry,
        InferenceServiceRegistry serviceRegistry,
        InferenceStats inferenceStats,
        StreamingTaskManager streamingTaskManager
    ) {
        super(
            InferenceAction.NAME,
            transportService,
            actionFilters,
            modelRegistry,
            serviceRegistry,
            inferenceStats,
            streamingTaskManager,
            InferenceAction.Request::new
        );
    }

    @Override
    protected boolean isInvalidTaskTypeForInferenceEndpoint(InferenceAction.Request request, UnparsedModel unparsedModel) {
        return false;
    }

    @Override
    protected ElasticsearchStatusException createIncompatibleTaskTypeException(
        InferenceAction.Request request,
        UnparsedModel unparsedModel
    ) {
        return null;
    }

    @Override
    protected void doInference(
        Model model,
        InferenceAction.Request request,
        InferenceService service,
        ActionListener<InferenceServiceResults> listener
    ) {
        service.infer(
            model,
            request.getQuery(),
            request.getInput(),
            request.isStreaming(),
            request.getTaskSettings(),
            request.getInputType(),
            request.getInferenceTimeout(),
            listener
        );
    }
}
