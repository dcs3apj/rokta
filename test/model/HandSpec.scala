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

package model

import org.specs2.mutable.Specification
import model.Hand._

/**
 * @author alex
 *
 */
class HandSpec extends Specification {

  "The number of different hands" should {
    "equal 3" in {
      Hand.values.size must be equalTo(3)
      Hand.valueMap.size must be equalTo(3)
    }
  }
  
  "Rock" should {
    "beat scissors" in {
      ROCK.beats(SCISSORS) must be equalTo(true)
    }
    "not beat paper" in {
      ROCK.beats(PAPER) must be equalTo(false)
    }
    "not beat rock" in {
      ROCK.beats(ROCK) must be equalTo(false)
    }
  }

  "Scissors" should {
    "not beat scissors" in {
      SCISSORS.beats(SCISSORS) must be equalTo(false)
    }
    "beat paper" in {
      SCISSORS.beats(PAPER) must be equalTo(true)
    }
    "not beat rock" in {
      SCISSORS.beats(ROCK) must be equalTo(false)
    }
  }

  "Paper" should {
    "not beat scissors" in {
      PAPER.beats(SCISSORS) must be equalTo(false)
    }
    "not beat paper" in {
      PAPER.beats(PAPER) must be equalTo(false)
    }
    "beat rock" in {
      PAPER.beats(ROCK) must be equalTo(true)
    }
  }
}