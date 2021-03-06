/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.internal.util;

import org.neo4j.driver.internal.ConnectionSettings;
import org.neo4j.driver.internal.async.ChannelConnector;
import org.neo4j.driver.internal.async.ChannelConnectorImpl;
import org.neo4j.driver.internal.async.ChannelPipelineBuilder;
import org.neo4j.driver.internal.messaging.MessageFormat;
import org.neo4j.driver.internal.security.SecurityPlan;
import org.neo4j.driver.v1.Config;

public class ChannelTrackingDriverFactoryWithMessageFormat extends ChannelTrackingDriverFactory
{
    private final MessageFormat messageFormat;

    public ChannelTrackingDriverFactoryWithMessageFormat( MessageFormat messageFormat, Clock clock )
    {
        super( clock );
        this.messageFormat = messageFormat;
    }

    @Override
    protected ChannelConnector createRealConnector( ConnectionSettings settings, SecurityPlan securityPlan,
            Config config, Clock clock )
    {
        ChannelPipelineBuilder pipelineBuilder = new ChannelPipelineBuilderWithMessageFormat( messageFormat );
        return new ChannelConnectorImpl( settings, securityPlan, pipelineBuilder, config.logging(), clock );
    }
}

