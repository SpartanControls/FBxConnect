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
#ifndef __MOCK_LINK_LAYER_USER_H_
#define __MOCK_LINK_LAYER_USER_H_

#include <opendnp3/LayerInterfaces.h>
#include <opendnp3/link/ILinkLayer.h>

#include <deque>
#include <string>

namespace opendnp3
{

class MockTransportLayer final : public IUpperLayer
{

public:

	struct Counters
	{
		uint32_t numTxReady = 0;
		uint32_t numLayerUp = 0;
		uint32_t numLayerDown = 0;
	};

	MockTransportLayer();

	void SetLinkLayer(ILinkLayer& linkLayer);

	bool SendDown(ITransportSegment& segments);

	bool IsOnline() const
	{
		return isOnline;
	}

	bool CountersEquals(const Counters& other)
	{
		return (counters.numTxReady == other.numTxReady)
		       && (counters.numLayerUp == other.numLayerUp)
		       && (counters.numLayerDown == other.numLayerDown);
	}

	Counters GetCounters() const
	{
		return counters;
	}

	// these are the NVII delegates
	virtual bool OnReceive(const Message& message) override;
	virtual bool OnTxReady() override;
	virtual bool OnLowerLayerUp() override;
	virtual bool OnLowerLayerDown() override;

	std::deque<std::string> receivedQueue;

private:
	ILinkLayer* pLinkLayer;
	bool isOnline;
	Counters counters;
};



}

#endif
