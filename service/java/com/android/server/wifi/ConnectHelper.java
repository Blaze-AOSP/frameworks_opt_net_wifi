/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wifi;

import android.annotation.Nullable;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.server.wifi.util.ActionListenerWrapper;

/**
 * This class is used to hold connection logic that is shared between {@link WifiServiceImpl} and
 * other helper classes, because helper classes should not call WifiServiceImpl directly.
 */
public class ConnectHelper {
    private static final String TAG = "WifiConnectHelper";

    private final ActiveModeWarden mActiveModeWarden;
    private final WifiConfigManager mWifiConfigManager;

    public ConnectHelper(
            ActiveModeWarden activeModeWarden,
            WifiConfigManager wifiConfigManager) {
        mActiveModeWarden = activeModeWarden;
        mWifiConfigManager = wifiConfigManager;
    }

    /** Trigger connection to an existing network and provide status via the provided callback. */
    public void connectToNetwork(
            NetworkUpdateResult result,
            @Nullable ActionListenerWrapper wrapper,
            int callingUid) {
        int netId = result.getNetworkId();
        boolean success = mWifiConfigManager.updateBeforeConnect(netId, callingUid);
        if (success) {
            mActiveModeWarden.getPrimaryClientModeManager().connectNetwork(
                    result, wrapper, callingUid);
        } else {
            Log.e(TAG, "connectToNetwork Invalid network Id=" + netId);
            wrapper.sendFailure(WifiManager.ERROR);
        }
    }
}