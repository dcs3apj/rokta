/**
 * 
 */
package uk.co.unclealex.rokta.client.security;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import uk.co.unclealex.rokta.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.rokta.client.util.CanWait;
import uk.co.unclealex.rokta.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.rokta.client.util.WaitingController;
import uk.co.unclealex.rokta.shared.service.AnonymousRoktaServiceAsync;
import uk.co.unclealex.rokta.shared.service.UserRoktaServiceAsync;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

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
public class SecureAsyncCallbackExecutor implements AsyncCallbackExecutor {

  private final WaitingController i_waitingController;
	private final AnonymousRoktaServiceAsync i_anonymousRoktaService;
	private final UserRoktaServiceAsync i_userRoktaService;

	@Inject
	public SecureAsyncCallbackExecutor(WaitingController waitingController,
			AnonymousRoktaServiceAsync anonymousRoktaService, UserRoktaServiceAsync userRoktaService) {
		super();
		i_waitingController = waitingController;
		i_anonymousRoktaService = anonymousRoktaService;
		i_userRoktaService = userRoktaService;
	}

	@Override
	public <T> void execute(final ExecutableAsyncCallback<T> callback) {
		execute(callback, null);
	}
	
	protected <T> void execute(final ExecutableAsyncCallback<T> callback, final Runnable cancelAction) {
		class RunnableAsyncCallback implements AsyncCallback<T>, Runnable {
			public void onSuccess(T result) {
				callback.onSuccess(result);
			}			
			public void onFailure(Throwable caught) {
				SecureAsyncCallbackExecutor.this.onFailure(caught, this, cancelAction, callback);
			}
			public void run() {
				callback.execute(getAnonymousRoktaService(), getUserRoktaService(), this);
			}
		}
		new RunnableAsyncCallback().run();
	}

	public <T> void onFailure(Throwable caught, Runnable originalAction, Runnable cancelAction, final ExecutableAsyncCallback<T> callback) {
		if (isRefused(caught)) {
			onRefused();
		}
		else if (isLoginRequired(caught)) {
			onLoginRequired(originalAction, cancelAction);
		}
		else {
			callback.onFailure(caught);
		}
	}

  protected boolean isRefused(Throwable t) {
		return (t instanceof StatusCodeException) && 403 == ((StatusCodeException) t).getStatusCode();
	}
	
	protected void onRefused() {
		Window.alert("You were refused access to this resource.");
	}
	
	protected void onLoginRequired(Runnable originalAction, Runnable cancelAction) {
	  onRefused();
	}

	/**
	 * @param caught
	 * @return
	 */
	private boolean isLoginRequired(Throwable caught) {
		return caught instanceof InvocationException && caught.getMessage().contains("/j_spring_security_check");
	}

	protected boolean isLoginFailed(Throwable caught) {
		return false;
	}

	@Override
	public <T> void executeAndWait(
			final ExecutableAsyncCallback<T> executableAsyncCallback, final String message, final CanWait... canWaits) {
    final WaitingController waitingController = getWaitingController();
		final List<CanWait> canWaitList = Arrays.asList(canWaits);
		class WaitCallback implements ExecutableAsyncCallback<T>, Runnable {
	  	Integer waitingHandler;
	    @Override
	    public void onSuccess(T result) {
	    	stopWaiting();
	      executableAsyncCallback.onSuccess(result);
	    }

			protected void stopWaiting() {
				if (waitingHandler != null) {
	    		waitingController.stopWaiting(waitingHandler, canWaitList);
	    	}
			}

	    @Override
	    public void onFailure(Throwable cause) {
	    	stopWaiting();
	      executableAsyncCallback.onFailure(cause);
	    }
	    
	    @Override
	    public void run() {
	    	stopWaiting();
	    }
	    
	    @Override
	    public void execute(AnonymousRoktaServiceAsync anonymousRoktaService,
	        UserRoktaServiceAsync userRoktaService,
	        AsyncCallback<T> callback) {
	    	if (waitingHandler == null) {
	    		waitingHandler = waitingController.startWaiting(message, canWaitList);
	    	}
	      executableAsyncCallback.execute(anonymousRoktaService, userRoktaService, callback);

	    }
    };
    WaitCallback waitCallback = new WaitCallback();
    execute(waitCallback, waitCallback);
	}
	
  public WaitingController getWaitingController() {
    return i_waitingController;
  }

	public AnonymousRoktaServiceAsync getAnonymousRoktaService() {
		return i_anonymousRoktaService;
	}

	public UserRoktaServiceAsync getUserRoktaService() {
		return i_userRoktaService;
	}
}
