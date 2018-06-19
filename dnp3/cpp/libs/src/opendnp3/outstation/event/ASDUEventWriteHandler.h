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
#ifndef OPENDNP3_ASDUEVENTWRITEHANDLER_H
#define OPENDNP3_ASDUEVENTWRITEHANDLER_H

#include "opendnp3/app/HeaderWriter.h"
#include "opendnp3/app/DNP3Serializer.h"

#include "openpal/util/Uncopyable.h"

#include "IEventWriteHandler.h"

namespace opendnp3
{

class ASDUEventWriteHandler final : public IEventWriteHandler, private openpal::Uncopyable
{

public:

	explicit ASDUEventWriteHandler(const HeaderWriter& writer) : writer(writer) {}

	virtual uint16_t Write(EventBinaryVariation variation, const Binary& first, IEventCollection<Binary>& items) override;
	virtual uint16_t Write(EventDoubleBinaryVariation variation, const DoubleBitBinary& first, IEventCollection<DoubleBitBinary>& items) override;
	virtual uint16_t Write(EventCounterVariation variation, const Counter& first, IEventCollection<Counter>& items) override;
	virtual uint16_t Write(EventFrozenCounterVariation variation, const FrozenCounter& first, IEventCollection<FrozenCounter>& items) override;
	virtual uint16_t Write(EventAnalogVariation variation, const Analog& first, IEventCollection<Analog>& items) override;
	virtual uint16_t Write(EventBinaryOutputStatusVariation variation, const BinaryOutputStatus& first, IEventCollection<BinaryOutputStatus>& items) override;
	virtual uint16_t Write(EventAnalogOutputStatusVariation variation, const AnalogOutputStatus& first, IEventCollection<AnalogOutputStatus>& items) override;
	virtual uint16_t Write(EventOctetStringVariation variation, const OctetString& first, IEventCollection<OctetString>& items) override;

private:

	HeaderWriter writer;
};

}

#endif

