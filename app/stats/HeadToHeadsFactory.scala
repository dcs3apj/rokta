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

import model.Game

/**
 * A trait used to count the results in rounds where there are just two players.
 * @author alex
 *
 */
trait HeadToHeadsFactory {

  /**
   * Collate the results of all head-to-heads.
   * @return A map containing a count of all the head-to-heads. The first key is the name of the winner and
   * the second the name of the loser.
   */
  def apply(games: Iterable[Game]): Map[String, Map[String, Int]]
}