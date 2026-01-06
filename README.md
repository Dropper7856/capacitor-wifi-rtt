# @donovandb/capacitor-wifi-rtt

Wifi RTT for Capacitor (Only Android)

## Install

```bash
npm install @donovandb/capacitor-wifi-rtt
npx cap sync
```

## API

<docgen-index>

* [`isSupported()`](#issupported)
* [`isWifiEnabled()`](#iswifienabled)
* [`startScan()`](#startscan)
* [`getScanResults()`](#getscanresults)
* [`startRanging(...)`](#startranging)
* [`stopRanging()`](#stopranging)
* [`addListener('rttResults', ...)`](#addlistenerrttresults-)
* [`addListener('rttError', ...)`](#addlistenerrtterror-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### isSupported()

```typescript
isSupported() => Promise<IsSupportedResult>
```

Check if Wi-Fi RTT is supported on this device

**Returns:** <code>Promise&lt;<a href="#issupportedresult">IsSupportedResult</a>&gt;</code>

--------------------


### isWifiEnabled()

```typescript
isWifiEnabled() => Promise<IsWifiEnabledResult>
```

Check if Wi-Fi is currently enabled

**Returns:** <code>Promise&lt;<a href="#iswifienabledresult">IsWifiEnabledResult</a>&gt;</code>

--------------------


### startScan()

```typescript
startScan() => Promise<StartScanResult>
```

Start a Wi-Fi scan to discover access points

**Returns:** <code>Promise&lt;<a href="#startscanresult">StartScanResult</a>&gt;</code>

--------------------


### getScanResults()

```typescript
getScanResults() => Promise<GetScanResultsResponse>
```

Get the results from the last Wi-Fi scan

**Returns:** <code>Promise&lt;<a href="#getscanresultsresponse">GetScanResultsResponse</a>&gt;</code>

--------------------


### startRanging(...)

```typescript
startRanging(options: StartRangingOptions) => Promise<void>
```

Start RTT ranging to specified access points

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#startrangingoptions">StartRangingOptions</a></code> |

--------------------


### stopRanging()

```typescript
stopRanging() => Promise<void>
```

Stop any ongoing RTT ranging

--------------------


### addListener('rttResults', ...)

```typescript
addListener(eventName: 'rttResults', listenerFunc: (event: RttResultsEvent) => void) => Promise<PluginListenerHandle>
```

Add a listener for RTT results

| Param              | Type                                                                            |
| ------------------ | ------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'rttResults'</code>                                                       |
| **`listenerFunc`** | <code>(event: <a href="#rttresultsevent">RttResultsEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('rttError', ...)

```typescript
addListener(eventName: 'rttError', listenerFunc: (event: RttErrorEvent) => void) => Promise<PluginListenerHandle>
```

Add a listener for RTT errors

| Param              | Type                                                                        |
| ------------------ | --------------------------------------------------------------------------- |
| **`eventName`**    | <code>'rttError'</code>                                                     |
| **`listenerFunc`** | <code>(event: <a href="#rtterrorevent">RttErrorEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

Remove all listeners for this plugin

--------------------


### Interfaces


#### IsSupportedResult

| Prop            | Type                 |
| --------------- | -------------------- |
| **`supported`** | <code>boolean</code> |
| **`reason`**    | <code>string</code>  |


#### IsWifiEnabledResult

| Prop          | Type                 |
| ------------- | -------------------- |
| **`enabled`** | <code>boolean</code> |


#### StartScanResult

| Prop          | Type                 |
| ------------- | -------------------- |
| **`started`** | <code>boolean</code> |


#### GetScanResultsResponse

| Prop          | Type                      |
| ------------- | ------------------------- |
| **`results`** | <code>ScanResult[]</code> |


#### ScanResult

| Prop                     | Type                 |
| ------------------------ | -------------------- |
| **`ssid`**               | <code>string</code>  |
| **`bssid`**              | <code>string</code>  |
| **`frequency`**          | <code>number</code>  |
| **`rssi`**               | <code>number</code>  |
| **`is80211mcResponder`** | <code>boolean</code> |
| **`channelWidth`**       | <code>number</code>  |
| **`timestamp`**          | <code>number</code>  |


#### StartRangingOptions

| Prop                   | Type                         |
| ---------------------- | ---------------------------- |
| **`targets`**          | <code>RangingTarget[]</code> |
| **`timeoutMs`**        | <code>number</code>          |
| **`scanFirst`**        | <code>boolean</code>         |
| **`resultIntervalMs`** | <code>number</code>          |


#### RangingTarget

| Prop        | Type                |
| ----------- | ------------------- |
| **`bssid`** | <code>string</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### RttResultsEvent

| Prop            | Type                        |
| --------------- | --------------------------- |
| **`timestamp`** | <code>number</code>         |
| **`results`**   | <code>RttResultDto[]</code> |


#### RttResultDto

| Prop                            | Type                        |
| ------------------------------- | --------------------------- |
| **`bssid`**                     | <code>string</code>         |
| **`status`**                    | <code>'OK' \| 'FAIL'</code> |
| **`distanceMm`**                | <code>number</code>         |
| **`distanceStdDevMm`**          | <code>number</code>         |
| **`rssi`**                      | <code>number</code>         |
| **`numAttemptedMeasurements`**  | <code>number</code>         |
| **`numSuccessfulMeasurements`** | <code>number</code>         |
| **`mac`**                       | <code>string</code>         |
| **`errorCode`**                 | <code>number</code>         |
| **`errorMessage`**              | <code>string</code>         |


#### RttErrorEvent

| Prop          | Type                |
| ------------- | ------------------- |
| **`code`**    | <code>string</code> |
| **`message`** | <code>string</code> |
| **`details`** | <code>any</code>    |

</docgen-api>
