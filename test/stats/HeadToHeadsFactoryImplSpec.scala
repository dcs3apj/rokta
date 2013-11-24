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

package stats

import model.NonPersistedGameDsl
import org.specs2.mutable.Specification
import dates.DaysAndTimes
import dates.UtcChronology

/**
 * @author alex
 *
 */
class HeadToHeadsFactoryImplSpec extends Specification with NonPersistedGameDsl with DaysAndTimes with UtcChronology {

  val headToHeadsFactory = new HeadToHeadsFactoryImpl
  
  "A game that does not end with a head to head" should {
    "be ignored" in {
      val game = freddie losesAt September(5, 1972) whilst (brian plays 4) and (roger plays 4)
      headToHeadsFactory(Seq(game)) must beEmpty
    }
  }

  "A game that does end with a head to head" should {
    "be recorded as a win for the winner" in {
      val game = freddie losesAt September(5, 1972) whilst (brian plays 4) and (roger plays 3)
      val headToHeads = headToHeadsFactory(Seq(game))
      headToHeads.keys must containTheSameElementsAs(Seq(brian.name))
      headToHeads(brian.name)  must containTheSameElementsAs(Seq(freddie.name -> 1))
    }
  }

  "Two games that end with the same head to head" should {
    "be recorded as two wins for the winner" in {
      val game = freddie losesAt September(5, 1972) whilst (brian plays 4) and (roger plays 3)
      val headToHeads = headToHeadsFactory(Seq(game, game))
      headToHeads.keys must containTheSameElementsAs(Seq(brian.name))
      headToHeads(brian.name)  must containTheSameElementsAs(Seq(freddie.name -> 2))
    }
  }

  "Two games that end with different head to heads with the same winner" should {
    "be recorded as two wins for the winner" in {
      val gameA = freddie losesAt September(5, 1972) whilst (brian plays 4) and (roger plays 3)
      val gameB = roger losesAt September(5, 1972) whilst (brian plays 4) and (freddie plays 3)
      val headToHeads = headToHeadsFactory(Seq(gameA, gameB))
      headToHeads.keys must containTheSameElementsAs(Seq(brian.name))
      headToHeads(brian.name)  must containTheSameElementsAs(Seq(freddie.name -> 1, roger.name -> 1))
    }
  }

  "Two games that end with different head to heads with different winners" should {
    "be recorded as wins for each winner" in {
      val gameA = freddie losesAt September(5, 1972) whilst (brian plays 4) and (roger plays 3)
      val gameB = roger losesAt September(5, 1972) whilst (freddie plays 4) and (brian plays 3)
      val headToHeads = headToHeadsFactory(Seq(gameA, gameB))
      headToHeads.keys must containTheSameElementsAs(Seq(brian.name, freddie.name))
      headToHeads(brian.name)  must containTheSameElementsAs(Seq(freddie.name -> 1))
      headToHeads(freddie.name)  must containTheSameElementsAs(Seq(roger.name -> 1))
    }
  }
}