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

#ifndef ASIODNP3_ERRORCODES_H
#define ASIODNP3_ERRORCODES_H

#include <system_error>
#include <string>


namespace asiodnp3
{

enum class Error : int
{
	SHUTTING_DOWN,
	NO_TLS_SUPPORT,
	NO_SERIAL_SUPPORT
};

class ErrorCategory final : public std::error_category
{
public:

	static const std::error_category& Instance()
	{
		return instance;
	}

	virtual const char* name() const noexcept
	{
		return "dnp3";
	}

	virtual std::string message(int ev) const;

private:

	ErrorCategory() {}
	ErrorCategory(const ErrorCategory&) = delete;

	static ErrorCategory instance;
};

inline std::error_code make_error_code(Error err)
{
	return std::error_code(static_cast<int>(err), ErrorCategory::Instance());
}

}


namespace std
{

template <>
struct is_error_code_enum<asiodnp3::Error> : public true_type {};

}


#endif
