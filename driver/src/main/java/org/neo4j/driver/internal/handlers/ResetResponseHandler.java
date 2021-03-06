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
package org.neo4j.driver.internal.handlers;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.neo4j.driver.internal.async.inbound.InboundMessageDispatcher;
import org.neo4j.driver.internal.spi.ResponseHandler;
import org.neo4j.driver.internal.util.Clock;
import org.neo4j.driver.v1.Value;

import static org.neo4j.driver.internal.async.ChannelAttributes.setLastUsedTimestamp;

public class ResetResponseHandler implements ResponseHandler
{
    private final Channel channel;
    private final ChannelPool pool;
    private final InboundMessageDispatcher messageDispatcher;
    private final Clock clock;
    private final CompletableFuture<Void> releaseFuture;

    public ResetResponseHandler( Channel channel, ChannelPool pool, InboundMessageDispatcher messageDispatcher,
            Clock clock, CompletableFuture<Void> releaseFuture )
    {
        this.channel = channel;
        this.pool = pool;
        this.messageDispatcher = messageDispatcher;
        this.clock = clock;
        this.releaseFuture = releaseFuture;
    }

    @Override
    public void onSuccess( Map<String,Value> metadata )
    {
        releaseChannel();
    }

    @Override
    public void onFailure( Throwable error )
    {
        releaseChannel();
    }

    @Override
    public void onRecord( Value[] fields )
    {
        throw new UnsupportedOperationException();
    }

    private void releaseChannel()
    {
        messageDispatcher.unMuteAckFailure();
        setLastUsedTimestamp( channel, clock.millis() );

        Future<Void> released = pool.release( channel );
        released.addListener( ignore -> releaseFuture.complete( null ) );
    }
}
