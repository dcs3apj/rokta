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

/**
 * The main SubCut [[BindingModule]] for this application.
 * @author alex
 *
 */
import com.escalatesoft.subcut.inject.NewBindingModule
import com.escalatesoft.subcut.inject.NewBindingModule._
import stats.SnapshotsFactory
import stats.SnapshotsFactoryImpl
import stats.LeagueFactory
import stats.LeagueFactoryImpl
import dao.SquerylDao
import dao.GameDao
import dao.PlayerDao
import dao.Transactional
import dao.SquerylDao
import stats.StreaksFactory
import stats.StreaksFactoryImpl
import stats.RaceGapCalculator
import stats.GapCalculator
import controllers.StatsController
import controllers.StatsController
import dates.Now
import dates.SystemNow
import stats.StatsFactory
import stats.StatsFactoryImpl
import stats.CurrentResultsFactory
import stats.CurrentResultsFactoryImpl

object RoktaBindingModule extends NewBindingModule(module => {
  import module._   // can now use bind directly

  // DAOs
  val squerylDao = new SquerylDao()
  bind[GameDao] toSingle(squerylDao)
  bind[PlayerDao] toSingle(squerylDao)
  bind[Transactional] toSingle(squerylDao)
  
  // Stats
  bind[SnapshotsFactory] toModuleSingle { implicit module => new SnapshotsFactoryImpl }
  bind[LeagueFactory] toModuleSingle { implicit module => new LeagueFactoryImpl }
  bind[StreaksFactory] toModuleSingle { implicit module => new StreaksFactoryImpl }
  bind[GapCalculator] toModuleSingle { implicit module => new RaceGapCalculator }
  bind[CurrentResultsFactory] toModuleSingle { implicit module => new CurrentResultsFactoryImpl }
  bind[Now] toSingle(SystemNow())
  bind[StatsFactory] toModuleSingle { implicit module => new StatsFactoryImpl}
  
  // Controllers
  bind[StatsController] toModuleSingle { implicit module => new StatsController }
    })
