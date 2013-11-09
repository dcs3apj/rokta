/**
 * Copyright 2013 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with work for additional information
 * regarding copyright ownership.  The ASF licenses file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.databind.SerializationFeature

/**
 * A global object used to configure Jackson JSON support.
 * @author alex
 *
 */
object Json {

  /**
   * The scala aware object mapper to use.
   */
  private val objectMapper: ObjectMapper =
    new ObjectMapper().registerModules(DefaultScalaModule, new JodaModule).
      disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
  
  def read[T](json: String)(implicit m: Manifest[T]): T = 
    objectMapper.readValue(json, m.runtimeClass).asInstanceOf[T]
    
  def apply(a: Any): String = objectMapper.writeValueAsString(a)
}