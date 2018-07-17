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
#ifndef OPENDNP3_TRANSPORTTX_H
#define OPENDNP3_TRANSPORTTX_H

#include <openpal/logging/Logger.h>
#include <openpal/container/Settable.h>
#include <openpal/container/StaticBuffer.h>

#include "opendnp3/StackStatistics.h"
#include "opendnp3/link/ITransportSegment.h"
#include "opendnp3/transport/TransportConstants.h"
#include "opendnp3/transport/TransportSeqNum.h"
#include "opendnp3/app/Message.h"

namespace opendnp3
{

/**
State/validation for the DNP3 transport layer's send channel.
*/
class TransportTx final : public ITransportSegment
{

public:

	TransportTx(const openpal::Logger& logger);

	void Configure(const Message& message);

	// -------  IBufferSegment ------------

	virtual const Addresses& GetAddresses() const override
	{
		return this->message.addresses;
	}

	virtual bool HasValue() const override;

	virtual openpal::RSlice GetSegment() override;

	virtual bool Advance() override;

	const StackStatistics::Transport::Tx& Statistics() const
	{
		return statistics;
	}

private:

	Message message;

	openpal::Settable<openpal::RSlice> txSegment;

	// Static buffer where we store tpdus that are being transmitted
	openpal::StaticBuffer<MAX_TPDU_LENGTH> tpduBuffer;

	openpal::Logger logger;
	StackStatistics::Transport::Tx statistics;
	TransportSeqNum sequence;
	uint32_t tpduCount = 0;
};

}

#endif

