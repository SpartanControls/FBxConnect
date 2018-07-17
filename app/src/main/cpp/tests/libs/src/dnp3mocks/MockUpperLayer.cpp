/*
 * Licensed to Green Energy Corp (www.greenenergycorp.com) under one or
 * more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Green Energy Corp licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This project was forked on 01/01/2013 by Automatak, LLC and modifications
 * may have been made to this file. Automatak, LLC licenses these modifications
 * to you under the terms of the License.
 */
#include "MockUpperLayer.h"

#include <testlib/BufferHelpers.h>

#include <openpal/util/ToHex.h>
#include <openpal/logging/LogMacros.h>

#include <memory>

using namespace openpal;
using namespace testlib;

namespace opendnp3
{

MockUpperLayer::MockUpperLayer() : isOnline(false)
{

}

bool MockUpperLayer::OnReceive(const Message& message)
{
	this->received.Write(message.payload);
	return true;
}

bool MockUpperLayer::OnTxReady()
{
	++counters.numTxReady;
	return true;
}

bool MockUpperLayer::OnLowerLayerUp()
{
	isOnline = true;
	++counters.numLayerUp;
	return true;
}

bool MockUpperLayer::OnLowerLayerDown()
{
	isOnline = false;
	++counters.numLayerDown;
	return true;
}

bool MockUpperLayer::SendDown(const openpal::RSlice& data, const Addresses& addresses)
{
	return this->pLowerLayer ? pLowerLayer->BeginTransmit(Message(addresses, data)) : false;
}

bool MockUpperLayer::SendDown(const std::string& hex, const Addresses& addresses)
{
	HexSequence hs(hex);
	return this->SendDown(hs.ToRSlice(), addresses);
}

}
