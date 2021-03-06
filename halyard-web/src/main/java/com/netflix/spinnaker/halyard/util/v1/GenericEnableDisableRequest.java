/*
 * Copyright 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.halyard.util.v1;

import com.netflix.spinnaker.halyard.config.config.v1.HalconfigParser;
import com.netflix.spinnaker.halyard.config.model.v1.node.Halconfig;
import com.netflix.spinnaker.halyard.config.model.v1.node.Node;
import com.netflix.spinnaker.halyard.core.DaemonResponse;
import com.netflix.spinnaker.halyard.core.problem.v1.ProblemSet;
import com.netflix.spinnaker.halyard.core.tasks.v1.DaemonTask;
import com.netflix.spinnaker.halyard.core.tasks.v1.DaemonTaskHandler;
import com.netflix.spinnaker.halyard.models.v1.ValidationSettings;
import lombok.Builder;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Builder(builderMethodName = "privateBuilder")
public class GenericEnableDisableRequest {
  private final HalconfigParser halconfigParser;

  private final Consumer<Boolean> updater;
  private final Supplier<ProblemSet> validator;
  private final String description;

  public DaemonTask<Halconfig, Void> execute(ValidationSettings validationSettings, boolean updatedValue) {
    DaemonResponse.UpdateRequestBuilder builder = RequestUtils.getUpdateRequestBuilder(halconfigParser);
    RequestUtils.addValidation(builder, validationSettings, validator);
    builder.setUpdate(() -> updater.accept(updatedValue));
    return DaemonTaskHandler.submitTask(builder::build, description);
  }

  private static <T extends Node> GenericEnableDisableRequestBuilder privateBuilder() {
    return new GenericEnableDisableRequestBuilder();
  }

  public static <T extends Node> GenericEnableDisableRequestBuilder builder(HalconfigParser halconfigParser) {
    return GenericEnableDisableRequest.<T>privateBuilder().halconfigParser(halconfigParser);
  }
}
