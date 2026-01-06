package com.donovanbeulze.wifirtt;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

@CapacitorPlugin(
    name = "WifiRtt",
    permissions = {
        @Permission(
            strings = { Manifest.permission.ACCESS_FINE_LOCATION },
            alias = "location"
        )
    }
)
public class WifiRttPlugin extends Plugin {

    private WifiManager wifiManager;
    private WifiRttManager rttManager;
    private BroadcastReceiver wifiScanReceiver;
    private boolean isRanging = false;
    private Handler rangingHandler;
    private Runnable rangingRunnable;
    private List<ScanResult> lastScanResults = new ArrayList<>();

    @Override
    public void load() {
        super.load();
        Context context = getContext();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            rttManager = (WifiRttManager) context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);
        }

        rangingHandler = new Handler(Looper.getMainLooper());
    }

    @PluginMethod
    public void isSupported(PluginCall call) {
        JSObject ret = new JSObject();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            ret.put("supported", false);
            ret.put("reason", "Android version too low (requires Android 9.0 / API 28+)");
            call.resolve(ret);
            return;
        }

        if (rttManager == null) {
            ret.put("supported", false);
            ret.put("reason", "WifiRttManager not available");
            call.resolve(ret);
            return;
        }

        PackageManager pm = getContext().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)) {
            ret.put("supported", false);
            ret.put("reason", "Wi-Fi RTT not supported on this device");
            call.resolve(ret);
            return;
        }

        ret.put("supported", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void isWifiEnabled(PluginCall call) {
        JSObject ret = new JSObject();

        if (wifiManager == null) {
            ret.put("enabled", false);
            call.resolve(ret);
            return;
        }

        ret.put("enabled", wifiManager.isWifiEnabled());
        call.resolve(ret);
    }

    @PluginMethod
    public void startScan(PluginCall call) {
        if (wifiManager == null) {
            call.reject("WifiManager not available");
            return;
        }

        if (!wifiManager.isWifiEnabled()) {
            call.reject("Wi-Fi is disabled");
            return;
        }

        if (getPermissionState("location") != PermissionState.GRANTED) {
            call.reject("Location permission required for Wi-Fi scanning");
            return;
        }

        // Register scan receiver
        if (wifiScanReceiver != null) {
            try {
                getContext().unregisterReceiver(wifiScanReceiver);
            } catch (Exception e) {
                // Receiver not registered
            }
        }

        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    lastScanResults = wifiManager.getScanResults();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean started = wifiManager.startScan();

        if (!started) {
            call.reject("Failed to start scan (possibly throttled by Android)");
            return;
        }

        JSObject ret = new JSObject();
        ret.put("started", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void getScanResults(PluginCall call) {
        if (wifiManager == null) {
            call.reject("WifiManager not available");
            return;
        }

        if (getPermissionState("location") != PermissionState.GRANTED) {
            call.reject("Location permission required for scan results");
            return;
        }

        List<ScanResult> scanResults = wifiManager.getScanResults();
        if (scanResults == null) {
            scanResults = lastScanResults;
        } else {
            lastScanResults = scanResults;
        }

        JSArray resultsArray = new JSArray();

        for (ScanResult result : scanResults) {
            JSObject resultObj = new JSObject();

            if (result.SSID != null && !result.SSID.isEmpty()) {
                resultObj.put("ssid", result.SSID);
            }

            resultObj.put("bssid", result.BSSID);
            resultObj.put("frequency", result.frequency);
            resultObj.put("rssi", result.level);

            // Check if AP supports 802.11mc (RTT)
            boolean is80211mcResponder = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                is80211mcResponder = result.is80211mcResponder();
            }
            resultObj.put("is80211mcResponder", is80211mcResponder);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                resultObj.put("channelWidth", result.channelWidth);
            }

            resultObj.put("timestamp", result.timestamp);

            resultsArray.put(resultObj);
        }

        JSObject ret = new JSObject();
        ret.put("results", resultsArray);
        call.resolve(ret);
    }

    @PluginMethod
    public void startRanging(PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            notifyError("NOT_SUPPORTED", "Android version too low for RTT", null);
            call.reject("Android version too low for RTT");
            return;
        }

        if (rttManager == null) {
            notifyError("NOT_SUPPORTED", "WifiRttManager not available", null);
            call.reject("WifiRttManager not available");
            return;
        }

        if (!wifiManager.isWifiEnabled()) {
            notifyError("WIFI_DISABLED", "Wi-Fi is disabled", null);
            call.reject("Wi-Fi is disabled");
            return;
        }

        if (getPermissionState("location") != PermissionState.GRANTED) {
            requestPermissionForAlias("location", call, "locationPermissionCallback");
            return;
        }

        JSArray targetsArray = call.getArray("targets");
        if (targetsArray == null || targetsArray.length() == 0) {
            call.reject("No targets specified");
            return;
        }

        List<String> targetBssids = new ArrayList<>();
        for (int i = 0; i < targetsArray.length(); i++) {
            try {
                org.json.JSONObject target = targetsArray.getJSONObject(i);
                String bssid = target.getString("bssid");
                if (bssid != null && !bssid.isEmpty()) {
                    targetBssids.add(bssid);
                }
            } catch (Exception e) {
                // Skip invalid target
            }
        }

        if (targetBssids.isEmpty()) {
            call.reject("No valid targets specified");
            return;
        }

        Integer timeoutMs = call.getInt("timeoutMs", 5000);
        Boolean scanFirst = call.getBoolean("scanFirst", false);
        Integer intervalMs = call.getInt("resultIntervalMs", 0);

        if (scanFirst) {
            // Force a scan first
            if (!wifiManager.startScan()) {
                call.reject("Failed to start scan");
                return;
            }
            // Wait a bit for scan to complete
            rangingHandler.postDelayed(() -> {
                performRanging(targetBssids, timeoutMs, intervalMs, call);
            }, 2000);
        } else {
            performRanging(targetBssids, timeoutMs, intervalMs, call);
        }
    }

    @PermissionCallback
    private void locationPermissionCallback(PluginCall call) {
        if (getPermissionState("location") == PermissionState.GRANTED) {
            startRanging(call);
        } else {
            notifyError("PERMISSION_DENIED", "Location permission denied by user", null);
            call.reject("Location permission denied");
        }
    }

    private void performRanging(List<String> targetBssids, int timeoutMs, int intervalMs, PluginCall call) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }

        // Get scan results to find matching APs
        List<ScanResult> scanResults = wifiManager.getScanResults();
        if (scanResults == null) {
            scanResults = lastScanResults;
        }

        List<ScanResult> rttCapableAPs = new ArrayList<>();
        for (ScanResult result : scanResults) {
            if (targetBssids.contains(result.BSSID) && result.is80211mcResponder()) {
                rttCapableAPs.add(result);
            }
        }

        if (rttCapableAPs.isEmpty()) {
            notifyError("NO_RESPONDER", "No RTT-capable access points found for specified targets", null);
            call.reject("No RTT-capable access points found");
            return;
        }

        // Build ranging request
        RangingRequest.Builder requestBuilder = new RangingRequest.Builder();
        requestBuilder.addAccessPoints(rttCapableAPs);
        RangingRequest request = requestBuilder.build();

        isRanging = true;

        // Create callback
        RangingResultCallback callback = new RangingResultCallback() {
            @Override
            public void onRangingFailure(int code) {
                String errorMessage = "Ranging failed with code: " + code;
                notifyError("RANGING_FAILED", errorMessage, code);
            }

            @Override
            public void onRangingResults(@NonNull List<RangingResult> results) {
                if (!isRanging) {
                    return;
                }

                JSObject event = new JSObject();
                event.put("timestamp", System.currentTimeMillis());

                JSArray resultsArray = new JSArray();
                for (RangingResult result : results) {
                    JSObject resultObj = convertRangingResult(result);
                    resultsArray.put(resultObj);
                }

                event.put("results", resultsArray);
                notifyListeners("rttResults", event);
            }
        };

        // Execute ranging
        try {
            rttManager.startRanging(request, Executors.newSingleThreadExecutor(), callback);

            // Setup timeout
            rangingHandler.postDelayed(() -> {
                if (isRanging) {
                    isRanging = false;
                    notifyError("TIMEOUT", "Ranging timeout", null);
                }
            }, timeoutMs);

            // Setup interval if specified
            if (intervalMs > 0) {
                rangingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (isRanging) {
                            try {
                                rttManager.startRanging(request, Executors.newSingleThreadExecutor(), callback);
                                rangingHandler.postDelayed(this, intervalMs);
                            } catch (Exception e) {
                                notifyError("RANGING_FAILED", "Failed to restart ranging: " + e.getMessage(), null);
                            }
                        }
                    }
                };
                rangingHandler.postDelayed(rangingRunnable, intervalMs);
            }

            call.resolve();
        } catch (SecurityException e) {
            notifyError("PERMISSION_DENIED", "Location permission required", null);
            call.reject("Location permission required");
        }
    }

    @PluginMethod
    public void stopRanging(PluginCall call) {
        isRanging = false;

        if (rangingRunnable != null) {
            rangingHandler.removeCallbacks(rangingRunnable);
            rangingRunnable = null;
        }

        call.resolve();
    }

    private JSObject convertRangingResult(RangingResult result) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return new JSObject();
        }

        JSObject obj = new JSObject();

        String bssid = result.getMacAddress() != null ? result.getMacAddress().toString() : "unknown";
        obj.put("bssid", bssid);
        obj.put("mac", bssid);

        int status = result.getStatus();
        if (status == RangingResult.STATUS_SUCCESS) {
            obj.put("status", "OK");
            obj.put("distanceMm", result.getDistanceMm());
            obj.put("distanceStdDevMm", result.getDistanceStdDevMm());
            obj.put("rssi", result.getRssi());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                obj.put("numAttemptedMeasurements", result.getNumAttemptedMeasurements());
                obj.put("numSuccessfulMeasurements", result.getNumSuccessfulMeasurements());
            }
        } else {
            obj.put("status", "FAIL");
            obj.put("errorCode", status);

            String errorMessage = "Unknown error";
            switch (status) {
                case RangingResult.STATUS_FAIL:
                    errorMessage = "Ranging failed";
                    break;
                case RangingResult.STATUS_RESPONDER_DOES_NOT_SUPPORT_IEEE80211MC:
                    errorMessage = "Responder does not support IEEE 802.11mc";
                    break;
            }
            obj.put("errorMessage", errorMessage);
        }

        return obj;
    }

    private void notifyError(String code, String message, Integer details) {
        JSObject event = new JSObject();
        event.put("code", code);
        event.put("message", message);
        if (details != null) {
            event.put("details", details);
        }
        notifyListeners("rttError", event);
    }

    @Override
    protected void handleOnDestroy() {
        super.handleOnDestroy();

        if (wifiScanReceiver != null) {
            try {
                getContext().unregisterReceiver(wifiScanReceiver);
            } catch (Exception e) {
                // Receiver not registered
            }
        }

        if (rangingRunnable != null) {
            rangingHandler.removeCallbacks(rangingRunnable);
        }
    }
}

