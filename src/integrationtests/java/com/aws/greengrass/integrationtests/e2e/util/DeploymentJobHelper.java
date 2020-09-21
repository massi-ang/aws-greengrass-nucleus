/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.aws.greengrass.integrationtests.e2e.util;

import com.amazonaws.services.evergreen.model.ComponentUpdatePolicy;
import com.amazonaws.services.evergreen.model.ComponentUpdatePolicyAction;
import com.aws.greengrass.deployment.model.FailureHandlingPolicy;
import com.aws.greengrass.deployment.model.FleetConfiguration;
import com.aws.greengrass.deployment.model.PackageInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.aws.greengrass.integrationtests.e2e.BaseE2ETestCase.getTestComponentNameInCloud;
import static com.aws.greengrass.integrationtests.e2e.util.IotJobsUtils.generateMockConfigurationArn;

public class DeploymentJobHelper {
    public int index;
    public String jobId;
    public CountDownLatch jobCompleted;
    public String targetPkgName;

    public DeploymentJobHelper(int index, String pkgName) {
        this.index = index;
        jobId = UUID.randomUUID().toString();
        jobCompleted = new CountDownLatch(1);
        targetPkgName = pkgName;
    }

    public String createIoTJobDocument() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(
                FleetConfiguration.builder()
                        .configurationArn(generateMockConfigurationArn(String.format("job/helper:%s", index)))
                        .creationTimestamp(System.currentTimeMillis())
                        .failureHandlingPolicy(FailureHandlingPolicy.DO_NOTHING)
                        .componentUpdatePolicy(new ComponentUpdatePolicy()
                                .withAction(ComponentUpdatePolicyAction.NOTIFY_COMPONENTS).withTimeout(60))
                        .packages(Collections.singletonMap(getTestComponentNameInCloud(targetPkgName),
                                new PackageInfo(true, "1.0.0", null)))
                        .build()
        );
    }
}