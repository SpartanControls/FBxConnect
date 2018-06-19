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
#ifndef OPENPAL_ARRAYVIEW_H
#define OPENPAL_ARRAYVIEW_H

#include "HasSize.h"

#include <assert.h>

namespace openpal
{

/**
* Acts as a safe facade around an underlying array
*/
template <class T, class W>
class ArrayView : public HasSize<W>
{

public:

	static ArrayView<T, W> Empty()
	{
		return ArrayView(nullptr, 0);
	}

	ArrayView(T* start, W size) : HasSize<W>(size), buffer(start)
	{}

	inline bool Contains(W index) const
	{
		return index < this->size;
	}

	inline bool Contains(W start, W stop) const
	{
		return (start < stop) && Contains(stop);
	}

	inline T& operator[](W index)
	{
		assert(index < this->size);
		return buffer[index];
	}

	inline const T& operator[](W index) const
	{
		assert(index < this->size);
		return buffer[index];
	}

	template <class Action>
	void foreach(const Action& action)
	{
		for (W i = 0; i < this->size; ++i)
		{
			action(buffer[i]);
		}
	}

private:
	T* buffer;
};



}

#endif
