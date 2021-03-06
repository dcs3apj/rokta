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

import scala.collection.SortedMap

import org.joda.time.DateTime
import org.specs2.mutable.Specification

import dates.DaysAndTimes
import dates.IsoChronology
import model.Game
import model.Hand._
import model.JodaDateTime._
import model.NonPersistedGameDsl
import model.Player
import stats.Snapshot._
/**
 * @author alex
 *
 */
class SnapshotsFactoryImplSpec extends Specification with DaysAndTimes with IsoChronology with NonPersistedGameDsl {

  val snapshotsFactory = new SnapshotsFactoryImpl

  "Adding a new player to a snapshot" should {
    val game = at(September(12, 2013) at (11 oclock), freddie plays PAPER, brian plays PAPER, roger plays PAPER) and (
        freddie plays ROCK, brian plays ROCK, roger plays PAPER) and (
        freddie plays SCISSORS, brian plays ROCK)
    
    val startingPoint = SnapshotCumulation()
    "record their wins if they win" in {
      val newCumulation = snapshotsFactory.addToSnapshot(game)(startingPoint, roger)
      val newSnapshot = newCumulation.cumulativeSnapshot
      newSnapshot.toSeq must contain(exactly(roger -> win(PAPER, PAPER)))
      newCumulation.historicalSnapshots.toSeq must be empty
    }
    "record their losses if they lose" in {
      val newCumulation = snapshotsFactory.addToSnapshot(game)(startingPoint, freddie)
      val newSnapshot = newCumulation.cumulativeSnapshot
      newSnapshot.toSeq must contain(exactly(freddie -> lose(PAPER, ROCK, SCISSORS)))
      newCumulation.historicalSnapshots.toSeq must be empty
    }
  }

  "Updating a player in a snapshot" should {
    val game = at(September(12, 2013) at (11 oclock), freddie plays PAPER, brian plays PAPER, roger plays PAPER) and (
        freddie plays ROCK, brian plays ROCK, roger plays PAPER) and (
        freddie plays SCISSORS, brian plays ROCK)
    
    val startingPoint = SnapshotCumulation(
      Map(freddie -> win(ROCK), roger -> lose(SCISSORS, SCISSORS, PAPER), brian -> win(ROCK, ROCK, PAPER, PAPER)),
      SortedMap.empty[DateTime, Map[Player, Snapshot]])
    "update their wins if they win" in {
      val newCumulation = snapshotsFactory.addToSnapshot(game)(startingPoint, roger)
      val newSnapshot = newCumulation.cumulativeSnapshot
      newSnapshot.toSeq must contain(exactly(
          freddie -> win(ROCK),
          roger -> lose(SCISSORS, SCISSORS, PAPER).win(PAPER, PAPER),
          brian -> win(ROCK, ROCK, PAPER, PAPER)))
      newCumulation.historicalSnapshots.toSeq must be empty
    }
    "record their losses if they lose" in {
      val newCumulation = snapshotsFactory.addToSnapshot(game)(startingPoint, freddie)
      val newSnapshot = newCumulation.cumulativeSnapshot
      newSnapshot.toSeq must contain(exactly(
          freddie -> win(ROCK).lose(PAPER, ROCK, SCISSORS),
          roger -> lose(SCISSORS, SCISSORS, PAPER),
          brian -> win(ROCK, ROCK, PAPER, PAPER)))
      newCumulation.historicalSnapshots.toSeq must be empty
    }
  }
  
  "Adding a game to a snapshot" should {
    val previousSnapshot: Map[Player, Snapshot] = 
      Map(freddie -> win(ROCK, SCISSORS), brian -> lose(ROCK, PAPER, ROCK), roger -> lose(PAPER, SCISSORS, PAPER, ROCK))
    val startingPoint = SnapshotCumulation(
      previousSnapshot, 
      SortedMap((September(12, 2013) at (11 oclock)) -> previousSnapshot))
      
    val game = at(September(12, 2013) at midday, freddie plays PAPER, brian plays PAPER, john plays PAPER) and (
        freddie plays ROCK, brian plays ROCK, john plays PAPER) and (
        freddie plays SCISSORS, brian plays ROCK)
    
    
    val expectedSnapshot: Map[Player, Snapshot] =
      Map(
        freddie -> win(ROCK, SCISSORS).lose(PAPER, ROCK, SCISSORS),
        brian -> lose(ROCK, PAPER, ROCK).win(PAPER, ROCK, ROCK),
        roger -> lose(PAPER, SCISSORS, PAPER, ROCK),
        john -> win(PAPER, PAPER))
    val newCumulation = snapshotsFactory.accumulateSnapshots(startingPoint, game)
    "Add the game data to the latest cumulation" in {
      newCumulation.cumulativeSnapshot must be equalTo(expectedSnapshot)
    }
    "Record the latest cumulation in its history" in {
      newCumulation.historicalSnapshots.toSeq must contain(exactly(
        (September(12, 2013) at (11 oclock)) -> previousSnapshot, (September(12, 2013) at (12 oclock)) -> expectedSnapshot)).inOrder
    }
  }

  "Getting the snapshot history for three games" should {
    val timeA = September(12, 2013) at (11 oclock)
    val timeB = September(12, 2013) at (15 oclock)
    val timeC = September(13, 2013) at (9 oclock)
    val gameA: Game = //freddie losesAt timeA whilst (brian plays 3) and (roger plays 4)
      at(timeA, freddie plays PAPER, brian plays SCISSORS, roger plays ROCK) and (
        freddie plays ROCK, brian plays ROCK, roger plays ROCK) and (
        freddie plays SCISSORS, brian plays ROCK, roger plays SCISSORS) and (freddie plays SCISSORS, roger plays ROCK)
    val gameB: Game = //brian losesAt timeB whilst (roger plays 2)
      at(timeB, brian plays ROCK, roger plays ROCK) and (brian plays PAPER, roger plays SCISSORS)
    val gameC: Game = //freddie losesAt timeC whilst (brian plays 1) and (roger plays 2)
      at(timeC, freddie plays ROCK, roger plays ROCK, brian plays SCISSORS) and (roger plays ROCK, freddie plays SCISSORS)
    val snapshotA: Map[Player, Snapshot] =
      Map(
        freddie -> lose(PAPER, ROCK,  SCISSORS, SCISSORS),
        brian -> win(SCISSORS, ROCK, ROCK),
        roger -> win(ROCK, ROCK, SCISSORS, ROCK))
    val snapshotAB: Map[Player, Snapshot] =
      Map(
        freddie -> lose(PAPER, ROCK,  SCISSORS, SCISSORS),
        brian -> win(SCISSORS, ROCK, ROCK).lose(ROCK, PAPER),
        roger -> win(ROCK, ROCK, SCISSORS, ROCK).win(ROCK, SCISSORS))
    val snapshotABC: Map[Player, Snapshot] =
      Map(
        freddie -> lose(PAPER, ROCK,  SCISSORS, SCISSORS).lose(ROCK, SCISSORS),
        brian -> win(SCISSORS, ROCK, ROCK).lose(ROCK, PAPER).win(SCISSORS),
        roger -> win(ROCK, ROCK, SCISSORS, ROCK).win(ROCK, SCISSORS).win(ROCK, ROCK))
    val snapshots: IndexedSeq[Pair[DateTime, Map[Player, Snapshot]]] =
      snapshotsFactory(Seq(gameA, gameB, gameC)).toIndexedSeq
    "record three snapshots" in {
      snapshots must have size(3)
    }
    "record the first snapshot" in {
      snapshots(0) must be equalTo(timeA -> snapshotA)
    }
    "record the second snapshot" in {
      snapshots(1) must be equalTo(timeB -> snapshotAB)
    }
    "record the third snapshot" in {
      snapshots(2) must be equalTo(timeC -> snapshotABC)
    }
  }

  implicit def toNameAndSnapshot(playerAndSnapshot: Pair[Player, Snapshot]): Pair[String, Snapshot] =
    playerAndSnapshot._1 -> playerAndSnapshot._2
}