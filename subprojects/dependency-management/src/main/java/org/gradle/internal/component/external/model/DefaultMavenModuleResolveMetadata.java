/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.internal.component.external.model;

import org.gradle.api.Nullable;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.internal.artifacts.DefaultModuleVersionIdentifier;
import org.gradle.internal.component.external.descriptor.ModuleDescriptorState;
import org.gradle.internal.component.model.ModuleSource;

import java.util.Arrays;
import java.util.Collection;

public class DefaultMavenModuleResolveMetadata extends AbstractModuleComponentResolveMetadata implements MavenModuleResolveMetadata, MutableMavenModuleResolveMetadata {
    public static final String POM_PACKAGING = "pom";
    public static final Collection<String> JAR_PACKAGINGS = Arrays.asList("jar", "ejb", "bundle", "maven-plugin", "eclipse-plugin");
    private final String packaging;
    private final boolean relocated;
    private String snapshotTimestamp;

    public DefaultMavenModuleResolveMetadata(ModuleComponentIdentifier componentId, ModuleDescriptorState descriptor, String packaging, boolean relocated) {
        this(componentId, DefaultModuleVersionIdentifier.newId(componentId), descriptor, packaging, relocated);
    }

    DefaultMavenModuleResolveMetadata(MutableMavenModuleResolveMetadata metadata) {
        this(metadata.getComponentId(), metadata.getDescriptor(), metadata.getPackaging(), metadata.isRelocated());
        setStatus(metadata.getStatus());
        setChanging(metadata.isChanging());
        setStatusScheme(metadata.getStatusScheme());
        setSource(metadata.getSource());
        setSnapshotTimestamp(metadata.getSnapshotTimestamp());
    }

    private DefaultMavenModuleResolveMetadata(ModuleComponentIdentifier componentId, ModuleVersionIdentifier id, ModuleDescriptorState moduleDescriptor, String packaging, boolean relocated) {
        super(componentId, id, moduleDescriptor);
        this.packaging = packaging;
        this.relocated = relocated;
    }

    @Override
    public DefaultMavenModuleResolveMetadata withSource(ModuleSource source) {
        return (DefaultMavenModuleResolveMetadata) super.withSource(source);
    }

    @Override
    public MutableMavenModuleResolveMetadata asMutable() {
        return copy();
    }

    @Override
    public MavenModuleResolveMetadata asImmutable() {
        return copy();
    }

    @Override
    protected DefaultMavenModuleResolveMetadata copy() {
        // TODO:ADAM - need to make a copy of the descriptor (it's effectively immutable at this point so it's not a problem yet)
        DefaultMavenModuleResolveMetadata copy = new DefaultMavenModuleResolveMetadata(getComponentId(), getId(), getDescriptor(), packaging, relocated);
        copyTo(copy);
        copy.snapshotTimestamp = snapshotTimestamp;
        return copy;
    }

    public String getPackaging() {
        return packaging;
    }

    public boolean isRelocated() {
        return relocated;
    }

    public boolean isPomPackaging() {
        return POM_PACKAGING.equals(packaging);
    }

    public boolean isKnownJarPackaging() {
        return JAR_PACKAGINGS.contains(packaging);
    }

    public void setSnapshotTimestamp(@Nullable String snapshotTimestamp) {
        this.snapshotTimestamp = snapshotTimestamp;
    }

    @Nullable
    public String getSnapshotTimestamp() {
        return snapshotTimestamp;
    }
}
