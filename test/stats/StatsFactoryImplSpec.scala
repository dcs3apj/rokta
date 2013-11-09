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

import org.specs2.mutable.Specification
import model.NonPersistedGameDsl
import module.EmptyBindingModule
import dates.DaysAndTimes
import dates.IsoChronology
import org.specs2.mock.Mockito
import dates.WhenImplicits
import dates.StaticNow
import com.escalatesoft.subcut.inject.NewBindingModule
import dao.GameDao
import dao.PlayerDao
import dao.Transactional
import filter.BetweenGameFilter
import filter.ContiguousGameFilter
import dates.Now
import org.specs2.matcher.Matcher
import filter.SinceGameFilter
import filter.UntilGameFilter
import filter.YearGameFilter
import filter.MonthGameFilter
import filter.DayGameFilter

/**
 * @author alex
 *
 */
class StatsFactoryImplSpec extends Specification with Mockito
  with NonPersistedGameDsl with EmptyBindingModule with DaysAndTimes with IsoChronology with WhenImplicits {

  val now: Now = StaticNow(September(5, 2013) at midday)

  /**
   * Give tests access to the concrete [[StatsFactoryImpl]] and its required mocks.
   */
  def mocked[B](
    block: StatsFactoryImpl => LeagueFactory => CurrentResultsFactory => SnapshotsFactory => StreaksFactory => PlayerDao => GameDao => B): B = {
    val leagueFactory = mock[LeagueFactory]
    val currentResultsFactory = mock[CurrentResultsFactory]
    val snapshotsFactory = mock[SnapshotsFactory]
    val streaksFactory = mock[StreaksFactory]
    val gameDao = mock[GameDao]
    val playerDao = mock[PlayerDao]
    val transactional = new Transactional() {
      def tx[T](block: PlayerDao => GameDao => T): T = block(playerDao)(gameDao)
    }
    val statsFactory = new StatsFactoryImpl(Some(now), Some(leagueFactory), Some(currentResultsFactory),
        Some(snapshotsFactory), Some(streaksFactory), Some(transactional))
    block(statsFactory)(leagueFactory)(currentResultsFactory)(snapshotsFactory)(streaksFactory)(playerDao)(gameDao)
  }

  mocked {
    statsFactory: StatsFactoryImpl => leagueFactory: LeagueFactory => 
      currentResultsFactory: CurrentResultsFactory => snapshotsFactory: SnapshotsFactory => 
        streaksFactory: StreaksFactory => playerDao: PlayerDao => implicit gameDao: GameDao =>
    def matchCurrent(beResult: Matcher[Boolean]): Matcher[ContiguousGameFilter] = 
      beResult ^^ { (f: ContiguousGameFilter) => statsFactory.filterIsCurrent(f) }
    def beCurrent = matchCurrent(beTrue)
    def notBeCurrent = matchCurrent(beFalse)
    "The between game filter" should {
      "be current if it starts any time today" in {
        BetweenGameFilter(September(5, 2013) at (1 oclock).pm, September(6, 2013) at (2 oclock).pm) must beCurrent
      }
      "be current if it finishes any time today" in {
        BetweenGameFilter(September(4, 2013) at (1 oclock).pm, September(5, 2013) at (2 oclock)) must beCurrent
      }
      "be current if it encloses today" in {
        BetweenGameFilter(September(4, 2013) at (1 oclock).pm, September(6, 2013) at (2 oclock)) must beCurrent
      }
      "not be current if it finishes before today" in {
        BetweenGameFilter(September(2, 2013) at (1 oclock).pm, September(4, 2013) at (2 oclock)) must notBeCurrent
      }
      "not be current if it starts after today" in {
        BetweenGameFilter(September(7, 2013) at (1 oclock).pm, September(8, 2013) at (2 oclock)) must notBeCurrent
      }
    }
    "The since game filter" should {
      "be current if it starts today" in {
        SinceGameFilter(September(5, 2013) at (1 oclock).pm) must beCurrent
      }
      "be current if it starts in the past" in {
        SinceGameFilter(September(4, 2013) at (1 oclock).pm) must beCurrent
      }
      "not be current if it starts in the future" in {
        SinceGameFilter(September(6, 2013) at (1 oclock).pm) must notBeCurrent
      }
    }
    "The until game filter" should {
      "be current if it ends today" in {
        UntilGameFilter(September(5, 2013) at (1 oclock)) must beCurrent
      }
      "be current if it ends in the future" in {
        UntilGameFilter(September(6, 2013) at (1 oclock).pm) must beCurrent
      }
      "not be current if it ends in the past" in {
        UntilGameFilter(September(4, 2013) at (1 oclock).pm) must notBeCurrent
      }
    }
    "The year game filter" should {
      "be current this year" in {
        YearGameFilter(2013) must beCurrent
      }
      "not be current any other year" in {
        YearGameFilter(2012) must notBeCurrent
      }
    }
    "The month game filter" should {
      "be current this month" in {
        MonthGameFilter(2013, 9) must beCurrent
      }
      "not be current any other month" in {
        MonthGameFilter(2012, 9) must notBeCurrent
        MonthGameFilter(2013, 8) must notBeCurrent
      }
    }
    "The day game filter" should {
      "be current today" in {
        DayGameFilter(2013, 9, 5) must beCurrent
      }
      "not be current any other day" in {
        DayGameFilter(2013, 9, 4) must notBeCurrent
        DayGameFilter(2013, 8, 5) must notBeCurrent
        DayGameFilter(2012, 9, 5) must notBeCurrent
      }
    }
    
    "Today's players" should {
      "be empty when no games have been played today" in {
        statsFactory.findTodaysPlayers(Set()) must beEmpty
      }
      "include everyone who has played today" in {
        val gameOne = freddie losesAt (September(5, 2013) at (midday)) whilst(brian plays 2) and (roger plays 1)
        val gameTwo = brian losesAt (September(5, 2013) at (1 oclock).pm) whilst(john plays 2)
        statsFactory.findTodaysPlayers(Set(gameOne, gameTwo)) must contain(
          freddie.name, brian.name, roger.name, john.name).exactly
      }
    }
    
    "The exempt player" should {
      "be no-one when no games have been played today" in {
        statsFactory.findExemptPlayer(Set()) must beNone
      }
      "be the loser of the last game played today" in {
        val gameOne = freddie losesAt (September(5, 2013) at (midday)) whilst(brian plays 2) and (roger plays 1)
        val gameTwo = brian losesAt (September(5, 2013) at (1 oclock).pm) whilst(john plays 2)
        statsFactory.findExemptPlayer(Set(gameOne, gameTwo)) must beSome(brian.name)
      }
    }
  }
  
}