/**
 * 
 */
package uk.co.unclealex.rokta.client.gin;

import javax.inject.Singleton;

import uk.co.unclealex.rokta.client.cache.InformationCache;
import uk.co.unclealex.rokta.client.cache.InformationCacheImpl;
import uk.co.unclealex.rokta.client.cache.InformationService;
import uk.co.unclealex.rokta.client.cache.InformationServiceImpl;
import uk.co.unclealex.rokta.client.filter.GameFilterFactory;
import uk.co.unclealex.rokta.client.places.LeaguePlace;
import uk.co.unclealex.rokta.client.places.RoktaActivityMapper;
import uk.co.unclealex.rokta.client.places.RoktaPlaceHistoryMapper;
import uk.co.unclealex.rokta.client.security.AuthenticationManager;
import uk.co.unclealex.rokta.client.security.AuthenticationManagerImpl;
import uk.co.unclealex.rokta.client.security.SecureAsyncCallbackExecutor;
import uk.co.unclealex.rokta.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.rokta.client.util.CanWaitSupport;
import uk.co.unclealex.rokta.client.util.CanWaitSupportImpl;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Provides;

/**
 * Copyright 2011 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
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
 * @author unclealex72
 *
 */
public class RoktaInternalModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
		bind(SimplePanel.class).in(Singleton.class);
		bind(PlaceHistoryMapper.class).to(RoktaPlaceHistoryMapper.class).in(Singleton.class);

		bind(ActivityMapper.class).to(RoktaActivityMapper.class).in(Singleton.class);
		
		bind(AsyncCallbackExecutor.class).to(SecureAsyncCallbackExecutor.class).in(Singleton.class);
		bind(AuthenticationManager.class).to(AuthenticationManagerImpl.class).in(Singleton.class);
		bind(CanWaitSupport.class).to(CanWaitSupportImpl.class);
		bind(InformationService.class).to(InformationServiceImpl.class).in(Singleton.class);
		bind(InformationCache.class).to(InformationCacheImpl.class).in(Singleton.class);

	}

	@Provides
	@Singleton
	public PlaceHistoryHandler getHistoryHandler(
			PlaceController placeController, PlaceHistoryMapper historyMapper,
			EventBus eventBus, ActivityManager activityManager) {
		/*
		 * !!Important note!! maybe you have noticed that we are passing an
		 * instance of ActivityManager to the HistoryHandler provider method and
		 * not assigning it to anything. This is simply to force an instance of
		 * activity manager to get created, initialize and register itself with
		 * the event bus. It's just a little trick.
		 */
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
				historyMapper);
		historyHandler.register(
				placeController, 
				eventBus, 
				new LeaguePlace(GameFilterFactory.createDefaultGameFilter()));
		return historyHandler;
	}

	@Provides
	@Singleton
	public PlaceController getPlaceController(EventBus eventBus) {
		return new PlaceController(eventBus);
	}

	@Provides
	@Singleton
	public ActivityManager getActivityManager(ActivityMapper mapper,
			EventBus eventBus, SimplePanel display) {
		ActivityManager activityManager = new ActivityManager(mapper, eventBus);
		activityManager.setDisplay(display);
		return activityManager;
	}

}
