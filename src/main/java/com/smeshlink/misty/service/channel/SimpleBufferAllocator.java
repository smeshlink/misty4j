/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package com.smeshlink.misty.service.channel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * A simplistic {@link IoBufferAllocator} which simply allocates a new
 * buffer every time.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
class SimpleBufferAllocator implements IoBufferAllocator {

    public IoBuffer allocate(int capacity, boolean direct) {
        return wrap(allocateNioBuffer(capacity, direct));
    }

    public ByteBuffer allocateNioBuffer(int capacity, boolean direct) {
        ByteBuffer nioBuffer;
        if (direct) {
            nioBuffer = ByteBuffer.allocateDirect(capacity);
        } else {
            nioBuffer = ByteBuffer.allocate(capacity);
        }
        return nioBuffer;
    }

    public IoBuffer wrap(ByteBuffer nioBuffer) {
        return new SimpleBuffer(nioBuffer);
    }

    public void dispose() {
        // Do nothing
    }

    private class SimpleBuffer extends AbstractIoBuffer {
        private ByteBuffer buf;

        protected SimpleBuffer(ByteBuffer buf) {
            super(SimpleBufferAllocator.this, buf.capacity());
            this.buf = buf;
            buf.order(ByteOrder.BIG_ENDIAN);
        }

        protected SimpleBuffer(SimpleBuffer parent, ByteBuffer buf) {
            super(parent);
            this.buf = buf;
        }

        public ByteBuffer buf() {
            return buf;
        }

        protected void buf(ByteBuffer buf) {
            this.buf = buf;
        }

        protected IoBuffer duplicate0() {
            return new SimpleBuffer(this, this.buf.duplicate());
        }

        protected IoBuffer slice0() {
            return new SimpleBuffer(this, this.buf.slice());
        }

        protected IoBuffer asReadOnlyBuffer0() {
            return new SimpleBuffer(this, this.buf.asReadOnlyBuffer());
        }

        public byte[] array() {
            return buf.array();
        }

        public int arrayOffset() {
            return buf.arrayOffset();
        }

        public boolean hasArray() {
            return buf.hasArray();
        }

        public void free() {
            // Do nothing
        }
    }
}
