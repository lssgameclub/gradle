/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.dependencylock;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.api.artifacts.component.ComponentSelector;
import org.gradle.api.artifacts.component.ModuleComponentSelector;
import org.gradle.api.artifacts.result.DependencyResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.internal.artifacts.DefaultModuleIdentifier;
import org.gradle.internal.dependencylock.model.DependencyLock;
import org.gradle.internal.dependencylock.model.DependencyVersion;

public class DefaultDependencyLockCreator implements DependencyLockCreator {

    @Override
    public DependencyLock create(Project project) {
        final DependencyLock dependencyLock = new DependencyLock();

        project.getConfigurations().all(new Action<Configuration>() {
            @Override
            public void execute(final Configuration configuration) {
                if (configuration.isCanBeResolved()) {
                    configuration.getIncoming().afterResolve(new Action<ResolvableDependencies>() {
                        @Override
                        public void execute(ResolvableDependencies resolvableDependencies) {
                            resolvableDependencies.getResolutionResult().allDependencies(new Action<DependencyResult>() {
                                @Override
                                public void execute(DependencyResult dependencyResult) {
                                    if (dependencyResult instanceof ResolvedDependencyResult) {
                                        ResolvedDependencyResult resolvedDependencyResult = (ResolvedDependencyResult)dependencyResult;
                                        ComponentSelector requested = dependencyResult.getRequested();

                                        if (requested instanceof ModuleComponentSelector) {
                                            ModuleComponentSelector requestedModule = (ModuleComponentSelector)requested;
                                            ModuleIdentifier moduleIdentifier = DefaultModuleIdentifier.newId(requestedModule.getGroup(), requestedModule.getModule());
                                            String resolvedVersion = resolvedDependencyResult.getSelected().getModuleVersion().getVersion();
                                            DependencyVersion dependencyVersion = new DependencyVersion(requestedModule.getVersion(), resolvedVersion);
                                            dependencyLock.addDependency(configuration.getName(), moduleIdentifier, dependencyVersion);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        return dependencyLock;
    }
}
