export interface WifiRttPlugin {
  /**
   * Check if Wi-Fi RTT is supported on this device
   */
  isSupported(): Promise<IsSupportedResult>;

  /**
   * Check if Wi-Fi is currently enabled
   */
  isWifiEnabled(): Promise<IsWifiEnabledResult>;

  /**
   * Start a Wi-Fi scan to discover access points
   */
  startScan(): Promise<StartScanResult>;

  /**
   * Get the results from the last Wi-Fi scan
   */
  getScanResults(): Promise<GetScanResultsResponse>;

  /**
   * Start RTT ranging to specified access points
   */
  startRanging(options: StartRangingOptions): Promise<void>;

  /**
   * Stop any ongoing RTT ranging
   */
  stopRanging(): Promise<void>;

  /**
   * Add a listener for RTT results
   */
  addListener(
    eventName: 'rttResults',
    listenerFunc: (event: RttResultsEvent) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Add a listener for RTT errors
   */
  addListener(
    eventName: 'rttError',
    listenerFunc: (event: RttErrorEvent) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Remove all listeners for this plugin
   */
  removeAllListeners(): Promise<void>;
}

export interface IsSupportedResult {
  supported: boolean;
  reason?: string;
}

export interface IsWifiEnabledResult {
  enabled: boolean;
}

export interface StartScanResult {
  started: boolean;
}

export interface ScanResult {
  ssid?: string;
  bssid: string;
  frequency: number;
  rssi: number;
  is80211mcResponder: boolean;
  channelWidth?: number;
  timestamp?: number;
}

export interface GetScanResultsResponse {
  results: ScanResult[];
}

export interface RangingTarget {
  bssid: string;
}

export interface StartRangingOptions {
  targets: RangingTarget[];
  timeoutMs?: number;
  scanFirst?: boolean;
  resultIntervalMs?: number;
}

export interface RttResultDto {
  bssid: string;
  status: 'OK' | 'FAIL';
  distanceMm?: number;
  distanceStdDevMm?: number;
  rssi?: number;
  numAttemptedMeasurements?: number;
  numSuccessfulMeasurements?: number;
  mac?: string;
  errorCode?: number;
  errorMessage?: string;
}

export interface RttResultsEvent {
  timestamp: number;
  results: RttResultDto[];
}

export interface RttErrorEvent {
  code: string;
  message: string;
  details?: any;
}

export interface PluginListenerHandle {
  remove(): Promise<void>;
}
